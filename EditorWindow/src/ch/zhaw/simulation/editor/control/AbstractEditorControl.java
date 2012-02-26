package ch.zhaw.simulation.editor.control;

import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.gui.configuration.codeditor.FormulaEditor;
import ch.zhaw.simulation.help.gui.HelpFrame;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.undo.UndoHandler;

/**
 * The controler of a model editor
 * 
 * @author Andreas Butti
 */
public abstract class AbstractEditorControl<M extends AbstractSimulationModel> {
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
	 * CTor
	 * 
	 * @param parent
	 * @param settings
	 */
	public AbstractEditorControl(JFrame parent, Settings settings) {
		this.settings = settings;
		if (settings == null) {
			throw new NullPointerException("settings == null");
		}

		this.parent = parent;
		if (parent == null) {
			throw new NullPointerException("parent == null");
		}

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
}
