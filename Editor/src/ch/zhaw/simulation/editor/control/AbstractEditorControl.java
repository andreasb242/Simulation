package ch.zhaw.simulation.editor.control;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.util.RestartUtil;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.editor.status.StatusHandler;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.view.TextView;
import ch.zhaw.simulation.gui.configuration.codeditor.FormulaEditor;
import ch.zhaw.simulation.help.gui.HelpFrame;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menu.RecentMenu;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;
import ch.zhaw.simulation.model.listener.SimulationAdapter;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.action.AddNamedSimulationUndoAction;

/**
 * The controler of a model editor
 * 
 * @author Andreas Butti
 */
public abstract class AbstractEditorControl<M extends AbstractSimulationModel<?>> implements MenuActionListener {
	/**
	 * The selection model, contains the current selected gui elements
	 */
	protected SelectionModel selectionModel = new SelectionModel();

	/**
	 * The model of this editor
	 */
	protected M model;

	private String documentName = null;
	private FormulaEditor formulaEditor;
	private HelpFrame helpFrame;

	private SimulationApplication app;

	// TODO: move to application
	protected RecentMenu recentMenu;

	/**
	 * The parent JFrame
	 */
	private JFrame parent;

	/**
	 * The system integration object
	 */
	private Sysintegration integration;

	/**
	 * The settings of this application
	 * 
	 */
	// TODO: mode to application control
	protected Settings settings;

	/**
	 * The mouse listener to add elements to the view, e.g. a new global
	 */
	private MouseListener lastMouseListener;

	/**
	 * Status handler, for statusbar text / icons
	 */
	private StatusHandler status = new StatusHandler();
	
	/**
	 * CTor
	 * 
	 * @param parent
	 * @param settings
	 */
	public AbstractEditorControl(JFrame parent, Settings settings, SimulationApplication app, M model) {
		this.settings = settings;
		if (settings == null) {
			throw new NullPointerException("settings == null");
		}

		this.parent = parent;
		if (parent == null) {
			throw new NullPointerException("parent == null");
		}

		this.app = app;
		if (app == null) {
			throw new NullPointerException("app == null");
		}

		this.model = model;
		if (model == null) {
			throw new NullPointerException("model == null");
		}

		model.addSimulationListener(new SimulationAdapter() {
			@Override
			public void dataChanged(SimulationObject o) {
				updateTitle();
			}

			@Override
			public void dataSaved(boolean saved) {
				updateTitle();
			}
		});

		this.recentMenu = new RecentMenu(settings);

		this.recentMenu.addListener(this);

		integration = SysintegrationFactory.createSysintegration();
	}

	/**
	 * Deletes the current selected elements
	 */
	public void deleteSelected() {
		SelectableElement[] selected = selectionModel.getSelected();
		selectionModel.clearSelection();

		delete(selected);
	}

	/**
	 * Deletes the elements from the model and may also other depending objects
	 */
	protected abstract void delete(SelectableElement[] elements);

	/**
	 * @return The selecion model
	 */
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @return The system integration object
	 */
	public Sysintegration getSysintegration() {
		return integration;
	}

	/**
	 * @return The model of this editor
	 */
	public M getModel() {
		return model;
	}

	/**
	 * @return The settings of this application
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @return The view of this editor
	 */
	public abstract AbstractEditorView<?> getView();

	/**
	 * @return The undo / redo handler for this view
	 */
	public UndoHandler getUndoManager() {
		return getView().getUndoHandler();
	}

	/**
	 * @return The clipboard handler
	 */
	public ClipboardHandler<?> getClipboard() {
		return getView().getClipboard();
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

	protected boolean askSave() {
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

	public abstract boolean save();

	public JFrame getParent() {
		return parent;
	}

	// TODO move to application control
	private FunctionHelp functionHelp = new FunctionHelp();

	public FunctionHelp getFunctionHelp() {
		return functionHelp;
	}

	public void setDocumentTitle(String name) {
		documentName = name;
		updateTitle();
	}

	protected void updateTitle() {
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

	public void showFormulaEditor(NamedSimulationObject data) {
		if (formulaEditor == null) {
			formulaEditor = new FormulaEditor(parent, this);
		}

		formulaEditor.setData(data);
		formulaEditor.setVisible(true);
	}

	public String getDocumentName() {
		return documentName;
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

	public abstract void open(String path);

	public RecentMenu getRecentMenu() {
		return recentMenu;
	}

	/**
	 * 
	 * @param action
	 * @return true if event is handled and should not be cared about in
	 *         superclass
	 */
	public boolean menuActionPerformedOverwrite(MenuToolbarAction action) {
		return false;
	}

	public void about() {
		this.app.showAboutDialog();
	}

	public void selectAll() {
		for (Component c : getView().getComponents()) {
			if (c instanceof SelectableElement) {
				selectionModel.addSelectedInt((SelectableElement) c);
			}
		}

		selectionModel.fireSelectionChanged();
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

	/**
	 * Cancels e.g. all current actions, e.g. adding a connector
	 */
	protected void cancelAllActions() {
		if (lastMouseListener != null) {
			getView().removeMouseListener(lastMouseListener);
			lastMouseListener = null;
		}
	}


	private void postAddAction(NamedSimulationObject so) {
		if (so instanceof TextData) {
			TextData data = (TextData) so;
			for (Component c : getView().getComponents()) {
				if (c instanceof TextView) {
					if (((TextView) c).getData() == data) {
						((TextView) c).showTextEditor();
					}
				}
			}
		}
	}
	

	public void clearStatus() {
		status.clearStatus();
	}

	
	public void addComponent(final NamedSimulationObject so, String type) {
		setStatusTextInfo("Ins Dokument klicken um " + type + " einzufügen");

		lastMouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				so.setX((int) e.getPoint().getX() - so.getWidth() / 2);
				so.setY((int) e.getPoint().getY() - so.getHeight() / 2);
				clearStatus();
				getView().removeMouseListener(this);

				lastMouseListener = null;

				getUndoManager().addEdit(new AddNamedSimulationUndoAction(so, model));

				postAddAction(so);
			}
		};

		getView().addMouseListener(lastMouseListener);
	}
	
	public void setStatusTextInfo(String text) {
		getStatus().setStatusTextInfo(text);
	}

	public void setStatusText(String text) {
		getStatus().setStatusText(text);		
	}
	
	public StatusHandler getStatus() {
		return status;
	}

	public void addGlobal() {
		cancelAllActions();
		addComponent(new SimulationGlobal(0, 0), "Global");
	}
	
	public void addText() {
		cancelAllActions();
		addComponent(new TextData(0, 0), "Text");
	}

	@Override
	public final void menuActionPerformed(MenuToolbarAction action) {
		if (menuActionPerformedOverwrite(action)) {
			return;
		}

		switch (action.getType()) {
		case EXIT:
			this.exit();
			break;

		case HELP:
			this.help();
			break;

		case ABOUT:
			this.about();
			break;

		case SELECT_ALL:
			selectAll();
			break;

		case DELETE_SELECTION:
			this.deleteSelected();
			break;

		case SHOW_MATH_CONSOLE:
			this.app.showMathConsole();
			break;

		case LOOK_AND_FEEL_CHANGED:
			setLookAndFeel(action.getData().toString());
			break;

		case SAVE:
			this.save();
			break;

		case FLOW_ADD_GLOBAL:
			addGlobal();
			break;
			
		case FLOW_ADD_TEXT:
			addText();
			break;


		case NEW_FILE:
		case OPEN_FILE:
		case SETTINGS:
		case SAVE_AS:

		default:
			System.err.println("Unhandled event: " + action.getType() + " / " + action.getData());
		}
	}

}
