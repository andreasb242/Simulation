package ch.zhaw.simulation.model.xy;

import java.awt.Point;
import java.util.Vector;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.SimulationListener;
import ch.zhaw.simulation.model.listener.XYSimulationListener;

/**
 * The XY Model
 * 
 * @author Andreas Butti
 */
public class SimulationXYModel extends AbstractSimulationModel<XYSimulationListener> {

	/**
	 * The width of the model, cannot be extended during simulation!
	 */
	private int width;

	/**
	 * The height of the model, cannot be extended during simulation!
	 */
	private int height;

	/**
	 * The 0 Point
	 */
	private Point zero = new Point(0, 0);

	/**
	 * The grid size in pixel
	 */
	private int grid = 5;

	/**
	 * The Density
	 */
	private Vector<DensityData> density = new Vector<DensityData>();

	/**
	 * The flow models
	 */
	private SubModelList submodels = new SubModelList();

	public SimulationXYModel() {
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;

		fireSizeRasterChanged();
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		
		fireSizeRasterChanged();
	}

	public int getGrid() {
		return grid;
	}

	public void setGrid(int grid) {
		this.grid = grid;
		
		fireSizeRasterChanged();
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

			@Override
			public void modelSizeRasterChanged() {
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

	private void fireSizeRasterChanged() {
		for (XYSimulationListener l : this.listener) {
			l.modelSizeRasterChanged();
		}
	}

	/**
	 * Autoparser don't change the element, so the model is safed after if it
	 * was it before
	 * 
	 * @param o
	 */
	public void fireObjectChangedAutoparser(Object o) {
		if (o instanceof AbstractNamedSimulationData) {
			checkIntegrity((AbstractNamedSimulationData) o);
		}

		if (o instanceof AbstractSimulationData) {
			for (int i = 0; i < listener.size(); i++) {
				listener.get(i).dataChanged((AbstractSimulationData) o);
			}
		} else if (o instanceof DensityData) {
			fireDensityChanged((DensityData) o);
		}
	}

	@Override
	public Vector<AbstractNamedSimulationData> getSource(NamedFormulaData data) {
		return new Vector<AbstractNamedSimulationData>();
	}

	/**
	 * @return The zero Point relative to the intern coordinate system
	 */
	public Point getZero() {
		return zero;
	}
	
	public void setZero(Point zero) {
		if(zero == null) {
			throw new NullPointerException("zero == null");
		}

		this.zero = zero;
		
		fireSizeRasterChanged();
	}

	public SubModelList getSubmodels() {
		return submodels;
	}
}
