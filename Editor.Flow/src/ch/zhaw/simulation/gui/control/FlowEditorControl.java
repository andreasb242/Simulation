package ch.zhaw.simulation.gui.control;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXStatusBar;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.util.RestartUtil;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.dialog.overview.OverviewWindow;
import ch.zhaw.simulation.dialog.settings.SettingsDlg;
import ch.zhaw.simulation.dialog.snapshot.SnapshotDialog;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ConnectorPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.global.GlobalView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.view.TextView;
import ch.zhaw.simulation.filehandling.ImportPlugins;
import ch.zhaw.simulation.filehandling.LoadSaveHandler;
import ch.zhaw.simulation.gui.FlowEditorView;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.math.Autoparser;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionListener;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.listener.SimulationAdapter;
import ch.zhaw.simulation.sim.SimulationManager;
import ch.zhaw.simulation.sim.SimulationPlugin;
import ch.zhaw.simulation.sim.StandardParameter;
import ch.zhaw.simulation.undo.action.flow.AddConnectorUndoAction;
import ch.zhaw.simulation.undo.action.flow.AddNamedSimulationUndoAction;
import ch.zhaw.simulation.undo.action.flow.DeleteUndoAction;
import ch.zhaw.simulation.window.flow.FlowWindow;

public class FlowEditorControl extends AbstractEditorControl<SimulationFlowModel>{

	private FlowEditorView view;

	private JXStatusBar sBar = new JXStatusBar();
	private JLabel lbStatus = new JLabel(" ");
	private boolean emptyStatus = true;

	private LoadSaveHandler savehandler;

	private Vector<DrawModusListener> drawModusListener = new Vector<DrawModusListener>();

	private MouseAdapter lastMouseListener;

	private SettingsDlg settigsDialog;
	private Autoparser autoparser;

	private SimulationManager manager;
	private SimulationApplication app;

	private ImportPlugins importPlugins;

	private void initPlugins() {
		importPlugins = new ImportPlugins(this.settings);

	}

	public ImportPlugins getImportPlugins() {
		return importPlugins;
	}

	public FlowEditorControl(SimulationApplication app, FlowWindow parent, Settings settings) {
		super(parent, settings);
		this.app = app;

		if (app == null) {
			throw new NullPointerException("app == null");
		}

		model = new SimulationFlowModel();
		manager = new SimulationManager(settings, model.getSimulationConfiguration(), parent);
		loadSimulationParameterFromSettings();

		initPlugins();

		savehandler = new LoadSaveHandler(this);

		initMetadata();

		initStatusBar();

		model.addListener(new SimulationAdapter() {
			@Override
			public void dataChanged(SimulationObject o) {
				updateTitle();
			}

			@Override
			public void dataSaved(boolean saved) {
				updateTitle();
			}
		});

		addListeners();

		autoparser = new Autoparser(this);
	}

	private void loadSimulationParameterFromSettings() {
		SimulationConfiguration conf = model.getSimulationConfiguration();

		int prefixLen = StandardParameter.SIM_PROPERTY_STRING_PREFIX.length();
		for (String k : settings.getKeysStartingWith(StandardParameter.SIM_PROPERTY_STRING_PREFIX)) {
			conf.setParameter(k.substring(prefixLen), settings.getSetting(k));
		}

		prefixLen = StandardParameter.SIM_PROPERTY_DOUBLE_PREFIX.length();
		for (String k : settings.getKeysStartingWith(StandardParameter.SIM_PROPERTY_DOUBLE_PREFIX)) {
			try {
				double d = Double.parseDouble(settings.getSetting(k));
				conf.setParameter(k.substring(prefixLen), d);
			} catch (Exception e) {
				System.err.println("Invalid double setting: \"" + k + "\" = \"" + settings.getSetting(k) + "\"");
			}
		}
	}

	public void stopAutoparser() {
		autoparser.stop();
	}

	public void startAutoparser() {
		autoparser.start();
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		Vector<NamedSimulationObject> removedObjects = new Vector<NamedSimulationObject>();
		Vector<Connector<?>> removedConnectors = new Vector<Connector<?>>();
		Vector<InfiniteData> removedInfinite = new Vector<InfiniteData>();

		Vector<Connector<?>> tmpRemovedConnectors = new Vector<Connector<?>>();
		for (SelectableElement el : elements) {
			if (el instanceof FlowConnectorParameter) {
				FlowConnector c = ((FlowConnectorParameter) el).getConnector();

				tmpRemovedConnectors.add(c);

				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(c.getValve()));
			} else if (el instanceof ConnectorPoint) {
				tmpRemovedConnectors.add(((ConnectorPoint) el).getConnector());
			}
		}

		addConnectors(removedConnectors, removedInfinite, tmpRemovedConnectors);

		for (SelectableElement el : elements) {
			if (el instanceof ParameterView || el instanceof ContainerView) {
				GuiDataTextElement<?> control = (GuiDataTextElement<?>) el;
				NamedSimulationObject data = (NamedSimulationObject) control.getData();

				removedObjects.add(data);
				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(data));
			}
		}
		for (SelectableElement el : elements) {
			if (el instanceof InfiniteSymbol) {
				addInfiniteData(removedInfinite, ((InfiniteSymbol) el).getData());
				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(((InfiniteSymbol) el).getData()));
			}
		}

		getUndoManager().addEdit(new DeleteUndoAction(removedObjects, removedConnectors, removedInfinite, this));
	}

	private void addInfiniteData(Vector<InfiniteData> removedInfinite, InfiniteData d) {
		if (!removedInfinite.contains(d)) {
			removedInfinite.add(d);
		}
	}

	public void selectAll() {
		for (Component c : view.getComponents()) {
			if (c instanceof SelectableElement) {
				selectionModel.addSelectedInt((SelectableElement) c);
			}
		}

		selectionModel.fireSelectionChanged();
	}

	private void addConnectors(Vector<Connector<?>> removedConnectors, Vector<InfiniteData> removedInfinite, Vector<Connector<?>> add) {
		for (Connector<?> c : add) {
			if (!removedConnectors.contains(c)) {
				removedConnectors.add(c);

				if (c instanceof FlowConnector) {
					if (c.getSource() instanceof InfiniteData) {
						addInfiniteData(removedInfinite, (InfiniteData) c.getSource());
					}
					if (c.getTarget() instanceof InfiniteData) {
						addInfiniteData(removedInfinite, (InfiniteData) c.getTarget());
					}
				}
			}
		}
	}

	private void addListeners() {
		model.addListener(new SimulationAdapter() {

			@Override
			public void dataRemoved(SimulationObject o) {
				clearStatus();
			}

			@Override
			public void dataChanged(SimulationObject o) {
				clearStatus();
			}

			@Override
			public void dataAdded(SimulationObject o) {
				clearStatus();
			}

			@Override
			public void connectorChanged(Connector<?> c) {
				clearStatus();
			}

			@Override
			public void connectorRemoved(Connector<?> c) {
				clearStatus();
			}

			@Override
			public void connectorAdded(Connector<?> c) {
				clearStatus();
			}
		});

		// Muss als erstes aufgerufen werden => als erstes hinzufügen
		selectionModel.addSelectionListener(new SelectionListener() {
			@Override
			public void selectionMoved(int dX, int dY) {
			}

			@Override
			public void selectionChanged() {
				SelectableElement elem = selectionModel.getSingleSelectedElement();
				if (elem == null) {
					selectionModel.clearDependentElement();
					return;
				}

				Vector<SimulationObject> shadowData = new Vector<SimulationObject>();

				if (elem instanceof GlobalView) {
					SimulationGlobal g = ((GlobalView) elem).getData();

					for (SimulationObject d : model.getData()) {
						Vector<SimulationGlobal> usedGlobals = d.getUsedGlobals();
						if (usedGlobals != null && usedGlobals.contains(g)) {
							shadowData.add(d);
						}
					}
				}

				if (elem instanceof GuiDataElement<?>) {
					SimulationObject d = ((GuiDataElement<?>) elem).getData();

					Vector<SimulationGlobal> globals = d.getUsedGlobals();
					if (globals != null) {
						shadowData.addAll(globals);
					}
				}

				Vector<SelectableElement> elements = view.convertToSelectable(shadowData);

				selectionModel.setDependentElement(elements);
			}
		});
	}

	public void initMetadata() {
		model.clearMetadata();
		updateMetadata("orig");
		updateLastMetadata();
	}

	private void updateLastMetadata() {
		updateMetadata("last");
	}

	private void updateMetadata(String prefix) {
		model.putMetainf(prefix + ".author", System.getProperty("user.name"));
		model.putMetainf(prefix + ".os", System.getProperty("os.name"));
		model.putMetainf(prefix + ".os.version", System.getProperty("os.version"));
		model.putMetainf(prefix + ".os.desktop", System.getProperty("sun.desktop"));

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

		model.putMetainf(prefix + ".date", sdf.format(cal.getTime()));
	}

	private void initStatusBar() {
		sBar.add(lbStatus);
	}

	public void setStatusText(String text) {
		lbStatus.setText(text);
		emptyStatus = false;
	}

	public void setStatusTextInfo(String text) {
		setStatusText(text);
		lbStatus.setIcon(IconSVG.getIcon("info", 22));
		emptyStatus = false;
	}

	public void clearStatus() {
		if (emptyStatus) {
			return;
		}
		lbStatus.setIcon(null);
		setStatusText(" ");
		emptyStatus = true;
	}

	void cancelAllActions() {
		if (lastMouseListener != null) {
			view.removeMouseListener(lastMouseListener);
			lastMouseListener = null;
		}
		view.cancelAddArrow();
	}

	void addComponent(final NamedSimulationObject so, String type) {
		setStatusTextInfo("Ins Dokument klicken um " + type + " einzufügen");

		lastMouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				so.setX((int) e.getPoint().getX() - so.getWidth() / 2);
				so.setY((int) e.getPoint().getY() - so.getHeight() / 2);
				clearStatus();
				view.removeMouseListener(this);

				lastMouseListener = null;

				getUndoManager().addEdit(new AddNamedSimulationUndoAction(so, model));

				postAddAction(so);
			}
		};

		view.addMouseListener(lastMouseListener);
	}

	private void postAddAction(NamedSimulationObject so) {
		if (so instanceof TextData) {
			TextData data = (TextData) so;
			for (Component c : view.getComponents()) {
				if (c instanceof TextView) {
					if (((TextView) c).getData() == data) {
						((TextView) c).showTextEditor();
					}
				}
			}
		}
	}

	public JXStatusBar getStatusBar() {
		return sBar;
	}

	public FlowEditorView getView() {
		return view;
	}

	public boolean save() {
		updateLastMetadata();
		if (!savehandler.save()) {
			return false;
		}

		updatePaths();
		return true;
	}

	private void updatePaths() {
		if (savehandler.getPath() != null) {
			recentMenu.addFile(savehandler.getPath().getAbsolutePath());
			setDocumentTitle(savehandler.getPath().getName());
		}
	}

	public boolean saveAs() {
		updateLastMetadata();
		if (!savehandler.saveAs()) {
			return false;
		}
		updatePaths();

		return true;
	}

	public void open(String path) {
		if (askSave() == true) {
			if (savehandler.open(new File(path))) {
				selectionModel.clearSelection();
				updatePaths();
			}
		}
	}

	public void open() {
		if (askSave() == true) {
			if (savehandler.open()) {
				selectionModel.clearSelection();
				updatePaths();
			}
		}
	}

	public void about() {
		this.app.showAboutDialog();
	}

	public void newFile() {
		if (askSave() == true) {
			model.clear();
			initMetadata();
			selectionModel.clearSelection();
			setDocumentTitle(null);
			setStatusText("Neues Dokument erstellt");
			savehandler.clear();
			model.setSaved();

			getUndoManager().discardAllEdits();
		}
	}

	public void fireDrawModusFinished() {
		for (DrawModusListener l : drawModusListener) {
			l.drawModusFinished();
		}
	}

	public void fireDrawModusEnabled() {
		for (DrawModusListener l : drawModusListener) {
			l.drawModusEnabled();
		}
	}

	public void addDrawModusListener(DrawModusListener l) {
		drawModusListener.add(l);
	}

	public void removeDrawModusListener(DrawModusListener l) {
		drawModusListener.remove(l);
	}

	public void addConnector(Connector<?> c) {
		getUndoManager().addEdit(new AddConnectorUndoAction(c, model));
	}

	public void settings() {
		if (settigsDialog == null) {
			settigsDialog = new SettingsDlg(this);
		}
		settigsDialog.setVisible(true);
	}

	public void startSimulation() {
		SimulationConfiguration simulationConfiguration = getModel().getSimulationConfiguration();
		String plugin = simulationConfiguration.getPlugin();

		if (plugin == null) {
			Messagebox.showError(getParent(), "Kein Plugin gewählt", "Bitte wählen Sie in der Sidebar mit welchem Plugin simuliert werden soll");
			return;
		}

		PluginDescription<SimulationPlugin> selectedPlugin = null;
		for (PluginDescription<SimulationPlugin> p : manager.getPlugins()) {
			if (plugin.equalsIgnoreCase(p.getName())) {
				selectedPlugin = p;
				break;
			}
		}

		if (selectedPlugin == null) {
			Messagebox.showError(getParent(), "Plugin nicht gefunden", "Bitte wählen Sie in der Sidebar mit welchem Plugin simuliert werden soll");
			return;
		}

		SimulationPlugin handler = selectedPlugin.getPlugin();

		try {
			handler.checkModel(getModel());
		} catch (SimulationModelException ex) {
			Messagebox.showError(getParent(), "Simulation nicht möglich", ex.getMessage());

			view.selectElement(ex.getSimObject());

			ex.printStackTrace();
			return;
		}

		try {
			handler.prepareSimulation(getModel());
		} catch (Exception e) {
			Errorhandler.showError(e, "Simulation fehlgeschlagen");
		}
	}

	public void addParameter() {
		cancelAllActions();
		addComponent(new SimulationParameter(0, 0), "Parameter");
	}

	public void addContainer() {
		cancelAllActions();
		addComponent(new SimulationContainer(0, 0), "Container");
	}

	public void addGlobal() {
		cancelAllActions();
		addComponent(new SimulationGlobal(0, 0), "Global");
	}

	public void addText() {
		cancelAllActions();
		addComponent(new TextData(0, 0), "Text");
	}

	public void takeSnapshot() {
		SnapshotDialog dlg = new SnapshotDialog(getParent(), getSysintegration(), view, view.getBounds());
		dlg.setVisible(true);
	}

	public SimulationManager getManager() {
		return manager;
	}

	@Override
	public void menuActionPerformed(MenuToolbarAction action) {
		switch (action.getType()) {

		case FLOW_ADD_CONTAINER:
			addContainer();
			break;
		case FLOW_ADD_GLOBAL:
			addGlobal();
			break;
		case FLOW_ADD_PARAMETER:
			addParameter();
			break;
		case FLOW_ADD_TEXT:
			addText();
			break;

		case FLOW_ADD_CONNECTOR:
			cancelAllActions();
			getSelectionModel().clearSelection();
			getView().addConnectArrow();
		
		case FLOW_ADD_FLOW:
			cancelAllActions();
			getSelectionModel().clearSelection();
			getView().addFlowArrow();
			break;

		case NEW_FILE:
			this.newFile();
			break;

		case OPEN_FILE:
			if (action.getData() != null) {
				this.open(action.getData().toString());
			} else {
				this.open();
			}
			break;

		case EXIT:
			this.exit();
			break;

		case FORMULA_OVERVIEW:
			OverviewWindow w = new OverviewWindow(getParent(), getClipboard(), getModel(), getSysintegration().getGuiConfig());
			w.setVisible(true);
			break;

		case HELP:
			this.help();
			break;

		case ABOUT:
			this.about();
			break;

		case LOOK_AND_FEEL_CHANGED:
			setLookAndFeel(action.getData().toString());
			break;

		case DELETE_SELECTION:
			this.deleteSelected();
			break;

		case SETTINGS:
			this.settings();
			break;

		case START_SIMULATION:
			startSimulation();
			break;

		case SAVE:
			this.save();
			break;

		case SAVE_AS:
			this.saveAs();
			break;

		case SNAPSHOT:
			takeSnapshot();
			break;

		case SELECT_ALL:
			selectAll();
			break;

		case SHOW_MATH_CONSOLE:
			this.app.showMathConsole();
			break;

		default:
			throw new InvalidParameterException("SimulationControl.menuActionPerformed unimplemented action");
		}
	}

	private void setLookAndFeel(String lookAndFeel) {
		settings.setSetting("ui.look-and-feel", lookAndFeel);

		if (this.exit()) {
			if (!RestartUtil.restartApplication("startup.Startup")) {
				Messagebox msg = new Messagebox(null, "Neu Starten", "<html>Das Programm konnte nicht neu gstartet werden.<br>"
						+ "Es wird jetzt beendet, bitte starten Sie es manuell neu.</html>", Messagebox.ERROR);
				msg.addButton("OK", 0);
				msg.display();
			}
		}

	}

	public void setView(FlowEditorView view) {
		this.view = view;
	}
}
