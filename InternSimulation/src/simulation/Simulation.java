package simulation;

import gui.control.SimulationControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidAlgorithmParameterException;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import math.CompilerError;
import math.Parser;
import math.UserException;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationAttachment;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.Simulationtype;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;

import butti.javalibs.gui.messagebox.Messagebox;

import simulation.data.SimulationCollection;
import simulation.data.SimulationSerie;
import simulation.euler.EulerSimulation;
import simulation.gui.SimulationDiagrammDialog;
import simulation.gui.SimulationProgress;
import simulation.rungekutta.RungeKuttaSimulation;
import butti.javalibs.errorhandler.Errorhandler;

public class Simulation {

	private SimulationDocument model;
	private SimulationControl control;

	private Parser parser = new Parser();

	private Vector<FlowConnector> flowConnectors = new Vector<FlowConnector>();

	private Vector<NamedSimulationObject> dataObjects = new Vector<NamedSimulationObject>();

	private Simulationtype type;

	private AbstractSimulation simulation;
	private SimulationProgress progressDialog;
	private double dt;

	public enum CheckState {
		OK, NO_DATA, ERROR
	};

	public Simulation(SimulationControl control) {
		this.model = control.getModel();
		this.control = control;

		type = model.getSimModel().getType();
	}

	public void startSimulation() {
		// TODO: Phasendiagramm

		// TODO: Batch run

		progressDialog = new SimulationProgress(control.getParent());
		progressDialog.setVisible(true);

		final JProgressBar progressBar = progressDialog.getProgress();

		try {

			initSimulationAttachment();

			// Zusammenhänge berechnen
			calcSources();

			// Zusammenänge auf rekursion Prüfen
			checkRecursion();

			SimulationCollection series = initSeries();

			// Alle Formeln parsen, optimieren und ggf. berechnen
			parseFormulas();

			// Initwert für Container berechnen
			initData();

			initSimulate();
			simulation.setSeries(series);

			progressDialog.getCancelButton().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					simulation.cancel(false);
				}
			});

			simulation.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equals(evt.getPropertyName())) {
						progressBar.setValue((Integer) evt.getNewValue());
					} else if ("state".equals(evt.getPropertyName())) {
						if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
							displayDiagramm();
						}
					}
				}
			});

		} catch (UserException e) {
			Messagebox.showError(control.getParent(), "Simulationsfehler", e.getMessage());
		} catch (Exception e) {
			Errorhandler.showError(e, "Simulation fehlgeschlagen!");
		} finally {
			// Synchronisationsproblem...
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					progressDialog.setVisible(false);
				}
			});
		}

	}

	protected void displayDiagramm() {
		try {
			SimulationCollection series = simulation.get();

			SimulationDiagrammDialog dia = new SimulationDiagrammDialog(control, series);
			dia.setVisible(true);
		} catch (java.util.concurrent.CancellationException e) {
			Messagebox.showInfo(control.getParent(), "Abgebrochen", "Die Simulaton wurd abgebrochen.");
		} catch (Exception e) {
			Errorhandler.showError(e, "Fehler bei der Simulation aufgetreten.");
		}
		progressDialog.setVisible(false);
	}

	protected SimulationCollection initSeries() {
		SimulationCollection series = new SimulationCollection(model.getSimModel().getStartTime(), model.getSimModel().getEndTime());
		Vector<NamedSimulationObject> namedObjects = model.getNamedSimulationObject();

		Collections.sort(namedObjects);

		for (NamedSimulationObject c : namedObjects) {
			String type = "";

			if (c instanceof FlowParameterPoint) {
				type = "flow";
			} else if (c instanceof SimulationContainer) {
				type = "container";
			} else if (c instanceof SimulationParameter) {
				type = "parameter";
			} else {
				type = c.getClass().getName();
			}

			SimulationSerie serie = new SimulationSerie(c.getName() + " " + type);
			c.a.serie = serie;
			series.addSeries(serie);
		}

		return series;
	}

	private void parseFormulas() throws CompilerError, ParseException, UserException.NotUsedException {
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				parseFormula((NamedSimulationObject) d);
			}
		}

		// Wenn möglich optimieren (Parameter die nur von konstanten Parametern
		// abhängig sind auch konstant machen)
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				checkIfConst((NamedSimulationObject) d);
			}
		}

		// Constwerte auslesen
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				setConstValue((NamedSimulationObject) d);
			}
		}
	}

	private void setConstValue(NamedSimulationObject d) {
		if (d instanceof SimulationContainer) {
			// TODO: Container dürfen nur Konstant sein wenn keine Ein- Und
			// Ausflüsse vorhanden sind!
			return;
		}

		Object value = d.a.getValue();
		if (value != null) {
			if (value instanceof Double) {
				d.a.serie.setConbstValue(((Double) value).doubleValue());
			} else {
				// TODO: handle complex values, vectors etc.
				d.a.serie.setConbstValue(0);
			}
		}
	}

	private void checkIfConst(NamedSimulationObject d) throws ParseException {
		d.a.optimize2();
	}

	private void parseFormula(NamedSimulationObject d) throws CompilerError, ParseException, UserException.NotUsedException {
		Vector<NamedSimulationObject> sources = new Vector<NamedSimulationObject>();
		sources.addAll(d.a.getSources());

		d.a.setParsed(parser.checkCode(d.getFormula(), d, sources, control, d.getName()));
		d.a.assigneSourcesVars();
		d.a.optimize();
	}

	private void initSimulationAttachment() {
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				NamedSimulationObject n = (NamedSimulationObject) d;
				n.a = new SimulationAttachment();
			}
		}

		for (FlowConnector c : model.getFlowConnectors()) {
			c.getParameterPoint().a = new SimulationAttachment();
			flowConnectors.add(c);
		}
	}

	/**
	 * Zusammenhänge analysieren
	 * 
	 * @throws UserException
	 *             Wenn keine Flüsse vorhanden sind
	 */
	private void calcSources() throws UserException {
		dataObjects.clear();
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				calcSources((NamedSimulationObject) d);
				dataObjects.add((NamedSimulationObject) d);
			}
		}

		for (FlowConnector c : flowConnectors) {
			calcSources(c.getParameterPoint());
			dataObjects.add(c.getParameterPoint());
		}

		if (flowConnectors.size() == 0) {
			throw new UserException("Keine Flüsse vorhanden, es kann nichts simuliert werden!");
		}
	}

	private void calcSources(NamedSimulationObject s) {
		Vector<NamedSimulationObject> sources = control.getModel().getSource(s);
		s.a.setSources(sources);
	}

	private void initSimulate() throws CompilerError, ParseException, InvalidAlgorithmParameterException, InterruptedException, ExecutionException {
		switch (type) {
		case EULER:
			simulation = new EulerSimulation(this);
			break;
		case RUNGE_KUTTA4:
			simulation = new RungeKuttaSimulation(this);
			break;
		default:
			throw new RuntimeException("Simulation " + type.toString() + " not found!");
		}

		simulation.execute();
	}

	private void checkRecursion() throws RecursionException {
		Vector<NamedSimulationObject> dataObjects = new Vector<NamedSimulationObject>();
		dataObjects.addAll(this.dataObjects);

		int maxRun = dataObjects.size();
		while (maxRun > 0) {
			if (dataObjects.size() == 0) {
				return;
			}

			int removed = 0;
			for (int i = 0; i < dataObjects.size(); i++) {
				NamedSimulationObject s = dataObjects.get(i);
				if (s.a.getSources().size() == 0) {
					dataObjects.remove(s);
					removed++;
					continue;
				}

				boolean relations = false;

				for (NamedSimulationObject o : s.a.getSources()) {
					if (dataObjects.contains(o)) {
						relations = true;
						break;
					}
				}

				if (!relations) {
					dataObjects.remove(s);
					removed++;
				}
			}

			if (removed == 0) {
				break;
			}
			maxRun--;
		}

		if (dataObjects.size() == 0) {
			return;
		}

		StringBuilder text = new StringBuilder();
		for (SimulationObject o : dataObjects) {
			String name = "<unkown>(" + o.hashCode() + ")";
			if (o instanceof NamedSimulationObject) {
				name = ((NamedSimulationObject) o).getName();
			}

			text.append(", ");
			text.append(name);
		}
		throw new RecursionException("Cycle detected: " + text.substring(2));
	}

	private void initData() throws CompilerError, ParseException {
		this.dt = model.getSimModel().getDt();
		for (SimulationContainer d : model.getSimulationContainer()) {
			calcInitData(d);
		}
	}

	private void calcInitData(SimulationContainer d) throws CompilerError, ParseException {
		d.a.setContainerValue(d.a.calc(0, dt));
	}

	/**
	 * Prüft ob alle Formeln gültig sind
	 * 
	 * @return
	 */
	public CheckState checkData() {
		if (!model.containsContainerOrParameter()) {
			// Keine Daten
			return CheckState.NO_DATA;
		}

		for (SimulationContainer d : model.getSimulationContainer()) {
			if (d.getStaus() != NamedSimulationObject.Status.SYNTAX_OK) {
				return CheckState.ERROR;
			}
		}

		for (FlowConnector c : model.getFlowConnectors()) {
			if (c.getParameterPoint().getStaus() != NamedSimulationObject.Status.SYNTAX_OK) {
				return CheckState.ERROR;
			}
		}

		return CheckState.OK;
	}

	public SimulationDocument getModel() {
		return model;
	}
}
