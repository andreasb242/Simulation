package ch.zhaw.simulation.sim.intern.main;

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

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import org.nfunk.jep.ParseException;
import org.omg.CORBA.UserException;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.EmptyFormulaException;
import ch.zhaw.simulation.math.exception.NotUsedException;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.plugin.PluginDataProvider;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.sim.intern.InternSimulationParameter;
import ch.zhaw.simulation.sim.intern.euler.EulerSimulation;
import ch.zhaw.simulation.sim.intern.gui.SimulationProgress;
import ch.zhaw.simulation.sim.intern.gui.TableDialog;
import ch.zhaw.simulation.sim.intern.model.SimulationAttachment;
import ch.zhaw.simulation.sim.intern.rungekutta.RungeKuttaSimulation;

public class Simulation {
	private SimulationDocument doc;
	private SimulationFlowModel model;
	private PluginDataProvider provider;

	private Parser parser = new Parser();

	private Vector<FlowConnectorData> flowConnectors = new Vector<FlowConnectorData>();

	private Vector<AbstractNamedSimulationData> dataObjects = new Vector<AbstractNamedSimulationData>();

	private int type;

	private AbstractSimulation simulation;
	private SimulationProgress progressDialog;
	private double dt;

	private Settings settings;

	public enum CheckState {
		OK, NO_DATA, ERROR
	};

	public Simulation(PluginDataProvider provider, Settings settings, SimulationDocument doc) {
		this.provider = provider;
		this.doc = doc;
		this.settings = settings;
		this.model = doc.getFlowModel();

		type = (int) doc.getSimulationConfiguration().getParameter(InternSimulationParameter.TYPE, 0);
	}

	public void startSimulation() {
		progressDialog = new SimulationProgress(provider.getParent());
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

		} catch (SimulationModelException e) {
			Messagebox.showError(provider.getParent(), "Simulationsfehler", e.getMessage());
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

			TableDialog dlg = new TableDialog(provider, settings, series);
			dlg.setVisible(true);
		} catch (java.util.concurrent.CancellationException e) {
			Messagebox.showInfo(provider.getParent(), "Abgebrochen", "Die Simulaton wurd abgebrochen.");
		} catch (Exception e) {
			Errorhandler.showError(e, "Fehler bei der Simulation aufgetreten.");
		}
		progressDialog.setVisible(false);
	}

	protected SimulationCollection initSeries() {
		double start = doc.getSimulationConfiguration().getParameter(StandardParameter.START, 0);
		double end = doc.getSimulationConfiguration().getParameter(StandardParameter.END, 10);
		SimulationCollection series = new SimulationCollection(start, end);

		Vector<AbstractNamedSimulationData> namedObjects = model.getNamedSimulationObject();

		Collections.sort(namedObjects);

		for (AbstractNamedSimulationData c : namedObjects) {
			String type = "";

			if (c instanceof FlowValveData) {
				type = "flow";
			} else if (c instanceof SimulationContainerData) {
				type = "container";
			} else if (c instanceof SimulationParameterData) {
				type = "parameter";
			} else {
				type = c.getClass().getName();
			}

			SimulationSerie serie = new SimulationSerie(c.getName() + " " + type);
			((SimulationAttachment) c.attachment).serie = serie;
			series.addSeries(serie);
		}

		return series;
	}

	private void parseFormulas() throws CompilerError, ParseException, NotUsedException, EmptyFormulaException {
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				parseFormula((AbstractNamedSimulationData) d);
			}
		}

		// Wenn möglich optimieren (Parameter die nur von konstanten Parametern
		// abhängig sind auch konstant machen)
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				checkIfConst((AbstractNamedSimulationData) d);
			}
		}

		// Constwerte auslesen
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				setConstValue((AbstractNamedSimulationData) d);
			}
		}
	}

	private void setConstValue(AbstractNamedSimulationData d) {
		if (d instanceof SimulationContainerData) {
			// TODO: Container dürfen nur Konstant sein wenn keine Ein- Und
			// Ausflüsse vorhanden sind!
			return;
		}

		Object value = ((SimulationAttachment) d.attachment).getValue();
		if (value != null) {
			if (value instanceof Double) {
				((SimulationAttachment) d.attachment).serie.setConstValue(((Double) value).doubleValue());
			} else {
				((SimulationAttachment) d.attachment).serie.setConstValue(0);
			}
		}
	}

	private void checkIfConst(AbstractNamedSimulationData d) throws ParseException {
		((SimulationAttachment) d.attachment).optimize2();
	}

	private void parseFormula(AbstractNamedSimulationData d) throws CompilerError, ParseException, NotUsedException, EmptyFormulaException {
		SimulationAttachment a = (SimulationAttachment) d.attachment;
		Vector<AbstractNamedSimulationData> sources = new Vector<AbstractNamedSimulationData>();
		sources.addAll(a.getSources());

		a.setParsed(parser.checkCode(d.getFormula(), d, this.model, sources, d.getName()));
		a.assigneSourcesVars();
		a.optimize();
	}

	private void initSimulationAttachment() {
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData n = (AbstractNamedSimulationData) d;
				n.attachment = new SimulationAttachment();
			}
		}

		for (FlowConnectorData c : model.getFlowConnectors()) {
			c.getValve().attachment = new SimulationAttachment();
			flowConnectors.add(c);
		}
	}

	/**
	 * Zusammenhänge analysieren
	 * 
	 * @throws SimulationModelException
	 * 
	 * @throws UserException
	 *             Wenn keine Flüsse vorhanden sind
	 */
	private void calcSources() throws SimulationModelException {
		dataObjects.clear();
		for (AbstractSimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				calcSources((AbstractNamedSimulationData) d);
				dataObjects.add((AbstractNamedSimulationData) d);
			}
		}

		for (FlowConnectorData c : flowConnectors) {
			calcSources(c.getValve());
			dataObjects.add(c.getValve());
		}

		if (flowConnectors.size() == 0) {
			throw new SimulationModelException(null, "Keine Flüsse vorhanden, es kann nichts simuliert werden!");
		}
	}

	private void calcSources(AbstractNamedSimulationData s) {
		Vector<AbstractNamedSimulationData> sources = model.getSource(s);
		((SimulationAttachment) s.attachment).setSources(sources);
	}

	private void initSimulate() throws CompilerError, ParseException, InvalidAlgorithmParameterException, InterruptedException, ExecutionException {
		switch (type) {
		case 0:// EULER
			simulation = new EulerSimulation(this);
			break;
		case 1:// RUNGE_KUTTA4
			simulation = new RungeKuttaSimulation(this);
			break;
		default:
			throw new RuntimeException("Simulation " + type + " not found!");
		}

		simulation.execute();
	}

	private void checkRecursion() throws RecursionException {
		Vector<AbstractNamedSimulationData> dataObjects = new Vector<AbstractNamedSimulationData>();
		dataObjects.addAll(this.dataObjects);

		int maxRun = dataObjects.size();
		while (maxRun > 0) {
			if (dataObjects.size() == 0) {
				return;
			}

			int removed = 0;
			for (int i = 0; i < dataObjects.size(); i++) {
				AbstractNamedSimulationData s = dataObjects.get(i);
				if (((SimulationAttachment) s.attachment).getSources().size() == 0) {
					dataObjects.remove(s);
					removed++;
					continue;
				}

				boolean relations = false;

				for (AbstractNamedSimulationData o : ((SimulationAttachment) s.attachment).getSources()) {
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
		for (AbstractSimulationData o : dataObjects) {
			String name = "<unkown>(" + o.hashCode() + ")";
			if (o instanceof AbstractNamedSimulationData) {
				name = ((AbstractNamedSimulationData) o).getName();
			}

			text.append(", ");
			text.append(name);
		}
		throw new RecursionException("Cycle detected: " + text.substring(2));
	}

	private void initData() throws CompilerError, ParseException {
		this.dt = doc.getSimulationConfiguration().getParameter(StandardParameter.DT, 0.1);
		for (SimulationContainerData d : model.getSimulationContainer()) {
			calcInitData(d);
		}
	}

	private void calcInitData(SimulationContainerData d) throws CompilerError, ParseException {
		((SimulationAttachment) d.attachment).setContainerValue(((SimulationAttachment) d.attachment).calc(0, dt));
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

		for (SimulationContainerData d : model.getSimulationContainer()) {
			if (d.getStaus() != AbstractNamedSimulationData.Status.SYNTAX_OK) {
				return CheckState.ERROR;
			}
		}

		for (FlowConnectorData c : model.getFlowConnectors()) {
			if (c.getValve().getStaus() != AbstractNamedSimulationData.Status.SYNTAX_OK) {
				return CheckState.ERROR;
			}
		}

		return CheckState.OK;
	}

	public SimulationDocument getDocument() {
		return doc;
	}

	public SimulationFlowModel getModel() {
		return model;
	}
}
