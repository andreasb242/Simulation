package ch.zhaw.simulation.model.xy;

import java.awt.Color;

import ch.zhaw.simulation.model.flow.SimulationFlowModel;

public class SubModel {
	private String name;
	private Color color;
	private SimulationFlowModel model;

	public SubModel() {
		model = new SimulationFlowModel();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public SimulationFlowModel getModel() {
		return model;
	}
}
