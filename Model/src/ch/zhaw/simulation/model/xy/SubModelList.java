package ch.zhaw.simulation.model.xy;

import java.awt.Color;
import java.util.Vector;

public class SubModelList {
	private Vector<SubModel> data = new Vector<SubModel>();
	private Vector<SubModelListener> listener = new Vector<SubModelListener>();

	public SubModelList() {
	}

	public void addModel(SubModel model) {
		this.data.add(model);

		calculateColors();

		fireModelAdded(model);
	}

	public void removeModel(SubModel model) {
		this.data.remove(model);

		calculateColors();

		fireModelRemoved(model);
	}

	private void fireModelRemoved(SubModel model) {
		for (SubModelListener l : this.listener) {
			l.modelRemoved(model);
		}
	}

	private void fireModelAdded(SubModel model) {
		for (SubModelListener l : this.listener) {
			l.modelAdded(model);
		}
	}

	public void fireModelChanged(SubModel model) {
		for (SubModelListener l : this.listener) {
			l.modelChanged(model);
		}
	}

	public int getSize() {
		return this.data.size();
	}

	public SubModel getModel(int id) {
		return this.data.get(id);
	}

	private void calculateColors() {
		int count = data.size();
		Color[] colors = ColorCalculator.calcColors(count);
		for (int i = 0; i < count; i++) {
			data.get(i).setColor(colors[i]);
		}
	}

	public void addListener(SubModelListener l) {
		this.listener.add(l);
	}

	public void removeListener(SubModelListener l) {
		this.listener.remove(l);
	}

}