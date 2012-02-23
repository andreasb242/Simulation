package ch.zhaw.simulation.filehandling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import ch.zhaw.simulation.filehandling.configuration.XmlConfigurationSaver;
import ch.zhaw.simulation.filehandling.contents.XmlContentsSaver;
import ch.zhaw.simulation.model.flow.SimulationDocument;

/**
 * Saves a .simz file
 * 
 * @author Andreas Butti
 */
public class SimzSaver implements SimzFileVersion {
	private XmlContentsSaver contentsSaver = new XmlContentsSaver();
	private XmlConfigurationSaver configurationSaver = new XmlConfigurationSaver();
	
	public SimzSaver() {
	}

	public void save(File file, SimulationDocument model) throws IOException, ParserConfigurationException, TransformerException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fout = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream(fout);

		try {
			out.setComment("Simulation file");
			out.putNextEntry(new ZipEntry("mimetype"));
			out.write("application/butti.simulation.project".getBytes());
			out.putNextEntry(new ZipEntry("version"));
			out.write(("version=" + SIMZ_VERSION + "\ncompatible=" + SIMZ_VERSION_COMPATIBLE).getBytes());
			out.putNextEntry(new ZipEntry("metainf"));
			storeMetainf(out, model);

			out.putNextEntry(new ZipEntry("simulation.xml"));
			contentsSaver.saveContents(out, model);

			out.putNextEntry(new ZipEntry("configuration.xml"));

			configurationSaver.save(out, model.getSimulationConfiguration());

		} finally {
			out.close();
			fout.close();
		}
	}

	private void storeMetainf(OutputStream out, SimulationDocument model) throws IOException {
		Properties p = new Properties();

		for (String key : model.getMetainfKeys()) {
			p.put(key, model.getMetainf(key));
		}

		p.store(out, "Simulation metainformations");
	}

}
