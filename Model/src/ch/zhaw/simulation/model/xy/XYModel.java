package ch.zhaw.simulation.model.xy;

import java.util.Vector;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.SimulationListener;
import ch.zhaw.simulation.model.listener.XYSimulationListener;

/**
 * The XY Model
 * 
 * @author Andreas Butti
 */
public class XYModel extends AbstractSimulationModel<XYSimulationListener> {

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
	public XYSimulationListener addSimulationListener(final SimulationListener l) {
		XYSimulationListener listener = new XYSimulationListener() {

			@Override
			public void dataSaved(boolean saved) {
				l.dataSaved(saved);
			}

			@Override
			public void dataRemoved(AbstractSimulationData o) {
				l.dataRemoved(o);
			}

			@Override
			public void dataChanged(AbstractSimulationData o) {
				l.dataChanged(o);
			}

			@Override
			public void dataAdded(AbstractSimulationData o) {
				l.dataAdded(o);
			}

			@Override
			public void clearData() {
				l.clearData();
			}

			@Override
			public void densityRemoved(DensityData d) {
			}

			@Override
			public void densityChanged(DensityData d) {
			}

			@Override
			public void densityAdded(DensityData d) {
			}
		};
		addListener(listener);

		return listener;
	}

	public Vector<DensityData> getDensity() {
		return density;
	}

	public void addDensity(DensityData d) {
		setChanged();
		density.add(d);
		fireDensityAdded(d);
	}

	public void removeDensity(DensityData d) {
		setChanged();
		density.remove(d);
		fireDensityRemoved(d);
	}

	public void densityChanged(DensityData d) {
		setChanged();
		fireDensityChanged(d);
	}

	private void fireDensityAdded(DensityData d) {
		for (XYSimulationListener l : this.listener) {
			l.densityAdded(d);
		}
	}

	private void fireDensityRemoved(DensityData d) {
		for (XYSimulationListener l : this.listener) {
			l.densityRemoved(d);
		}
	}

	private void fireDensityChanged(DensityData d) {
		for (XYSimulationListener l : this.listener) {
			l.densityChanged(d);
		}
	}

}
