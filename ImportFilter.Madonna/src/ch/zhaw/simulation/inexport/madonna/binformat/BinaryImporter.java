package ch.zhaw.simulation.inexport.madonna.binformat;

import java.util.HashMap;
import java.util.Map.Entry;

import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;

/**
 * @see AbstractMadonnaImportPlugin
 * 
 *      This is the Converter from Madonna (Binray) to (AB)² Simulation
 * 
 * @author Andreas Butti
 */
public class BinaryImporter extends AbstractMadonnaImportPlugin {
	private HashMap<Integer, AbstractNamedSimulationData> data2 = new HashMap<Integer, AbstractNamedSimulationData>();

	public BinaryImporter() {
	}

	@Override
	public boolean load(SimulationFlowModel model) throws ImportException {
		model.clear();

		for (Entry<Integer, MadonnaElement> element : data.entrySet()) {
			MadonnaElement d = element.getValue();
			int id = element.getKey();

			if (d instanceof MContainer) {
				MContainer e = (MContainer) d;
				SimulationContainerData c = new SimulationContainerData(e.getPos().x, e.getPos().y);

				c.setName(e.getName());
				c.setFormula(e.getFormula());

				model.addData(c);
				data2.put(id, c);
			} else if (d instanceof MParameter) {
				MParameter e = (MParameter) d;

				SimulationParameterData c = new SimulationParameterData(e.getPos().x, e.getPos().y);

				c.setName(e.getName());
				c.setFormula(e.getFormula());

				model.addData(c);
				data2.put(id, c);
			}
		}

		// Handle flows
		for (Entry<Integer, MadonnaElement> d : data.entrySet()) {
			if (d.getValue() instanceof MValve) {
				MValve e = (MValve) d.getValue();

				handleFlow(e, model, d.getKey());
			}
		}

		// Handle Parameter
		for (Entry<Integer, MadonnaElement> d : data.entrySet()) {
			if (d.getValue() instanceof MConnector) {
				MConnector e = (MConnector) d.getValue();

				handleConnector(e, model, d.getKey());
			}
		}

		return true;
	}

	private void handleConnector(MConnector arc, SimulationFlowModel model, int id) throws ImportException {
		AbstractNamedSimulationData source = data2.get(arc.getFromId());
		AbstractNamedSimulationData target = data2.get(arc.getToId());

		if (source == null) {
			throw new ImportException("Source from flow #" + id + " is null!");
		}
		if (target == null) {
			throw new ImportException("Target from flow #" + id + " is null!");
		}

		ParameterConnectorData c = new ParameterConnectorData(source, target);

		c.setHelperPoint(arc.getPos());

		model.addConnector(c);
	}

	private void handleFlow(MValve f, SimulationFlowModel model, int id) throws ImportException {
		MFlow toArrow = null;
		MFlow fromArrow = null;

		for (MadonnaElement e : data.values()) {
			if (e instanceof MFlow) {
				MFlow p = (MFlow) e;

				if (p.getFromId() == id) {
					if (toArrow != null) {
						throw new ImportException("Found more than one Pipe to Flow id #" + id);
					}
					toArrow = p;
				} else if (p.getToId() == id) {
					if (fromArrow != null) {
						throw new ImportException("Found more than one Pipe from Flow id #" + id);
					}
					fromArrow = p;
				}
			}
		}

		if (toArrow == null) {
			throw new ImportException("Found no Connector to Flow id #" + id);
		}
		if (fromArrow == null) {
			throw new ImportException("Found no Connector from Flow id #" + id);
		}

		AbstractSimulationData source = getValueFor(fromArrow.getFromId(), model);
		AbstractSimulationData target = getValueFor(toArrow.getToId(), model);

		if (source == null) {
			throw new ImportException("Source from flow #" + id + " is null!");
		}
		if (target == null) {
			throw new ImportException("Target from flow #" + id + " is null!");
		}

		FlowConnectorData flow = new FlowConnectorData(source, target);

		FlowValveData pp = flow.getValve();

		pp.setName(f.getName());
		pp.setFormula(f.getFormula());

		model.addConnector(flow);
		data2.put(id, pp);
	}

	private AbstractSimulationData getValueFor(int id, SimulationFlowModel model) {
		AbstractNamedSimulationData v = data2.get(id);

		if (v == null && data.get(id) instanceof MInfinite) {
			MInfinite c = (MInfinite) data.get(id);

			InfiniteData d = new InfiniteData(c.getPos().x, c.getPos().y);
			model.addData(d);
			return d;
		}

		return v;
	}

}
