package ch.zhaw.simulation.inexport.madonna;

import java.io.IOException;
import java.io.PushbackInputStream;

import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;

public interface FileImporter {

	boolean load(SimulationFlowModel model) throws ImportException;

	void read(PushbackInputStream in) throws IOException, ImportException;

}
