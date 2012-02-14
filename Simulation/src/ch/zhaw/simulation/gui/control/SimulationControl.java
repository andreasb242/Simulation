package ch.zhaw.simulation.gui.control;


import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;


import org.jdesktop.swingx.JXStatusBar;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.dialog.settings.SettingsDlg;
import ch.zhaw.simulation.dialog.snapshot.SnapshotDialog;
import ch.zhaw.simulation.editor.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.connector.parameterarrow.ConnectorPoint;
import ch.zhaw.simulation.editor.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.editor.elements.GuiDataTextElement;
import ch.zhaw.simulation.editor.elements.TextView;
import ch.zhaw.simulation.editor.elements.container.ContainerView;
import ch.zhaw.simulation.editor.elements.global.GlobalView;
import ch.zhaw.simulation.editor.elements.parameter.ParameterView;
import ch.zhaw.simulation.filehandling.ImportPlugins;
import ch.zhaw.simulation.filehandling.LoadSaveHandler;
import ch.zhaw.simulation.gui.DocumentView;
import ch.zhaw.simulation.gui.Menubar;
import ch.zhaw.simulation.gui.RecentMenu;
import ch.zhaw.simulation.gui.configuration.codeditor.FormulaEditor;
import ch.zhaw.simulation.help.gui.HelpFrame;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.math.Autoparser;
import ch.zhaw.simulation.model.InfiniteData;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationAdapter;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.TextData;
import ch.zhaw.simulation.model.connection.Connector;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.model.simulation.SimulationModel;
import ch.zhaw.simulation.sim.SimulationManager;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.action.AddConnectorUndoAction;
import ch.zhaw.simulation.undo.action.AddNamedSimulationUndoAction;
import ch.zhaw.simulation.undo.action.DeleteUndoAction;

public class SimulationControl {
	private GuiConfig config = new GuiConfig();
	private SimulationDocument model = new SimulationDocument();
	private Settings settings;

	private DocumentView view;

	private JXStatusBar sBar = new JXStatusBar();
	private JLabel lbStatus = new JLabel(" ");
	private boolean emptyStatus = true;

	private RecentMenu recentMenu;

	private JFrame parent;

	private String documentName = null;

	private ImportPlugins importPlugins;
	private LoadSaveHandler savehandler;
	private SelectionModel selectionModel = new SelectionModel();

	private Vector<DrawModusListener> drawModusListener = new Vector<DrawModusListener>();

	private Vector<SidebarListener> sidebarListener = new Vector<SidebarListener>();
	private boolean sidebarVisible = true;

	private MouseAdapter lastMouseListener;

	private UndoHandler undoHandler = new UndoHandler();

	private HelpFrame helpFrame;

	private FormulaEditor formulaEditor;

	private Sysintegration integration;

	Menubar mb;
	private SettingsDlg settigsDialog;
	private Autoparser autoparser;

	private MainToolbar toolbar;

	private FunctionHelp functionHelp;
	private SimulationSettingsSaver simulationsSettings;

	private ClipboardHandler clipboard = new ClipboardHandler(this);

	private SimulationManager manager;
	
	public SimulationControl(JFrame parent, Settings settings) {
		this.parent = parent;
		this.settings = settings;

		if (settings == null) {
			throw new NullPointerException("settings == null");
		}

		manager = new SimulationManager(settings);
		
		importPlugins = new ImportPlugins(settings);
		savehandler = new LoadSaveHandler(this);

		integration = SysintegrationFactory.createSysintegration();
		toolbar = new MainToolbar(this);
		functionHelp = new FunctionHelp();

		mb = new Menubar(this);

		recentMenu = new RecentMenu(this);

		this.view = new DocumentView(this);
		integration.initJComponnent(view);
		toolbar.initToolbar();
		initJcomponent(toolbar.getToolbar());

		mb.initMenusToolbar(recentMenu.getMenu());

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
		simulationsSettings = new SimulationSettingsSaver(model.getSimModel(), settings);
		simulationsSettings.load();
	}

	public void initJcomponent(JComponent c) {
		integration.initJComponnent(c);
	}

	public FunctionHelp getFunctionHelp() {
		return functionHelp;
	}

	public void stopAutoparser() {
		autoparser.stop();
	}

	public void startAutoparser() {
		autoparser.start();
	}

	public void openLastFile() {
		if (settings.isSetting("autoloadLastDocument", false)) {
			String path = recentMenu.getNewstEntry();
			if (path != null) {
				File f = new File(path);
				if (f.exists() && f.canRead()) {
					open(path);
				}
			}
		}
	}

	public Sysintegration getSysintegration() {
		return integration;
	}

	public void undo() {
		undoHandler.undo();
	}

	public void redo() {
		undoHandler.redo();
	}

	public void deleteSelected() {
		SelectableElement[] selected = selectionModel.getSelected();
		selectionModel.clearSelection();

		delete(selected);
	}

	private void delete(SelectableElement[] selected) {
		Vector<NamedSimulationObject> removedObjects = new Vector<NamedSimulationObject>();
		Vector<Connector<?>> removedConnectors = new Vector<Connector<?>>();
		Vector<InfiniteData> removedInfinite = new Vector<InfiniteData>();

		Vector<Connector<?>> tmpRemovedConnectors = new Vector<Connector<?>>();
		for (SelectableElement el : selected) {
			if (el instanceof FlowConnectorParameter) {
				FlowConnector c = ((FlowConnectorParameter) el).getConnector();

				tmpRemovedConnectors.add(c);

				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(c.getParameterPoint()));
			} else if (el instanceof ConnectorPoint) {
				tmpRemovedConnectors.add(((ConnectorPoint) el).getConnector());
			}
		}

		addConnectors(removedConnectors, removedInfinite, tmpRemovedConnectors);

		for (SelectableElement el : selected) {
			if (el instanceof ParameterView || el instanceof ContainerView) {
				GuiDataTextElement<?> control = (GuiDataTextElement<?>) el;
				NamedSimulationObject data = (NamedSimulationObject) control.getData();

				removedObjects.add(data);
				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(data));
			}
		}
		for (SelectableElement el : selected) {
			if (el instanceof InfiniteSymbol) {
				addInfiniteData(removedInfinite, ((InfiniteSymbol) el).getData());
				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(((InfiniteSymbol) el).getData()));
			}
		}

		undoHandler.addEdit(new DeleteUndoAction(removedObjects, removedConnectors, removedInfinite, this));
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

	public UndoHandler getUndoManager() {
		return undoHandler;
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

				// TODO: Debug
				// for (Component c : view.getComponents()) {
				// if (c instanceof SelectableElement) {
				// elements.add((SelectableElement) c);
				// }
				// }

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

	public void setDocumentTitle(String name) {
		documentName = name;
		updateTitle();
	}

	private void updateTitle() {
		if (documentName == null) {
			parent.setTitle("Simulation");
		} else {
			String saved = "";
			if (model.isChanged()) {
				saved = " *";
			}
			parent.setTitle(documentName + saved + " - Simulation");
		}
	}

	public String getDocumentName() {
		return documentName;
	}

	public void showFormulaEditor(NamedSimulationObject data) {
		if (formulaEditor == null) {
			formulaEditor = new FormulaEditor(parent, this);
		}

		formulaEditor.setData(data);
		formulaEditor.setVisible(true);
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

				undoHandler.addEdit(new AddNamedSimulationUndoAction(so, model));

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

	public GuiConfig getConfig() {
		return config;
	}

	public JComponent getToolbar() {
		return toolbar.getToolbar();
	}

	public JXStatusBar getStatusBar() {
		return sBar;
	}

	public JMenuBar getMenuBar() {
		return mb.getMenubar();
	}

	public SimulationDocument getModel() {
		return model;
	}

	public DocumentView getView() {
		return view;
	}

	public Settings getSettings() {
		return settings;
	}

	public boolean save() {
		updateLastMetadata();
		if (!savehandler.save()) {
			return false;
		}

		updatePaths();
		return true;
	}
	
	public ImportPlugins getImportPlugins() {
		return importPlugins;
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

	public void newFile() {
		if (askSave() == true) {
			model.clear();
			initMetadata();
			selectionModel.clearSelection();
			setDocumentTitle(null);
			setStatusText("Neues Dokument erstellt");
			savehandler.clear();
			model.setSaved();

			undoHandler.discardAllEdits();
		}
	}

	public boolean exit() {
		if (askSave() == true) {
			parent.setVisible(false);
			parent.dispose();

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Window[] windows = Window.getWindows();
					if (windows.length > 0) {
						System.err.println("Open windows (disposing now):");
						for (Window frame : windows) {
							System.err.println(frame.getName() + ": " + frame.getClass());
							frame.dispose();
						}
					}
				}
			});

			return true;
		}

		return false;
	}

	private boolean askSave() {
		if (model.isChanged()) {
			Messagebox msg = new Messagebox(parent, "Aktuelles Dokument", "Soll das aktuelle Dokument verworfen werden?", Messagebox.QUESTION);
			msg.addButton("Abbrechen", 0);
			msg.addButton("Verwerfen", 1);
			msg.addButton("Speichern", 2, true);

			int result = msg.display();
			if (result == 1) {
				return true;
			}
			if (result == 2) {
				return save();
			}

			return false;
		}
		return true;
	}

	public void help() {
		if (helpFrame == null) {
			helpFrame = new HelpFrame(parent);
		}
		helpFrame.setVisible(true);
	}

	public void about() {
		AboutDialog dlg = new AboutDialog(parent);
		dlg.setVisible(true);
	}

	public JFrame getParent() {
		return parent;
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
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

	public void fireSidebarVisible(boolean visible) {
		for (SidebarListener l : sidebarListener) {
			l.showSidebar(visible);
		}
	}

	public void addSidebarListener(SidebarListener l) {
		sidebarListener.add(l);
	}

	public void removeSidebarListener(SidebarListener l) {
		sidebarListener.remove(l);
	}

	public void addConnector(Connector<?> c) {
		undoHandler.addEdit(new AddConnectorUndoAction(c, model));
	}

	public ClipboardHandler getClipboard() {
		return clipboard;
	}

	public void settings() {
		if (settigsDialog == null) {
			settigsDialog = new SettingsDlg(this);
		}
		settigsDialog.setVisible(true);
	}

	public void startSimulation() {
//		Simulation s = new Simulation(this);
//
//		SimulationModel simModel = getModel().getSimModel();
//		if (simModel.getEndTime() - simModel.getStartTime() <= simModel.getDt()) {
//			Messagebox
//					.showError(getParent(), "Simulation", "<html>Simulation konnte nicht gestartet werden<br>Startzeit, Endzeit und DT kontrollieren!</html>");
//			return;
//		}
//
//		CheckState state = s.checkData();
//
//		if (state == CheckState.NO_DATA) {
//			Messagebox.showError(getParent(), "Simulation", "<html>Simulation konnte nicht gestartet werden<br>Es ist nichts zum simulieren vorhanden!</html>");
//			return;
//		}
//		if (state == CheckState.ERROR) {
//			Messagebox.showError(getParent(), "Simulation", "<html>Simulation konnte nicht gestartet werden<br>Daten sind unvollständig!</html>");
//			return;
//		}
//		s.startSimulation();
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

	public void setSidebarVisible(boolean visible) {
		if (sidebarVisible != visible) {
			fireSidebarVisible(visible);
			sidebarVisible = visible;
		}
	}

	public boolean isSidebarVisible() {
		return sidebarVisible;
	}

	public void takeSnapshot() {
		SnapshotDialog dlg = new SnapshotDialog(parent, integration, view, view.getBounds());
		dlg.setVisible(true);
	}
	
	public SimulationManager getManager() {
		return manager;
	}
}
