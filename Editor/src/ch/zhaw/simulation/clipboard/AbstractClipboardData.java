package ch.zhaw.simulation.clipboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
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
 * Needs to implmenent input stream, else you get an Exception on Windows
 * Systems
 * 
 * @author Andreas Butti
 */
public abstract class AbstractClipboardData<M extends AbstractSimulationModel<?>, V extends AbstractEditorView<?>> extends InputStream implements ClipboardData {

	/**
	 * The selection model to selected the inserted elements
	 */
	protected transient SelectionModel selectionModel;

	/**
	 * The model
	 */
	protected transient M model;

	/**
	 * The view
	 */
	protected transient V view;

	/**
	 * The contens of this transferable
	 */
	private Vector<TransferData> contents = new Vector<TransferData>();

	/**
	 * ID to identify the Transferable
	 */
	private int editorSourceId;
	private int transferableId;

	public AbstractClipboardData(int editorSourceId) {
		this.editorSourceId = editorSourceId;

		Random generator = new Random();
		this.transferableId = generator.nextInt();
	}

	@Override
	public int getEditorSourceId() {
		return editorSourceId;
	}

	@Override
	public int getTransferableId() {
		return transferableId;
	}

	public void addToModel(SelectionModel selectionModel, M model, V view) {
		this.selectionModel = selectionModel;

		this.model = model;
		this.view = view;

		selectionModel.clearSelection();

		for (TransferData d : this.contents) {
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

	public void add(TransferData d) {
		this.contents.add(d);
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
		if (view != null) {
			selectionModel.addSelectedInt(view.findGuiComponent(c));
		}
	}

	@Override
	public int read() throws IOException {
		return -1;
	}
}
