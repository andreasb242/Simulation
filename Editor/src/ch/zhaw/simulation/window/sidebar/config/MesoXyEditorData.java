package ch.zhaw.simulation.window.sidebar.config;

import java.util.Vector;

import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.xy.DensityData;

public class MesoXyEditorData {

	private NamedFormulaData data;
	private Vector<DensityData> density;

	public MesoXyEditorData(NamedFormulaData data, Vector<DensityData> density) {
		this.data = data;
		this.density = density;
	}

	public NamedFormulaData getData() {
		return data;
	}

	public Vector<DensityData> getDensity() {
		return density;
	}

}
