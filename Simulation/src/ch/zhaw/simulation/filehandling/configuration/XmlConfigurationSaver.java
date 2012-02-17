package ch.zhaw.simulation.filehandling.configuration;

import java.io.OutputStream;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import ch.zhaw.simulation.filehandling.AbstractXmlSaver;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;

public class XmlConfigurationSaver extends AbstractXmlSaver implements XmlConfigurationNames {
	private Element root;

	public XmlConfigurationSaver() {
	}

	public void save(OutputStream out, SimulationConfiguration config) throws ParserConfigurationException, TransformerException {
		this.root = initDocument(XML_ROOT);

		for (Entry<String, Double> entry : config.getDoubleParameter().entrySet()) {
			saveValue("double", entry.getKey(), String.valueOf(entry.getValue()));
		}

		for (Entry<String, String> entry : config.getStringParameter().entrySet()) {
			saveValue("string", entry.getKey(), entry.getValue());
		}

		saveOutDocument(out);
	}

	private void saveValue(String type, String name, String value) {
		Element element = document.createElement(type);
		element.setAttribute("name", name);
		element.setAttribute("value", value);

		this.root.appendChild(element);
	}

}
