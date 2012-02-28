package ch.zhaw.simulation.filehandling.contents;

import java.awt.Point;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.filehandling.XmlHelper;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;

public class XmlContentsLoader implements XmlContentsNames {
	private Vector<Node> parameterConnectors = new Vector<Node>();
	private Vector<Node> flowConnectors = new Vector<Node>();

	public XmlContentsLoader() {
	}

	private void parseNode(Node node, SimulationFlowModel model) {
		String name = node.getNodeName();

		if (XML_ELEMENT_CONTAINER.equals(name)) {
			SimulationContainer o = new SimulationContainer(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if (XML_ELEMENT_PARAMETER.equals(name)) {
			SimulationParameter o = new SimulationParameter(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if (XML_ELEMENT_GLOBAL.equals(name)) {
			SimulationGlobal o = new SimulationGlobal(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if (XML_ELEMENT_CONNECTOR.equals(name)) {
			// Connectors erst am Schluss parsen, das sicher alles geladen ist
			parameterConnectors.add(node);
		} else if (XML_ELEMENT_FLOW_CONNECTOR.equals(name)) {
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
		int x = XmlHelper.getAttributeInt(node, "x");
		int y = XmlHelper.getAttributeInt(node, "y");
		int width = XmlHelper.getAttributeInt(node, "width");
		int height = XmlHelper.getAttributeInt(node, "height");

		o.setX(x);
		o.setY(y);
		o.setWidth(width);
		o.setHeight(height);
		o.setText(XmlHelper.getAttribute(node, "text"));
	}

	private void parseConnector(Node node, ParameterConnector c, SimulationFlowModel model) {

		String sFrom = XmlHelper.getAttribute(node, "from");
		String sTo = XmlHelper.getAttribute(node, "to");

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

	private void parseConnector(Node node, FlowConnector c, SimulationFlowModel model) {

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
			c.getValve().setName(nName.getNodeValue());
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
			c.getValve().setX(p.x);
			c.getValve().setY(p.y);
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

	private boolean parseConnectors(SimulationFlowModel model) {
		boolean error = false;

		for (Node node : flowConnectors) {
			if ("flowConnector".equals(node.getNodeName())) {
				try {
					FlowConnector c = new FlowConnector(null, null);
					parseConnector(node, c, model);

					Node nValue = node.getAttributes().getNamedItem("value");
					if (nValue != null) {
						c.getValve().setFormula(nValue.getNodeValue());
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
		int x = XmlHelper.getAttributeInt(node, "x");
		int y = XmlHelper.getAttributeInt(node, "y");

		o.setX(x);
		o.setY(y);
		o.setName(XmlHelper.getAttribute(node, "name"));

		Node value = node.getAttributes().getNamedItem("value");
		if (value != null) {
			o.setFormula(value.getNodeValue());
		}
	}

	/**
	 * Parses a Content XML File
	 * 
	 * @param doc
	 *            The model to be overwritten
	 * @param in
	 *            The Inputstream
	 * @return true if everthing OK, false if some problems occured, but the
	 *         file was read anyway
	 * @throws Exception
	 *             If something went wrong, the file cannot be read
	 */
	public boolean parseXml(SimulationDocument doc, InputStream in) throws Exception {
//		flowConnectors.clear();
//		parameterConnectors.clear();
//
//		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
//
//		// Mögliche Kommentare überlesen
//		NodeList rootNodes = document.getChildNodes();
//		Node root = null;
//		for (int i = 0; i < rootNodes.getLength(); i++) {
//			Node n = rootNodes.item(i);
//			if (n.getNodeType() == Node.ELEMENT_NODE) {
//				root = n;
//				break;
//			}
//		}
//
//		if (root == null) {
//			throw new Exception("Root node not found!");
//		}
//
//		if (!XML_ROOT.equals(root.getNodeName())) {
//			throw new Exception("Root node name != " + XML_ROOT + "!");
//		}
//
//		NodeList nodes = root.getChildNodes();
//		for (int i = 0; i < nodes.getLength(); i++) {
//			Node n = nodes.item(i);
//
//			if (n.getNodeType() == Node.ELEMENT_NODE) {
//				parseNode(n, doc);
//			}
//		}
//
//		boolean result = parseConnectors(doc);
//
//		doc.calculateIds();
//
//		return result;
		
		return false;
	}
}
