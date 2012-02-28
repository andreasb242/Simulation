package ch.zhaw.simulation.app;

import java.awt.Window;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.util.RestartUtil;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.dialog.settings.SettingsDlg;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.filehandling.ImportPlugins;
import ch.zhaw.simulation.filehandling.LoadSaveHandler;
import ch.zhaw.simulation.help.gui.HelpFrame;
import ch.zhaw.simulation.math.console.MatrixConsole;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menu.RecentMenu;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.sim.PluginDataProvider;
import ch.zhaw.simulation.sim.SimulationManager;
import ch.zhaw.simulation.sim.SimulationPlugin;
import ch.zhaw.simulation.sim.StandardParameter;
import ch.zhaw.simulation.status.StatusHandler;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.window.flow.FlowWindow;
import ch.zhaw.simulation.window.xy.XYWindow;

public class ApplicationControl extends StatusHandler implements SimulationApplication, MenuActionListener {

	/**
	 * The settings
	 */
	private Settings settings;

	/**
	 * The import plugins
	 */
	private ImportPlugins importPlugins;

	/**
	 * The settings Dialog
	 */
	private SettingsDlg settigsDialog;

	/**
	 * Recent opened files
	 */
	private RecentMenu recentMenu;

	/**
	 * Load / Save handler
	 */
	private LoadSaveHandler savehandler;

	/**
	 * The help window
	 */
	private HelpFrame helpFrame;

	/**
	 * The simulation document with the data
	 */
	private SimulationDocument doc = new SimulationDocument();

	/**
	 * The main frame
	 */
	private JFrame mainFrame;

	/**
	 * The main editor controller
	 */
	private AbstractEditorControl<?> controller;

	/**
	 * The name of the current document
	 */
	private String documentName = null;

	private SimulationManager manager;

	/**
	 * Ctor
	 */
	public ApplicationControl() {
	}

	public void start(Settings settings, String openfile) {
		this.settings = settings;

		if (settings == null) {
			throw new NullPointerException("settings == null");
		}

		// Load and initialize the import plugins
		this.importPlugins = new ImportPlugins(settings);

		this.recentMenu = new RecentMenu(settings);
		this.recentMenu.addListener(this);

		this.manager = new SimulationManager(settings, doc.getSimulationConfiguration(), new PluginDataProvider() {

			@Override
			public JFrame getParent() {
				return ApplicationControl.this.mainFrame;
			}

		});

		boolean mainWindow = true;

		SimulationType type = SimulationType.FLOW_SIMULATION;

		Messagebox msg = new Messagebox(null, "Typ wählen", "Simulationstyp wählen", Messagebox.QUESTION);
		msg.addButton("XY", 0);
		msg.addButton("Flow", 1);
		msg.addButton("Flow [Child]", 2);
		int res = msg.display();
		if (res == 0) {
			type = SimulationType.XY_MODEL;
		} else if(res == 2) {
			mainWindow = false;
		}

		doc.setType(type);
		loadSimulationParameterFromSettings();

		if (type == SimulationType.FLOW_SIMULATION) {
			showFlowWindow(mainWindow);
		} else {
			showXYWindow();
		}

		this.savehandler = new LoadSaveHandler(mainFrame, settings, SysintegrationFactory.createSysintegration(), this.importPlugins);
		this.savehandler.addListener(this);
	}

	public void showXYWindow() {
		XYWindow win = new XYWindow();
		XYEditorControl control = new XYEditorControl(this, doc, doc.getXyModel(), win, settings);
		this.controller = control;
		this.addListener(control);
		win.init(control);
		win.addListener(control);

		this.mainFrame = win;

		win.setVisible(true);
	}

	public void showFlowWindow(boolean mainWindow) {
		FlowWindow win = new FlowWindow(mainWindow);
		FlowEditorControl control = new FlowEditorControl(this, doc.getFlowModel(), doc, win, settings);
		this.controller = control;
		this.addListener(control);
		win.init(control);
		win.addListener(control);

		this.mainFrame = win;
		mainFrame.setVisible(true);
	}

	private void loadSimulationParameterFromSettings() {
		int prefixLen = StandardParameter.SIM_PROPERTY_STRING_PREFIX.length();
		for (String k : settings.getKeysStartingWith(StandardParameter.SIM_PROPERTY_STRING_PREFIX)) {
			doc.getSimulationConfiguration().setParameter(k.substring(prefixLen), settings.getSetting(k));
		}

		prefixLen = StandardParameter.SIM_PROPERTY_DOUBLE_PREFIX.length();
		for (String k : settings.getKeysStartingWith(StandardParameter.SIM_PROPERTY_DOUBLE_PREFIX)) {
			try {
				double d = Double.parseDouble(settings.getSetting(k));
				doc.getSimulationConfiguration().setParameter(k.substring(prefixLen), d);
			} catch (Exception e) {
				System.err.println("Invalid double setting: \"" + k + "\" = \"" + settings.getSetting(k) + "\"");
			}
		}
	}

	public void releaseOpenWindow() {
		this.controller.dispose();

		// TODO release
	}

	@Override
	public SimulationManager getManager() {
		return manager;
	}

	public Settings getSettings() {
		return settings;
	}

	public AbstractEditorControl<?> getController() {
		return controller;
	}

	/**
	 * Shows the about dialog
	 */
	public void showAboutDialog() {
		AboutDialog dlg = new AboutDialog(mainFrame);
		dlg.setVisible(true);
	}

	/**
	 * Shows the settings dialog
	 */
	public void showSettingsDialog() {
		if (settigsDialog == null) {
			settigsDialog = new SettingsDlg(this);
		}
		settigsDialog.setVisible(true);
	}

	/**
	 * Shows the help dialog
	 */
	public void showHelpDialog() {
		if (helpFrame == null) {
			helpFrame = new HelpFrame(this.mainFrame);
		}
		helpFrame.setVisible(true);
	}

	/**
	 * Shows the math console
	 */
	public void showMathConsole() {
		new MatrixConsole().setVisible(true);
	}

	public ImportPlugins getImportPlugins() {
		return importPlugins;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void startSimulation() {
		String plugin = doc.getSimulationConfiguration().getPlugin();

		if (plugin == null) {
			Messagebox.showError(getMainFrame(), "Kein Plugin gewählt", "Bitte wählen Sie in der Sidebar mit welchem Plugin simuliert werden soll");
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
			Messagebox.showError(getMainFrame(), "Plugin nicht gefunden", "Bitte wählen Sie in der Sidebar mit welchem Plugin simuliert werden soll");
			return;
		}

		SimulationPlugin handler = selectedPlugin.getPlugin();

		try {
			handler.checkModel(doc);
		} catch (SimulationModelException ex) {
			Messagebox.showError(getMainFrame(), "Simulation nicht möglich", ex.getMessage());

			controller.getView().selectElement(ex.getSimObject());

			ex.printStackTrace();
			return;
		}

		try {
			handler.prepareSimulation(doc);
		} catch (Exception e) {
			Errorhandler.showError(e, "Simulation fehlgeschlagen");
		}
	}

	@Override
	public void newFile(SimulationType flowSimulation) {
		if (askSave() == true) {
			doc.clear();
			loadSimulationParameterFromSettings();

			this.controller.getSelectionModel().clearSelection();
			setDocumentTitle(null);

			setStatusText("Neues Dokument erstellt");
			savehandler.clear();
			doc.setSaved();

			this.controller.getUndoManager().discardAllEdits();
		}
	}

	@Override
	public boolean save() {
		if (!savehandler.save(doc)) {
			return false;
		}

		updatePaths();
		return true;
	}

	@Override
	public void open(String path) {
		if (askSave() == true) {
			if (savehandler.open(new File(path), doc)) {
				this.controller.getSelectionModel().clearSelection();
				updatePaths();
			}
		}
	}

	@Override
	public boolean saveAs() {
		if (!savehandler.saveAs(doc)) {
			return false;
		}
		updatePaths();

		return true;

	}

	@Override
	public void setLookAndFeel(String lookAndFeel) {
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

	@Override
	public void open() {
		if (askSave() == true) {
			if (savehandler.open(doc)) {
				this.controller.getSelectionModel().clearSelection();
				updatePaths();
			}
		}
	}

	public boolean exit() {
		if (askSave() == true) {
			this.mainFrame.setVisible(false);
			this.mainFrame.dispose();

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

	protected boolean askSave() {
		if (doc.isChanged()) {
			Messagebox msg = new Messagebox(this.mainFrame, "Aktuelles Dokument", "Soll das aktuelle Dokument verworfen werden?", Messagebox.QUESTION);
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

	@Override
	public void menuActionPerformed(MenuToolbarAction action) {

		switch (action.getType()) {
		case NEW_FILE:
			this.newFile(SimulationType.FLOW_SIMULATION);
			break;

		case SAVE_AS:
			this.saveAs();
			break;

		case OPEN_FILE:
			if (action.getData() != null) {
				this.open(action.getData().toString());
			} else {
				this.open();
			}
			break;

		case ABOUT:
			this.showAboutDialog();
			break;

		case SHOW_MATH_CONSOLE:
			this.showMathConsole();
			break;

		case SETTINGS:
			this.showSettingsDialog();
			break;

		case HELP:
			this.showHelpDialog();
			break;

		case EXIT:
			this.exit();
			break;

		case LOOK_AND_FEEL_CHANGED:
			setLookAndFeel(action.getData().toString());
			break;

		case START_SIMULATION:
			startSimulation();
			break;

		default:
			System.err.println("Unhandled event (ApplicationControl): " + action.getType() + " / " + action.getData());

		}
	}

	@Override
	public JMenu getRecentMenu() {
		return recentMenu.getMenu();
	}

	public void updatePaths() {
		if (savehandler.getPath() != null) {
			recentMenu.addFile(savehandler.getPath().getAbsolutePath());
			setDocumentTitle(savehandler.getPath().getName());
		}
	}

	public void setDocumentTitle(String name) {
		documentName = name;
		updateTitle();
	}

	@Override
	public void updateTitle() {
		if (documentName == null) {
			this.mainFrame.setTitle("Simulation");
		} else {
			String saved = "";
			if (doc.isChanged()) {
				saved = " *";
			}
			this.mainFrame.setTitle(documentName + saved + " - Simulation");
		}
	}

	public String getDocumentName() {
		return documentName;
	}
}
