package ch.zhaw.simulation.plugin.matlab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

/**
 * @author: bachi
 */
public class SimulationResultParser {

	private Vector<String> filenames;
	private double start;
	private double end;
	
	public SimulationResultParser(SimulationDocument doc, SimulationConfiguration config) {
		Vector<AbstractNamedSimulationData> dataVector = new Vector<AbstractNamedSimulationData>();
		filenames = new Vector<String>();

		start = config.getParameter(StandardParameter.START, StandardParameter.DEFAULT_START);
		end = config.getParameter(StandardParameter.END, StandardParameter.DEFAULT_END);

		if (doc.getType() == SimulationType.FLOW_SIMULATION) {
			// Add containers to parser-list
			dataVector.addAll(doc.getFlowModel().getSimulationContainer());

			// Add parameters to parser-list
			dataVector.addAll(doc.getFlowModel().getSimulationParameter());

			// Add valves (part of connectors) to parser-list
			for (FlowConnectorData c : doc.getFlowModel().getFlowConnectors()) {
				dataVector.add(c.getValve());
			}

			AbstractNamedSimulationData data;
			for (int i = 0; i < dataVector.size(); i++) {
				data = dataVector.get(i);
				filenames.add(data.getName());
			}
		} else if (doc.getType() == SimulationType.XY_MODEL) {
			String prefix;
			for (MesoData meso : doc.getXyModel().getMeso()) {
				prefix = meso.getName() + ".submodel.";

				filenames.add(meso.getName() + ".position.exact.x");
				filenames.add(meso.getName() + ".position.exact.y");
				filenames.add(meso.getName() + ".position.approx.x");
				filenames.add(meso.getName() + ".position.approx.y");

				// 1) add container
				for (SimulationContainerData container : meso.getSubmodel().getModel().getSimulationContainer()) {
					filenames.add(prefix + container.getName());
				}

				// 2) add parameter
				for (SimulationParameterData parameter : meso.getSubmodel().getModel().getSimulationParameter()) {
					filenames.add(prefix + parameter.getName());
				}

				// 3) add connector
				for (FlowConnectorData connector : meso.getSubmodel().getModel().getFlowConnectors()) {
					filenames.add(prefix + connector.getValve().getName());
				}
			}
		}
	}

	public SimulationCollection parse(String workpath) {
		SimulationCollection collection;
		SimulationSerie serie;
		String line;
		String cell[];
		BufferedReader reader;

		collection = new SimulationCollection(start, end);
		
		for (String filename : filenames) {
			try {
				serie = new SimulationSerie(filename);
				reader = new BufferedReader(new FileReader(new File(workpath + File.separator + "data_" + filename + ".txt")));
				// go through every line in a file
				while ((line = reader.readLine()) != null) {
					// Tab-split line in two parts
					cell = line.split("\\t");
					if (cell.length >= 2) {
						// Add new entry (time/value) to serie
						serie.add(Double.valueOf(cell[0]).doubleValue(), Double.valueOf(cell[1]).doubleValue());
					}
				}
				collection.addSerie(serie);
			} catch (FileNotFoundException e) {
				Errorhandler.logError(e, e.getMessage());
			} catch (IOException e) {
				Errorhandler.logError(e, e.getMessage());
			}
		}

		return collection;
	}
}
