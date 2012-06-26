package ch.zhaw.simulation.model.xy;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

public class SubModelList implements Iterable<SubModel> {
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
			l.submodelRemoved(model);
		}
	}

	private void fireModelAdded(SubModel model) {
		for (SubModelListener l : this.listener) {
			l.submodelAdded(model);
		}
	}

	/**
	 * A submodel has been changed
	 * 
	 * @param model
	 *            The changed submodel
	 */
	public void fireModelChanged(SubModel model) {
		for (SubModelListener l : this.listener) {
			l.submodelChanged(model);
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

	@Override
	public Iterator<SubModel> iterator() {
		return data.iterator();
	}

	public SubModel getByName(String name) {
		for (SubModel s : data) {
			if (s.getName().equals(name)) {
				return s;
			}
			System.out.println("DEBUG: " + s.getName());
		}
		return null;
	}

	public int indexOf(SubModel submodel) {
		return data.indexOf(submodel);
	}
}
