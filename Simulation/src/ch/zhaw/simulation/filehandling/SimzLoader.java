package ch.zhaw.simulation.filehandling;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ch.zhaw.simulation.filehandling.configuration.XmlConfigurationLoader;
import ch.zhaw.simulation.filehandling.contents.XmlContentsLoader;
import ch.zhaw.simulation.model.flow.SimulationDocument;

/**
 * Loads a .sizm file
 * 
 * @author Andreas Butti
 */
public class SimzLoader implements SimzFileVersion {
	private ZipInputStream in;
	private boolean versionCompatible;
	private boolean versionOk;
	private Properties properties;
	private String xmlContents;
	private String xmlConfiguration;

	private XmlContentsLoader contentsLoader = new XmlContentsLoader();
	private XmlConfigurationLoader configLoader = new XmlConfigurationLoader();

	public void open(File selectedFile) throws IOException, Exception {
		versionCompatible = false;
		versionOk = false;
		properties = new Properties();

		try {
			in = new ZipInputStream(new FileInputStream(selectedFile));

			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				String name = entry.getName();

				if ("mimetype".equals(name)) {
					continue;
				} else if ("version".equals(name)) {
					parseVersion(in);
				} else if ("metainf".equals(name)) {
					parseMetainf(in);
				} else if ("simulation.xml".equals(name)) {
					this.xmlContents = readFileToString(in);
				} else if ("configuration.xml".equals(name)) {
					this.xmlConfiguration = readFileToString(in);
				} else {
					System.err.println(".sizm file contains unexpected content: \"" + name + "\"");
				}
			}
		} finally {
			in.close();
		}
	}

	private String readFileToString(InputStream in) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		InputStreamReader reader = new InputStreamReader(in);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		return fileData.toString();
	}

	public boolean load(SimulationDocument model) throws Exception {
		model.clearMetadata();
		model.getSimulationConfiguration().clear();

		for (Object key : properties.keySet()) {
			model.putMetainf(key.toString(), properties.getProperty(key.toString()));
		}

		model.clear();

		configLoader.parseXml(model.getSimulationConfiguration(), new ByteArrayInputStream(xmlConfiguration.getBytes()));

		return contentsLoader.parseXml(model, new ByteArrayInputStream(xmlContents.getBytes()));
	}

	private void parseMetainf(ZipInputStream in) throws IOException {
		properties.load(in);
	}

	private void parseVersion(ZipInputStream in) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		InputStreamReader reader = new InputStreamReader(in);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		for (String line : fileData.toString().split("\n")) {
			line = line.trim();

			if (line.startsWith("version=")) {
				int v = Integer.parseInt(line.substring(8));

				if (v == SIMZ_VERSION) {
					versionOk = true;
					versionCompatible = true;
					break;
				} else {
					versionOk = false;
				}
			} else if (line.startsWith("compatible=")) {
				int v = Integer.parseInt(line.substring(11));
				if (v <= SIMZ_VERSION) {
					versionCompatible = true;
					break;
				}
			}
		}
	}

	public boolean versionCompatible() {
		return versionCompatible;
	}

	public boolean versionOk() {
		return versionOk;
	}

}
