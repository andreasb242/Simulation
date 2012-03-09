package ch.zhaw.simulation.clipboard;

import java.util.Vector;

import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.selection.SelectionModel;

/**
 * Contains clipboard data
 * 
 * @author Andreas Butti
 */
public abstract class AbstractClipboardData<M extends AbstractSimulationModel<?>, V extends AbstractEditorView<?>> extends Vector<TransferData> implements
		ClipboardData {
	private static final long serialVersionUID = 1L;

	/**
	 * The selection model to selected the inserted elements
	 */
	protected SelectionModel selectionModel;

	/**
	 * The model
	 */
	protected M model;

	/**
	 * The view
	 */
	protected V view;

	public AbstractClipboardData() {
	}

	public void addToModel(SelectionModel selectionModel, M model, V view) {
		this.selectionModel = selectionModel;

		this.model = model;
		this.view = view;

		selectionModel.clearSelection();

		for (TransferData d : this) {
			switch (d.getType()) {
			case Text:
				handleText(d);
				continue;
			case Global:
				handleGlobal(d);
				continue;
			}

			if (!handleData(d)) {
				throw new RuntimeException("Unhandled Type: " + d.getType());
			}
		}

		finalizePaste();

		selectionModel.fireSelectionChanged();
	}

	protected void finalizePaste() {
	}

	protected abstract boolean handleData(TransferData d);

	protected void handleText(TransferData d) {
		TextData t = new TextData(d.getX(), d.getY());
		t.setName(d.getName());
		t.setText(d.getFormula());

		addElement(t, d);

		select(t);
	}

	protected void handleGlobal(TransferData d) {
		SimulationGlobalData g = new SimulationGlobalData(d.getX(), d.getY());
		g.setName(d.getName());
		g.setFormula(d.getFormula());

		addElement(g, d);

		select(g);
	}

	/**
	 * Adds the data to the model, view or however the implementaion works
	 */
	protected abstract void addElement(AbstractSimulationData data, TransferData transferdata);

	public void select(AbstractSimulationData c) {
		selectionModel.addSelectedInt(view.findGuiComponent(c));
	}
}
