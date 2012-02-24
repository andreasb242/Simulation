import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.xy.XYModel;

/**
 * This is the main Simulation document
 * 
 * 
 * @author Andreas Butti
 */
public class SimulationDocument {
	public enum SimulationType {
		/**
		 * The Simulation Document contains a single flow document
		 */
		FLOW,

		/**
		 * The Simulation Document contains a XY Diagramm, containing Flow
		 * diagramms
		 */
		XY
	};

	/**
	 * The type of the simulation
	 */
	private SimulationType type;

	/**
	 * The flow model if this.type == FLOW
	 */
	private SimulationFlowModel flowModel;

	/**
	 * The XY model if this.type == XY
	 */
	private XYModel xyModel;

	public SimulationDocument() {
	}

	public SimulationType getType() {
		return type;
	}
}
