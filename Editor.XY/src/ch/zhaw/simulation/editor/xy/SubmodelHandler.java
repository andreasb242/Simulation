package ch.zhaw.simulation.editor.xy;

import java.util.Vector;

import ch.zhaw.simulation.model.xy.SubModel;

public class SubmodelHandler {
	private Vector<SubModelSelectionListener> listener = new Vector<SubModelSelectionListener>();

	public SubmodelHandler() {
	}

	public void fireItemSelected(SubModel submodel) {
		if (submodel == null) {
			throw new NullPointerException("submodel == null");
		}

		for (SubModelSelectionListener l : this.listener) {
			l.subModelSelected(submodel);
		}
	}

	public void addSubModelSelectionListener(SubModelSelectionListener l) {
		if (l == null) {
			throw new NullPointerException();
		}
		listener.add(l);
	}

	public void removeSubModelSelectionListener(SubModelSelectionListener l) {
		listener.remove(l);
	}

}
