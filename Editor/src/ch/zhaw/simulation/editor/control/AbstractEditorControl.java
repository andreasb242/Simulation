package ch.zhaw.simulation.editor.control;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;

import butti.javalibs.config.Config;
import butti.javalibs.config.Settings;
import butti.javalibs.util.StringUtil;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.editor.status.StatusLabelHandler;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.view.TextView;
import ch.zhaw.simulation.gui.codeditor.FormularEditorDialog;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.AutoparserListener;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.listener.SimulationAdapter;
import ch.zhaw.simulation.model.listener.SimulationListener;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.status.StatusHandler;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.action.AddNamedSimulationUndoAction;
import ch.zhaw.simulation.undo.action.FormulaChangeUndoAction;

/**
 * The controler of a model editor
 * 
 * @author Andreas Butti
 */
public abstract class AbstractEditorControl<M extends AbstractSimulationModel<?>> extends StatusHandler implements MenuActionListener, AutoparserListener {
	/**
	 * The selection model, contains the current selected gui elements
	 */
	protected SelectionModel selectionModel = new SelectionModel();

	/**
	 * The model of this editor
	 */
	protected M model;

	/**
	 * The formula editor with syntax hilighting etc.
	 */
	private FormularEditorDialog formulaEditor;

	/**
	 * The simulation application
	 */
	private SimulationApplication app;

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
	protected Settings settings;

	/**
	 * The mouse listener to add elements to the view, e.g. a new global
	 */
	private MouseListener lastMouseListener;

	/**
	 * Status handler, for statusbar text / icons
	 */
	private StatusLabelHandler status = new StatusLabelHandler();

	/**
	 * The simulation document
	 */
	protected SimulationDocument doc;

	/**
	 * Listener for Simulation progress and state
	 */
	private SimulationListener simListener;

	/**
	 * Editor ID for copy & paste
	 */
	private int editorId;

	/**
	 * CTor
	 * 
	 * @param parent
	 * @param settings
	 */
	public AbstractEditorControl(JFrame parent, Settings settings, SimulationApplication app, SimulationDocument doc, M model) {
		this.settings = settings;

		Random generator = new Random();
		this.editorId = generator.nextInt();
		if (this.editorId == 0) {
			this.editorId++;
		}

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

		this.addListener(status);

		this.doc = doc;
		if (doc == null) {
			throw new NullPointerException("doc == null");
		}

		simListener = model.addSimulationListener(new SimulationAdapter() {
			@Override
			public void dataChanged(AbstractSimulationData o) {
				AbstractEditorControl.this.app.updateTitle();
			}

			@Override
			public void dataSaved(boolean saved) {
				AbstractEditorControl.this.app.updateTitle();
			}
		});

		integration = SysintegrationFactory.getSysintegration();
	}

	/**
	 * @return a uniq, random editor ID for copy & paste
	 */
	public int getEditorId() {
		return editorId;
	}

	/**
	 * Returns the paste offset, so each pasted elements are moved a little bit
	 * more, so you see there are multiple elements
	 */
	public int getPasteOffset() {
		return Config.get("pasteOffset", 10);
	}

	/**
	 * Deletes the current selected elements
	 */
	public void deleteSelected() {
		SelectableElement<?>[] selected = selectionModel.getSelected();
		selectionModel.clearSelection();

		if (selected.length > 0) {
			delete(selected);
		}
	}

	/**
	 * Deletes the elements from the model and may also other depending objects
	 */
	protected abstract void delete(SelectableElement<?>[] elements);

	/**
	 * @return The selecion model
	 */
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return doc.getSimulationConfiguration();
	}

	public SimulationDocument getDoc() {
		return doc;
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

	/**
	 * Saves this (and all depeding) model(x)
	 * 
	 * @return true if successfully, false if not saved
	 */
	public final boolean save() {
		return this.app.save();
	}

	public SimulationApplication getApp() {
		return app;
	}

	public JFrame getParent() {
		return parent;
	}

	public void showFormulaEditor(NamedFormulaData data) {
		String oldFormula = data.getFormula();

		if (formulaEditor == null) {
			formulaEditor = new FormularEditorDialog(parent, getSysintegration(), getModel());
		}

		formulaEditor.setData(data);
		formulaEditor.setVisible(true);

		formulaEditor.unselect();

		String newFormula = data.getFormula();
		if (!StringUtil.equals(oldFormula, newFormula)) {
			getUndoManager().addEdit(new FormulaChangeUndoAction(data, oldFormula, newFormula, model));
		}
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

	public void selectAll() {
		for (Component c : getView().getComponents()) {
			if (c instanceof SelectableElement) {
				selectionModel.addSelectedInt((SelectableElement<?>) c);
			}
		}

		selectionModel.fireSelectionChanged();
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

	private void postAddAction(AbstractNamedSimulationData so) {
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

	@Override
	public abstract void stopAutoparser();

	@Override
	public abstract void startAutoparser();

	public void addComponent(final AbstractNamedSimulationData so, String type) {
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

	public StatusLabelHandler getStatus() {
		return status;
	}

	public JMenu getRecentMenu() {
		return this.app.getRecentMenu();
	}

	public void addGlobal() {
		cancelAllActions();
		addComponent(new SimulationGlobalData(0, 0), "Global");
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

		case SELECT_ALL:
			selectAll();
			break;

		case DELETE_SELECTION:
			this.deleteSelected();
			break;

		case SAVE:
			this.save();
			break;

		case EDITOR_ADD_GLOBAL:
			addGlobal();
			break;

		case EDITOR_ADD_TEXT:
			addText();
			break;

		case HELP:
		case LOOK_AND_FEEL_CHANGED:
		case EXIT:
		case NEW_FILE:
		case SAVE_AS:
		case SHOW_MATH_CONSOLE:
		case OPEN_FILE:
		case SETTINGS:
		case ABOUT:
		case START_SIMULATION:
		case LOAD_RESULTS:
		case SNAPSHOT:
			this.app.menuActionPerformed(action);
			break;

		default:
			System.err.println("Unhandled event: " + action.getType() + " / " + action.getData());
		}
	}

	public void dispose() {
		this.removeListener(this.status);

		if (this.status != null) {
			this.status.dispose();
			this.status = null;
		}

		if (this.model != null) {
			this.model.removeListener(simListener);
			this.model = null;
		}

		if (this.formulaEditor != null) {
			this.formulaEditor.dispose();
			this.formulaEditor = null;
		}

		this.app = null;

		this.parent = null;

		this.integration = null;

		this.settings = null;

		getView().removeMouseListener(lastMouseListener);
		this.lastMouseListener = null;

		this.doc = null;

		super.dispose();

		if (this.selectionModel != null) {
			this.selectionModel.clearSelection();
			this.selectionModel.clearTmpSelection();
		}
		this.selectionModel = null;
	}

}
