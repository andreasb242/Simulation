package ch.zhaw.simulation.editor.xy.autoparser;

import java.util.Vector;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.XYSimulationAdapter;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class Autoparser {
	private Parser parser = new Parser();
	private XYEditorControl control;

	private boolean running = false;

	public Autoparser(XYEditorControl control) {
		this.control = control;

		control.getModel().addListener(new XYSimulationAdapter() {
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
			public void dataRemoved(AbstractSimulationData o) {
				dataChanged(o);
			}
		});
	}

	private void parse(AbstractNamedSimulationData o) {
		if (!running) {

			// TODO Debug
			System.out.println("!running");
			return;
		}

		if (o.getStatus() != AbstractNamedSimulationData.Status.NOT_PARSED) {
			// TODO Debug
			System.out.println("NOT_PARSED: " + o.getClass().getName() +" / "+o.getName() + " / "+ o.getStatus() );
			return;
		}
		System.out.println("parse: " + o.getName());

		if (o instanceof MesoData) {
			MesoData d = (MesoData) o;

			Vector<AbstractNamedSimulationData> sources = control.getModel().getSource(o);
			checkCode(d.getDataX(), sources);
			checkCode(d.getDataY(), sources);

			control.getModel().fireObjectChangedAutoparser(d);
			return;
		}

		if (o.getStatus() != AbstractNamedSimulationData.Status.NOT_PARSED) {
			return;
		}

		Vector<AbstractNamedSimulationData> sources = control.getModel().getSource(o);
		checkCode(o, sources);

		control.getModel().fireObjectChangedAutoparser(o);
	}

	private void checkCode(NamedFormulaData o, Vector<AbstractNamedSimulationData> sources) {
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
	}

	public void stop() {
		running = false;
	}

	public void start() {
		if (running == true) {
			return;
		}
		running = true;

		SimulationXYModel model = control.getModel();
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				parse((AbstractNamedSimulationData) d);
			}
		}
	}
}
