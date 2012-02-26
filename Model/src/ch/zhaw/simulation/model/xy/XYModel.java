package ch.zhaw.simulation.model.xy;

import ch.zhaw.simulation.model.AbstractSimulationModel;

/**
 * The XY Model
 * 
 * @author Andreas Butti
 */
public class XYModel extends AbstractSimulationModel {

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
	public boolean isFlowModel() {
		return false;
	}

}
