package ch.zhaw.simulation.inexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butti.javalibs.config.Settings;
import butti.plugin.definition.AbstractPlugin;
import ch.zhaw.simulation.dialog.settings.SettingsPanel;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;

public abstract class ImportPlugin implements AbstractPlugin {
	public ImportPlugin() {
	}

	public void init(Settings settings) {
	}

	public boolean canHandle(File file) throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(file);
		boolean res = checkFile(in);
		in.close();
		return res;
	}

	public abstract void read(File file) throws IOException, ImportException;

	public abstract boolean load(SimulationFlowModel model) throws ImportException;

	protected abstract boolean checkFile(InputStream in) throws IOException;

	public abstract String[] getFileExtension();

	public SettingsPanel getSettingsPanel() {
		return null;
	}
}
