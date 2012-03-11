package ch.zhaw.simulation.math;


import java.util.Vector;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData.Status;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;

import butti.javalibs.errorhandler.Errorhandler;

public class Autoparser {
	private Parser parser = new Parser();
	private FlowEditorControl control;

	private boolean running = true;

	public Autoparser(FlowEditorControl control) {
		this.control = control;

		control.getModel().addListener(new FlowSimulationAdapter() {
			@Override
			public void dataChanged(AbstractSimulationData o) {
				if (o instanceof AbstractNamedSimulationData) {
					parse((AbstractNamedSimulationData) o);
				}
			}

			@Override
			public void dataAdded(AbstractSimulationData o) {
				dataChanged(o);
			}

			@Override
			public void connectorChanged(AbstractConnectorData<?> c) {
				if (c instanceof FlowConnectorData) {
					FlowValveData p = ((FlowConnectorData) c).getValve();
					parse(p);
				}

				// Ziel auch neu parsen
				reparse(c.getTarget());
			}

			private void reparse(Object o) {
				if (o instanceof AbstractNamedSimulationData) {
					AbstractNamedSimulationData n = (AbstractNamedSimulationData) o;
					n.setStaus(Status.NOT_PARSED, "");
					parse(n);
				}
			}

			@Override
			public void connectorAdded(AbstractConnectorData<?> c) {
				connectorChanged(c);
			}

			@Override
			public void dataRemoved(AbstractSimulationData o) {
				dataChanged(o);
			}

			public void connectorRemoved(AbstractConnectorData<?> c) {
				connectorChanged(c);
			};
		});
	}

	private void parse(AbstractNamedSimulationData o) {
		if (!running) {
			return;
		}

		if (o.getStaus() != AbstractNamedSimulationData.Status.NOT_PARSED) {
			return;
		}

		Vector<AbstractNamedSimulationData> sources = control.getModel().getSource(o);
		try {
			parser.checkCode(o.getFormula(), o, control.getModel(), sources, o.getName());
			o.setStaus(AbstractNamedSimulationData.Status.SYNTAX_OK, null);
		} catch (CompilerError e) {
			o.setStaus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, e.getMessage());
		} catch (SimulationModelException e) {
			o.setStaus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, e.getMessage());
		} catch (Exception e) {
			o.setStaus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, e.getMessage());
			Errorhandler.logError(e);
		}

		control.getModel().fireObjectChanged(o);
	}

	public void stop() {
		running = false;
	}

	public void start() {
		if (running == true) {
			return;
		}
		running = true;

		SimulationFlowModel model = control.getModel();
		for (AbstractSimulationData d : model.getDatas()) {
			if (d instanceof AbstractNamedSimulationData) {
				parse((AbstractNamedSimulationData) d);
			}
		}

		for (AbstractConnectorData<?> c : model.getConnectors()) {
			if (c instanceof FlowConnectorData) {
				FlowValveData p = ((FlowConnectorData) c).getValve();
				parse(p);
			}
		}
	}
}
