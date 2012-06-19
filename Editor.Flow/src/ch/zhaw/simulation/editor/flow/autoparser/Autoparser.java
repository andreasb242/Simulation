package ch.zhaw.simulation.editor.flow.autoparser;

import java.util.Vector;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.NamedFormulaData.Status;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;

public class Autoparser {
	private Parser parser = new Parser();
	private FlowEditorControl control;

	private boolean running = false;

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
					n.setStatus(Status.NOT_PARSED, "");
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

			@Override
			public void connectorRemoved(AbstractConnectorData<?> c) {
				connectorChanged(c);
			};
		});
	}

	private void parse(AbstractNamedSimulationData o) {
		if (!running) {
			return;
		}

		if (o.getStatus() != AbstractNamedSimulationData.Status.NOT_PARSED) {
			return;
		}

		Vector<AbstractNamedSimulationData> sources = control.getModel().getSource(o);
		try {
			parser.checkCode(o.getFormula(), o, control.getModel(), sources, o.getName());
			o.setStatus(AbstractNamedSimulationData.Status.SYNTAX_OK, null);
		} catch (CompilerError e) {
			o.setStatus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, e.getMessage());
		} catch (SimulationModelException e) {
			o.setStatus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, e.getMessage());
		} catch (Exception e) {
			o.setStatus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, e.getMessage());
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
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData && !(d instanceof SimulationDensityContainerData)) {
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
