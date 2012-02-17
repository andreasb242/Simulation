package ch.zhaw.simulation.filehandling.configuration;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.zhaw.simulation.filehandling.XmlHelper;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;

public class XmlConfigurationLoader implements XmlConfigurationNames {
	public XmlConfigurationLoader() {
	}

	private void parseNode(Node node, SimulationConfiguration config) {
		String name = node.getNodeName();

		if (XML_PARAMETER_STRING.equals(name)) {
			name = XML_PARAMETER_STRING;
		} else if (XML_PARAMETER_DOUBLE.equals(name)) {
			name = XML_PARAMETER_DOUBLE;
		} else {
			throw new RuntimeException("Node name \"" + name + "\" unknown!");
		}

		String n = XmlHelper.getAttribute(node, "name");
		String v = XmlHelper.getAttribute(node, "value");

		if (n == null) {
			throw new RuntimeException("Node name \"" + name + "\" unknown has no name attribute!");
		}
		if (v == null) {
			throw new RuntimeException("Node name \"" + name + "\" unknown has no value attribute!");
		}

		if (name == XML_PARAMETER_STRING) {
			config.setParameter(n, v);
		} else if (name == XML_PARAMETER_DOUBLE) {
			double d = 0;
			try {
				d = Double.parseDouble(v);
				config.setParameter(n, d);
			} catch (Exception e) {
				throw new RuntimeException("Node name \"" + name + "\" unknown has invalid double value: \"" + v + "\"!");
			}
		}
	}

	public void parseXml(SimulationConfiguration simulationConfiguration, InputStream in) throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

		// Mögliche Kommentare überlesen
		NodeList rootNodes = document.getChildNodes();
		Node root = null;
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node n = rootNodes.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				root = n;
				break;
			}
		}

		if (root == null) {
			throw new Exception("Root node not found!");
		}

		if (!XML_ROOT.equals(root.getNodeName())) {
			throw new Exception("Root node name != " + XML_ROOT + "!");
		}

		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseNode(n, simulationConfiguration);
			}
		}
	}
}
