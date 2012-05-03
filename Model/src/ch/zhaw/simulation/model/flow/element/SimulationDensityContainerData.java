package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.xy.DensityData;

/**
 * An simulation density container
 * 
 * @author Andreas Butti
 */
public class SimulationDensityContainerData extends AbstractNamedSimulationData {
	private DensityData density;

	public SimulationDensityContainerData(int x, int y) {
		super(x, y);
	}

	@Override
	public int getHeight() {
		return 70;
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public String getDefaultName() {
		return "d";
	}

	public DensityData getDensity() {
		return density;
	}

	public void setDensity(DensityData density) {
		this.density = density;
		if (density != null) {
			setFormula("", Status.SYNTAX_OK, null);
		} else {
			setFormula("", Status.SYNTAX_ERROR, "Keine Density zugeordnet");
		}
	}
}
