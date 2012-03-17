package ch.zhaw.simulation.model.xy;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

public class MesoData extends AbstractNamedSimulationData {

	private SubModel submodel;

	public MesoData(int x, int y) {
		super(x, y);
	}

	@Override
	public String getDefaultName() {
		return "m";
	}

	@Override
	public int getWidth() {
		return 30;
	}

	@Override
	public int getHeight() {
		return 30;
	}

	public void setSubmodel(SubModel submodel) {
		this.submodel = submodel;
	}

	public SubModel getSubmodel() {
		return submodel;
	}

}