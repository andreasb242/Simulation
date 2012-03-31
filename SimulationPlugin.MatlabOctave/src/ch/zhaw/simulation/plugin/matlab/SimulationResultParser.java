package ch.zhaw.simulation.plugin.matlab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

/**
 * @author: bachi
 */
public class SimulationResultParser {
	private SimulationDocument document;
	private SimulationConfiguration config;

	public SimulationResultParser(SimulationDocument document, SimulationConfiguration config) {
		this.document = document;
		this.config = config;
	}

	public SimulationCollection parse(String workpath) {
		SimulationCollection collection;
		SimulationSerie serie;
		String line;
		String cell[];
		Vector<AbstractNamedSimulationData> dataVector = new Vector<AbstractNamedSimulationData>();
		BufferedReader reader;

		double start;
		double end;

		start = config.getParameter(StandardParameter.START, StandardParameter.DEFAULT_START);
		end = config.getParameter(StandardParameter.END, StandardParameter.DEFAULT_END);
		collection = new SimulationCollection(start, end);

		dataVector.addAll(document.getFlowModel().getSimulationContainer());
		dataVector.addAll(document.getFlowModel().getSimulationParameter());
		for (AbstractNamedSimulationData data : dataVector) {
			try {
				serie = new SimulationSerie(data.getName());
				reader = new BufferedReader(new FileReader(new File(workpath + File.separator + data.getName() + "_data.txt")));
				while ((line = reader.readLine()) != null) {
					cell = line.split("\\t");
					if (cell.length >= 2) {
						serie.add(Double.valueOf(cell[0]).doubleValue(), Double.valueOf(cell[1]).doubleValue());
					}
				}
				collection.addSeries(serie);
			} catch (FileNotFoundException e) {
				Errorhandler.logError(e, e.getMessage());
			} catch (IOException e) {
				Errorhandler.logError(e, e.getMessage());
			}
		}

		return collection;
	}
}
