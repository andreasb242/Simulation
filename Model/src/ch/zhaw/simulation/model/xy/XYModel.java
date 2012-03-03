package ch.zhaw.simulation.model.xy;

import java.util.Vector;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.listener.SimulationListener;

/**
 * The XY Model
 * 
 * @author Andreas Butti
 */
public class XYModel extends AbstractSimulationModel<SimulationListener> {

	/**
	 * The width of the model, cannot be extended during simulation!
	 */
	private int width;

	/**
	 * The height of the model, cannot be extended during simulation!
	 */
	private int height;

	/**
	 * The grid size in pixel
	 */
	private int grid = 5;

	private Vector<DensityData> density = new Vector<DensityData>();

	public XYModel() {
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getGrid() {
		return grid;
	}

	public void setGrid(int grid) {
		this.grid = grid;
	}

	@Override
	public SimulationType getModelType() {
		return SimulationType.XY_MODEL;
	}

	@Override
	public SimulationListener addSimulationListener(SimulationListener l) {
		addListener(l);
		return l;
	}

	public Vector<DensityData> getDensity() {
		return density;
	}

}
