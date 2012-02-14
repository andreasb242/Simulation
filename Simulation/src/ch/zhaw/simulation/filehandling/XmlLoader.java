package ch.zhaw.simulation.filehandling;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.zhaw.simulation.model.InfiniteData;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.TextData;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.ParameterConnector;

import butti.javalibs.errorhandler.Errorhandler;

public class XmlLoader {

	private ZipInputStream in;
	private boolean versionCompatible;
	private boolean versionOk;
	private Properties properties;
	private String xmlString;
	private Vector<Node> parameterConnectors = new Vector<Node>();
	private Vector<Node> flowConnectors = new Vector<Node>();

	public boolean open(File selectedFile) {
		versionCompatible = false;
		versionOk = false;
		properties = new Properties();
		flowConnectors.clear();
		parameterConnectors.clear();
		xmlString = "";

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
					parseXml(in);
				}
			}

			return true;
		} catch (Exception e) {
			Errorhandler.logError(e, "Read file failed");
			return false;
		}
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

				if (v == XmlSaver.VERSION) {
					versionOk = true;
					versionCompatible = true;
					break;
				} else {
					versionOk = false;
				}
			} else if (line.startsWith("compatible=")) {
				int v = Integer.parseInt(line.substring(11));
				if (v <= XmlSaver.VERSION) {
					versionCompatible = true;
					break;
				}
			}
		}
	}

	private void parseXml(ZipInputStream in) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		InputStreamReader reader = new InputStreamReader(in);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		xmlString = fileData.toString();
	}

	public boolean versionCompatible() {
		return versionCompatible;
	}

	public boolean versionOk() {
		return versionOk;
	}

	public boolean load(SimulationDocument model) {
		model.clearMetadata();

		for (Object key : properties.keySet()) {
			model.putMetainf(key.toString(), properties.getProperty(key.toString()));
		}

		model.clear();

		try {
			return parseXmlString(model);
		} catch (Exception e) {
			Errorhandler.logError(e, "Error occured while loading");
			return false;
		}
	}

	private void parseNode(Node node, SimulationDocument model) {
		String name = node.getNodeName();

		if ("container".equals(name)) {
			SimulationContainer o = new SimulationContainer(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if ("parameter".equals(name)) {
			SimulationParameter o = new SimulationParameter(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if ("global".equals(name)) {
			SimulationGlobal o = new SimulationGlobal(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if ("connector".equals(name)) {
			// Connectors erst am Schluss parsen, das sicher alles geladen ist
			parameterConnectors.add(node);
		} else if ("flowConnector".equals(name)) {
			flowConnectors.add(node);
		} else if ("text".equals(name)) {
			TextData o = new TextData(0, 0);
			parseSimulationText(node, o);
			model.addData(o);
		} else {
			throw new RuntimeException("Node name \"" + name + "\" unknown!");
		}
	}

	private void parseSimulationText(Node node, TextData o) {
		int x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());
		int width = Integer.parseInt(node.getAttributes().getNamedItem("width").getNodeValue());
		int height = Integer.parseInt(node.getAttributes().getNamedItem("height").getNodeValue());

		o.setX(x);
		o.setY(y);
		o.setWidth(width);
		o.setHeight(height);
		o.setText(node.getAttributes().getNamedItem("text").getNodeValue());
	}

	private void parseConnector(Node node, ParameterConnector c, SimulationDocument model) {

		String sFrom = node.getAttributes().getNamedItem("from").getNodeValue();
		String sTo = node.getAttributes().getNamedItem("to").getNodeValue();

		NamedSimulationObject from = model.getByName(sFrom);
		NamedSimulationObject to = model.getByName(sTo);

		if (from == null) {
			throw new RuntimeException("from not available! \"" + sFrom + "\"");
		}
		if (to == null) {
			throw new RuntimeException("to not available! \"" + sTo + "\"");
		}

		c.setSource(from);
		c.setTarget(to);
	}

	private void parseConnector(Node node, FlowConnector c, SimulationDocument model) {

		Node nFrom = node.getAttributes().getNamedItem("from");
		Node nTo = node.getAttributes().getNamedItem("to");
		Node nName = node.getAttributes().getNamedItem("name");

		SimulationObject from = null;
		SimulationObject to = null;

		if (nFrom != null) {
			from = model.getByName(nFrom.getNodeValue());

			if (from == null) {
				throw new RuntimeException("from not available! \"" + nFrom.getNodeValue() + "\"");
			}
		}

		if (nTo != null) {
			to = model.getByName(nTo.getNodeValue());

			if (to == null) {
				throw new RuntimeException("to not available! \"" + nTo.getNodeValue() + "\"");
			}
		}

		if (nName != null) {
			c.getParameterPoint().setName(nName.getNodeValue());
		}

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if ("infinite".equals(n.getNodeName())) {
				int x = Integer.parseInt(n.getAttributes().getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(n.getAttributes().getNamedItem("y").getNodeValue());

				InfiniteData d = new InfiniteData(x, y);

				Node connectorNode = n.getAttributes().getNamedItem("connector");
				if (connectorNode == null) {
					throw new RuntimeException("Error parsing FlowConnector: infinite connector is null");
				}

				String connector = connectorNode.getNodeValue();
				if ("from".equals(connector)) {
					from = d;
				} else if ("to".equals(connector)) {
					to = d;
				} else {
					throw new RuntimeException("Error parsing FlowConnector: infinite connector: \"" + connector + "\"");
				}

				model.addData(d);
			}
		}

		Vector<Point> points = parsePoints(node);

		if (points.size() > 0) {
			Point p = points.get(0);
			c.getParameterPoint().setX(p.x);
			c.getParameterPoint().setY(p.y);
		}

		if (from == null || to == null) {
			throw new RuntimeException("Error parsing FlowConnector: from and to has to be set!");
		}

		c.setSource(from);
		c.setTarget(to);
	}

	private Vector<Point> parsePoints(Node node) {
		Vector<Point> points = new Vector<Point>();

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE && "point".equals(n.getNodeName())) {
				int x = Integer.parseInt(n.getAttributes().getNamedItem("x").getNodeValue());
				int y = Integer.parseInt(n.getAttributes().getNamedItem("y").getNodeValue());

				points.add(new Point(x, y));
			}
		}

		return points;
	}

	private boolean parseConnectors(SimulationDocument model) {
		boolean error = false;

		for (Node node : flowConnectors) {
			if ("flowConnector".equals(node.getNodeName())) {
				try {
					FlowConnector c = new FlowConnector(null, null);
					parseConnector(node, c, model);

					Node nValue = node.getAttributes().getNamedItem("value");
					if (nValue != null) {
						c.getParameterPoint().setFormula(nValue.getNodeValue());
					}

					model.addConnector(c);
				} catch (Exception e) {
					error = true;
					Errorhandler.logError(e, "connector ignored");
				}
			}
		}

		for (Node node : parameterConnectors) {
			if ("connector".equals(node.getNodeName())) {
				try {
					ParameterConnector c = new ParameterConnector(null, null);
					parseConnector(node, c, model);
					Vector<Point> points = parsePoints(node);

					if (points.size() > 0) {
						c.setConnectorPoint(points.get(0));
					}

					model.addConnector(c);
				} catch (Exception e) {
					Errorhandler.logError(e, "connector ignored");
				}
			}
		}

		return !error;
	}

	private void parseSimulationObject(Node node, NamedSimulationObject o) {
		int x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());

		o.setX(x);
		o.setY(y);
		o.setName(node.getAttributes().getNamedItem("name").getNodeValue());

		Node value = node.getAttributes().getNamedItem("value");
		if (value != null) {
			o.setFormula(value.getNodeValue());
		}
	}

	private boolean parseXmlString(SimulationDocument model) throws Exception {

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlString.getBytes()));

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

		if (!"simulation".equals(root.getNodeName())) {
			throw new Exception("Root node name != simulation!");
		}

		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseNode(n, model);
			}
		}

		boolean result = parseConnectors(model);

		model.calculateIds();

		return result;
	}
}
