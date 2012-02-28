package ch.zhaw.simulation.editor.control;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JMenu;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.editor.status.StatusLabelHandler;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.view.TextView;
import ch.zhaw.simulation.gui.configuration.codeditor.FormulaEditor;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.listener.SimulationAdapter;
import ch.zhaw.simulation.status.StatusHandler;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.action.AddNamedSimulationUndoAction;

/**
 * The controler of a model editor
 * 
 * @author Andreas Butti
 */
public abstract class AbstractEditorControl<M extends AbstractSimulationModel<?>> extends StatusHandler implements MenuActionListener {
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
	private FormulaEditor formulaEditor;

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

	private SimulationDocument doc;

	/**
	 * CTor
	 * 
	 * @param parent
	 * @param settings
	 */
	public AbstractEditorControl(JFrame parent, Settings settings, SimulationApplication app, SimulationDocument doc, M model) {
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

		this.addListener(status);
		
		this.doc = doc;
		if (doc == null) {
			throw new NullPointerException("doc == null");
		}

		model.addSimulationListener(new SimulationAdapter() {
			@Override
			public void dataChanged(SimulationObject o) {
				AbstractEditorControl.this.app.updateTitle();
			}

			@Override
			public void dataSaved(boolean saved) {
				AbstractEditorControl.this.app.updateTitle();
			}
		});

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

	public JFrame getParent() {
		return parent;
	}

	// TODO move to application control
	private FunctionHelp functionHelp = new FunctionHelp();

	public FunctionHelp getFunctionHelp() {
		return functionHelp;
	}

	public void showFormulaEditor(NamedSimulationObject data) {
		if (formulaEditor == null) {
			formulaEditor = new FormulaEditor(parent, this);
		}

		formulaEditor.setData(data);
		formulaEditor.setVisible(true);
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
				selectionModel.addSelectedInt((SelectableElement) c);
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

	public StatusLabelHandler getStatus() {
		return status;
	}

	public JMenu getRecentMenu() {
		return this.app.getRecentMenu();
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

		case SELECT_ALL:
			selectAll();
			break;

		case DELETE_SELECTION:
			this.deleteSelected();
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

		case HELP:
		case LOOK_AND_FEEL_CHANGED:
		case EXIT:
		case NEW_FILE:
		case SAVE_AS:
		case SHOW_MATH_CONSOLE:
		case OPEN_FILE:
		case SETTINGS:
		case ABOUT:
			this.app.menuActionPerformed(action);
			break;

		default:
			System.err.println("Unhandled event: " + action.getType() + " / " + action.getData());
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}