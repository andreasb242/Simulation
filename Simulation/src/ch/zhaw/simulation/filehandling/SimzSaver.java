package ch.zhaw.simulation.filehandling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import ch.zhaw.simulation.filehandling.configuration.XmlConfigurationSaver;
import ch.zhaw.simulation.filehandling.contents.XmlContentsSaver;
import ch.zhaw.simulation.model.SimulationDocument;

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

	public void save(File file, SimulationDocument doc) throws IOException, ParserConfigurationException, TransformerException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fout = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream(fout);

		try {
			out.setComment("Simulation file");
			out.putNextEntry(new ZipEntry("mimetype"));
			out.write("application/zhaw.simulation.project".getBytes());
			out.putNextEntry(new ZipEntry("version"));
			out.write(("version=" + SIMZ_VERSION + "\ncompatible=" + SIMZ_VERSION_COMPATIBLE).getBytes());

			out.putNextEntry(new ZipEntry("simulation.xml"));

			contentsSaver.saveContents(out, doc);

			out.putNextEntry(new ZipEntry("configuration.xml"));

			configurationSaver.save(out, doc.getSimulationConfiguration());

		} finally {
			out.close();
			fout.close();
		}
	}

}
