package ch.zhaw.simulation.inexport.madonna;


import java.util.HashMap;
import java.util.Map.Entry;

import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;


public class MadonnaImporter extends MadonnaReader {
	private HashMap<Integer, NamedSimulationObject> data2 = new HashMap<Integer, NamedSimulationObject>();

	public MadonnaImporter() {
	}

	@Override
	public boolean load(SimulationFlowModel model) throws ImportException {
		model.clear();

		for (Entry<Integer, MadonnaElement> element : data.entrySet()) {
			MadonnaElement d = element.getValue();
			int id = element.getKey();

			if (d instanceof MContainer) {
				MContainer e = (MContainer) d;
				SimulationContainer c = new SimulationContainer(e.getPos().x, e.getPos().y);

				c.setName(e.getName());
				c.setFormula(e.getFormula());

				model.addData(c);
				data2.put(id, c);
			} else if (d instanceof MFormula) {
				MFormula e = (MFormula) d;

				SimulationParameter c = new SimulationParameter(e.getPos().x, e.getPos().y);

				c.setName(e.getName());
				c.setFormula(e.getFormula());

				model.addData(c);
				data2.put(id, c);
			}
		}

		// Handle flows

		for (Entry<Integer, MadonnaElement> d : data.entrySet()) {
			if (d.getValue() instanceof MFlow) {
				MFlow e = (MFlow) d.getValue();

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
		NamedSimulationObject source = data2.get(arc.getFromId());
		NamedSimulationObject target = data2.get(arc.getToId());

		if (source == null) {
			throw new ImportException("Source from flow #" + id + " is null!");
		}
		if (target == null) {
			throw new ImportException("Target from flow #" + id + " is null!");
		}

		ParameterConnector c = new ParameterConnector(source, target);

		c.setHelperPoint(arc.getPos());

		model.addConnector(c);
	}

	private void handleFlow(MFlow f, SimulationFlowModel model, int id) throws ImportException {
		MConnector2 toArrow = null;
		MConnector2 fromArrow = null;

		for (MadonnaElement e : data.values()) {
			if (e instanceof MConnector2) {
				MConnector2 p = (MConnector2) e;

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

		SimulationObject source = getValueFor(fromArrow.getFromId(), model);
		SimulationObject target = getValueFor(toArrow.getToId(), model);

		if (source == null) {
			throw new ImportException("Source from flow #" + id + " is null!");
		}
		if (target == null) {
			throw new ImportException("Target from flow #" + id + " is null!");
		}

		FlowConnector flow = new FlowConnector(source, target);

		FlowValve pp = flow.getValve();

		pp.setName(f.getName());
		pp.setFormula(f.getFormula());

		model.addConnector(flow);
		data2.put(id, pp);
	}

	private SimulationObject getValueFor(int id, SimulationFlowModel model) {
		NamedSimulationObject v = data2.get(id);

		if (v == null && data.get(id) instanceof MCloud) {
			MCloud c = (MCloud) data.get(id);

			InfiniteData d = new InfiniteData(c.getPos().x, c.getPos().y);
			model.addData(d);
			return d;
		}

		return v;
	}

	@Override
	public boolean load() throws Exception {
		return true;
	}

	@Override
	public void unload() {
	}

	@Override
	public String[] getFileExtension() {
		return new String[] { ".mmd" };
	}
}
