package ch.zhaw.simulation.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.util.OS;

import butti.javalibs.gui.messagebox.Messagebox;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.editor.layouting.Layouting;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.simulation.PluginChangeListener;
import ch.zhaw.simulation.plugin.SimulationManager;
import ch.zhaw.simulation.plugin.SimulationPlugin;
import ch.zhaw.simulation.toolbar.AbstractToolbar;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.window.sidebar.SimulationConfigurationPanel;
import ch.zhaw.simulation.window.sidebar.config.ConfigurationSidebarPanel;

/**
 * A simulation window
 * 
 * @author Andreas Butti
 * 
 * @param <M>
 *            The model type
 * @param <T>
 *            The toolbar type
 * @param <V>
 *            The view type
 */
public abstract class SimulationWindow<M extends AbstractMenubar, T extends AbstractToolbar, V extends AbstractEditorView<?>> extends LockFrame implements
		MenuActionListener, PluginChangeListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Listener for menu action
	 */
	private Vector<MenuActionListener> listeners = new Vector<MenuActionListener>();

	/**
	 * The sidebar
	 */
	private FrameSidebar sidebar = new FrameSidebar();

	/**
	 * Undo / Redo handler
	 */
	protected UndoHandler um = new UndoHandler();

	/**
	 * Used for layouting elements
	 */
	private Layouting layouter;

	protected M menubar;
	protected T toolbar;
	protected V view;

	/**
	 * To remove the taskbar if the plugin changes
	 */
	private JXTaskPane lastPluginTaskbar = null;

	/**
	 * If this is the main application window
	 */
	protected boolean mainWindow;

	private SimulationDocument doc;

	private SimulationConfigurationPanel simConfig;

	protected ConfigurationSidebarPanel<?, ?> configurationSidebar;

	private JPanel pConentents = new JPanel();
	
	/**
	 * @param mainWindow
	 *            If this is the main application window
	 */
	public SimulationWindow(boolean mainWindow) {
		this.mainWindow = mainWindow;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(IconLoader.getIcon("simulation", 128).getImage());

		getPanel().setLayout(new BorderLayout());
		getPanel().add(pConentents, BorderLayout.CENTER);

		pConentents.setLayout(new BorderLayout());
		pConentents.add(sidebar.getPanel(), BorderLayout.EAST);
		
		if (OS.isMacOSX()) {
			pConentents.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x515151)));
		}
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (SimulationWindow.this.mainWindow) {
					fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.EXIT));
				} else {
					fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.CLOSE));
				}
			}

		});
	}

	protected void init(M menubar, T toolbar, V view) {
		this.menubar = menubar;
		this.toolbar = toolbar;
		this.view = view;
		this.layouter = new Layouting(view.getControl().getSelectionModel(), view.getUndoHandler(), view);
		this.doc = view.getControl().getDoc();

		doc.getSimulationConfiguration().addPluginChangeListener(this);

		setJMenuBar(menubar.getMenubar());

		this.menubar.addListener(this);
		this.toolbar.addListener(this);

		view.getUndoHandler().addUndoListener(toolbar);
		view.getClipboard().addListener(toolbar);

		JScrollPane pCenter = new JScrollPane(view);
		if (OS.isMacOSX()) {
			pCenter.setBorder(null);
		}

		getPanel().add(toolbar.getToolbar(), BorderLayout.NORTH);
		pConentents.add(pCenter, BorderLayout.CENTER);
		getPanel().add(view.getControl().getStatus().getStatusBar(), BorderLayout.SOUTH);

		initSidebar(sidebar);
	}

	/**
	 * Initialize the sidebar contents
	 * 
	 * @param sidebar
	 *            The sidebar
	 */
	protected void initSidebar(FrameSidebar sidebar) {
		initElementConfigurationSiebar();

		sidebar.add(configurationSidebar);
		view.getControl().getSelectionModel().addSelectionListener(configurationSidebar);

		this.simConfig = new SimulationConfigurationPanel(view.getControl());
		sidebar.add(simConfig);
	}

	protected abstract void initElementConfigurationSiebar();

	@Override
	public void menuActionPerformed(MenuToolbarAction action) {
		switch (action.getType()) {

		case COPY:
			view.getClipboard().copy();
			return;

		case CUT:
			view.getClipboard().cut();
			return;

		case PASTE:
			view.getClipboard().paste();
			return;

		case UNDO:
			view.getUndoHandler().undo();
			return;

		case REDO:
			view.getUndoHandler().redo();
			return;

		case LAYOUT_BOTTOM:
			this.layouter.layoutBottom();
			return;

		case LAYOUT_LEFT:
			this.layouter.layoutLeft();
			return;

		case LAYOUT_RIGHT:
			this.layouter.layoutRight();
			return;

		case LAYOUT_TOP:
			this.layouter.layoutTop();
			return;

		case LAYOUT_CENTER_HORIZONTAL:
			this.layouter.layoutCenterHorizontal();
			return;

		case LAYOUT_CENTER_VERTICAL:
			this.layouter.layoutCenterVertical();
			return;

		case SHOW_SIDEBAR:
			this.sidebar.getPanel().setVisible((Boolean) action.getData());
			return;
		}

		fireMenuActionPerformed(action);
	}

	public void addListener(MenuActionListener l) {
		listeners.add(l);
	}

	public void removeListener(MenuActionListener l) {
		listeners.remove(l);
	}

	public void fireMenuActionPerformed(MenuToolbarAction a) {
		for (int i = 0; i < listeners.size(); i++) {
			MenuActionListener l = listeners.get(i);
			l.menuActionPerformed(a);
		}
	}

	@Override
	public void dispose() {
		if (view != null) {
			view.getUndoHandler().removeUndoListener(toolbar);
			view.getClipboard().removeListener(toolbar);
		}

		if (doc != null) {
			doc.getSimulationConfiguration().removePluginChangeListener(this);
		}

		if (view != null) {
			if (view.getControl().getSelectionModel() != null) {
				view.getControl().getSelectionModel().removeSelectionListener(configurationSidebar);
			}

			view.dispose();
			view = null;
		}

		this.simConfig.dispose();

		configurationSidebar.dispose();

		super.dispose();
	}

	public V getView() {
		return view;
	}

	public T getToolbar() {
		return toolbar;
	}

	public ConfigurationSidebarPanel<?, ?> getConfigurationSidebar() {
		return configurationSidebar;
	}

	@Override
	public void pluginChanged(String pluginName) {
		SimulationManager manager = view.getControl().getApp().getManager();
		PluginDescription<SimulationPlugin> pluginDescription = manager.getSelectedPluginDescription();
		if (pluginDescription == null) {
			Messagebox.showWarning(this, "Simulation", "Plugin «" + pluginName + "» konnte nicht selektiert werden!");

			return;
		}

		if (lastPluginTaskbar != null) {
			this.sidebar.remove(lastPluginTaskbar);
		}

		JXTaskPane sb = pluginDescription.getPlugin().getConfigurationSidebar(view.getSimulationType());
		if (sb != null) {
			this.sidebar.add(sb);
			lastPluginTaskbar = sb;
		}
	}
}
