package ch.zhaw.simulation.window;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.layouting.Layouting;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.toolbar.AbstractToolbar;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;

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
public abstract class SimulationWindow<M extends AbstractMenubar, T extends AbstractToolbar, V extends AbstractEditorView<?>> extends JFrame implements
		MenuActionListener {
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
	 * If this is the main application window
	 */
	protected boolean mainWindow;
	
	/**
	 * @param mainWindow
	 *            If this is the main application window
	 */
	public SimulationWindow(boolean mainWindow) {
		this.mainWindow = mainWindow;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(IconSVG.getIcon("simulation", 128).getImage());

		add(BorderLayout.EAST, sidebar);

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

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	protected void init(M menubar, T toolbar, V view) {
		this.menubar = menubar;
		this.toolbar = toolbar;
		this.view = view;
		this.layouter = new Layouting(view.getControl().getSelectionModel(), view.getUndoHandler());
		
		setJMenuBar(menubar.getMenubar());

		this.menubar.addListener(this);
		this.toolbar.addListener(this);

		view.getUndoHandler().addUndoListener(toolbar);
		view.getClipboard().addListener(toolbar);

		add(BorderLayout.NORTH, toolbar.getToolbar());
		add(BorderLayout.CENTER, view);
		add(BorderLayout.SOUTH, view.getControl().getStatus().getStatusBar());

		initSidebar(sidebar);
	}

	/**
	 * Initialize the sidebar contents
	 * 
	 * @param sidebar
	 *            The sidebar
	 */
	protected void initSidebar(FrameSidebar sidebar) {
		final AbstractEditorControl<?> control = view.getControl();

		NameFormulaConfiguration formulaConfiguration = new NameFormulaConfiguration(control.getModel(), control.getSelectionModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void showFormulaEditor(NamedSimulationObject data) {
				control.showFormulaEditor(getData());
			}
			
		};
		sidebar.add(formulaConfiguration);
		view.getControl().getSelectionModel().addSelectionListener(formulaConfiguration);
	}

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
			this.sidebar.setVisible((Boolean)action.getData());
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
		for (MenuActionListener l : listeners) {
			l.menuActionPerformed(a);
		}
	}
}
