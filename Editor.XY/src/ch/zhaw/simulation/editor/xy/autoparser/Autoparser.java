package ch.zhaw.simulation.editor.xy.autoparser;

import java.util.Vector;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.NamedFormulaData.Status;
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

	private Status parseMesoPart(Vector<AbstractNamedSimulationData> sources, NamedFormulaData d) {
		try {
			parser.checkCode(d.getFormula(), d, control.getModel(), sources, d.getName());
			return AbstractNamedSimulationData.Status.SYNTAX_OK;
		} catch (CompilerError e) {
			return AbstractNamedSimulationData.Status.SYNTAX_ERROR;
		} catch (SimulationModelException e) {
			return AbstractNamedSimulationData.Status.SYNTAX_ERROR;
		} catch (Exception e) {
			Errorhandler.logError(e);
			return AbstractNamedSimulationData.Status.SYNTAX_ERROR;
		}
	}

	private void parse(AbstractNamedSimulationData o) {
		if (!running) {
			return;
		}

		if (o instanceof MesoData) {
			MesoData d = (MesoData) o;

			Vector<AbstractNamedSimulationData> sources = control.getModel().getSource(o);
			Status s1 = parseMesoPart(sources, d.getDataX());
			Status s2 = parseMesoPart(sources, d.getDataY());

			if (s1 == AbstractNamedSimulationData.Status.SYNTAX_OK && s2 == AbstractNamedSimulationData.Status.SYNTAX_OK) {
				o.setStaus(AbstractNamedSimulationData.Status.SYNTAX_OK, null);
			} else {
				o.setStaus(AbstractNamedSimulationData.Status.SYNTAX_ERROR, null);
			}
			
			control.getModel().fireObjectChanged(o);
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

		SimulationXYModel model = control.getModel();
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				parse((AbstractNamedSimulationData) d);
			}
		}
	}
}
