package ch.zhaw.simulation.undo.action.xy;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class SubModelAssignUndoAction extends AbstractUndoableEdit {
	private SubModel newSubmodel;
	private SimulationXYModel model;
	private Vector<AbstractNamedSimulationData> data;
	private HashMap<MesoData, SubModel> originalSubmodel = new HashMap<MesoData, SubModel>();

	public SubModelAssignUndoAction(Vector<AbstractNamedSimulationData> data, SubModel newSubmodel, SimulationXYModel model) {
		this.model = model;
		this.newSubmodel = newSubmodel;
		this.data = data;

		for (AbstractNamedSimulationData d : data) {
			MesoData m = (MesoData) d;
			originalSubmodel.put(m, m.getSubmodel());
		}

		assignNewSubmodel();
	}

	@Override
	public void die() {
		super.die();

		this.data.clear();
		this.originalSubmodel.clear();
		this.newSubmodel = null;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		for (AbstractNamedSimulationData d : data) {
			MesoData m = (MesoData) d;
			
			m.setSubmodel(this.originalSubmodel.get(m));
			this.model.fireObjectChanged(m);
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		
		assignNewSubmodel();
	}
	
	private void assignNewSubmodel() {
		for (AbstractNamedSimulationData d : data) {
			MesoData m = (MesoData) d;

			m.setSubmodel(newSubmodel);
			this.model.fireObjectChanged(d);
		}
	}

	@Override
	public String getRedoPresentationName() {
		return "Submodel «" + newSubmodel.getName() + "» nochmals zuordnen";
	}

	@Override
	public String getUndoPresentationName() {
		return "Submodel zuordnen rückgängig";
	}

}
