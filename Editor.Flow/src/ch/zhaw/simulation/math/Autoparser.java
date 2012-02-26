package ch.zhaw.simulation.math;


import java.util.Vector;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.NamedSimulationObject.Status;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
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
			public void dataChanged(SimulationObject o) {
				if (o instanceof NamedSimulationObject) {
					parse((NamedSimulationObject) o);
				}
			}

			@Override
			public void dataAdded(SimulationObject o) {
				dataChanged(o);
			}

			@Override
			public void connectorChanged(Connector<?> c) {
				if (c instanceof FlowConnector) {
					FlowValve p = ((FlowConnector) c).getValve();
					parse(p);
				}

				// Ziel auch neu parsen
				reparse(c.getTarget());
			}

			private void reparse(Object o) {
				if (o instanceof NamedSimulationObject) {
					NamedSimulationObject n = (NamedSimulationObject) o;
					n.setStaus(Status.NOT_PARSED, "");
					parse(n);
				}
			}

			@Override
			public void connectorAdded(Connector<?> c) {
				connectorChanged(c);
			}

			@Override
			public void dataRemoved(SimulationObject o) {
				dataChanged(o);
			}

			public void connectorRemoved(Connector<?> c) {
				connectorChanged(c);
			};
		});
	}

	private void parse(NamedSimulationObject o) {
		if (!running) {
			return;
		}

		if (o.getStaus() != NamedSimulationObject.Status.NOT_PARSED) {
			return;
		}

		Vector<NamedSimulationObject> sources = control.getModel().getSource(o);
		try {
			parser.checkCode(o.getFormula(), o, control.getModel(), sources, o.getName());
			o.setStaus(NamedSimulationObject.Status.SYNTAX_OK, null);
		} catch (CompilerError e) {
			o.setStaus(NamedSimulationObject.Status.SYNTAX_ERROR, e.getMessage());
		} catch (SimulationModelException e) {
			o.setStaus(NamedSimulationObject.Status.SYNTAX_ERROR, e.getMessage());
		} catch (Exception e) {
			o.setStaus(NamedSimulationObject.Status.SYNTAX_ERROR, e.getMessage());
			Errorhandler.logError(e);
		}

		control.getModel().fireObjectChanged(o, true);
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
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				parse((NamedSimulationObject) d);
			}
		}

		for (Connector<?> c : model.getConnectors()) {
			if (c instanceof FlowConnector) {
				FlowValve p = ((FlowConnector) c).getValve();
				parse(p);
			}
		}
	}
}
