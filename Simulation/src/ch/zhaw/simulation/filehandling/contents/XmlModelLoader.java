package ch.zhaw.simulation.filehandling.contents;

import java.awt.Point;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.filehandling.XmlHelper;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.xy.XYModel;

public class XmlModelLoader implements XmlContentsNames {
	private Vector<Node> parameterConnectors = new Vector<Node>();
	private Vector<Node> flowConnectors = new Vector<Node>();

	public XmlModelLoader() {
	}

	private void parseNode(Node node, AbstractSimulationModel<?> model) {
		String name = node.getNodeName();

		// even if some elemnts are not allowed within a XY Model, don't check
		// if the file is manupulated the model may not work, but there is no
		// "risk"

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
		} else if (XML_ELEMENT_TEXT.equals(name)) {
			TextData o = new TextData(0, 0);
			parseSimulationText(node, o);
			model.addData(o);
		} else if (XML_ELEMENT_DENSITY_CONTAINER.equals(name)) {
			SimulationDensityContainer o = new SimulationDensityContainer(0, 0);
			parseSimulationObject(node, o);
			model.addData(o);

		} else {
			throw new RuntimeException("Node name \"" + name + "\" unknown!");
		}
	}

	private void parseSimulationText(Node node, TextData o) {
		int x = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_X);
		int y = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_Y);
		int width = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_WIDTH);
		int height = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_HEIGHT);

		o.setX(x);
		o.setY(y);
		o.setWidth(width);
		o.setHeight(height);
		o.setText(XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_TEXT));
	}

	private void parseConnector(Node node, ParameterConnector c, SimulationFlowModel model) {

		String sFrom = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_FROM);
		String sTo = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_TO);

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

		Node nFrom = node.getAttributes().getNamedItem(XML_ELEMENT_ATTRIB_FROM);
		Node nTo = node.getAttributes().getNamedItem(XML_ELEMENT_ATTRIB_TO);
		Node nName = node.getAttributes().getNamedItem(XML_ELEMENT_ATTRIB_NAME);

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
			if (XML_ELEMENT_INFINITE.equals(n.getNodeName())) {
				int x = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_X);
				int y = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_Y);

				InfiniteData d = new InfiniteData(x, y);

				Node connectorNode = n.getAttributes().getNamedItem(XML_ELEMENT_CONNECTOR);
				if (connectorNode == null) {
					throw new RuntimeException("Error parsing FlowConnector: infinite connector is null");
				}

				String connector = connectorNode.getNodeValue();
				if (XML_ELEMENT_ATTRIB_FROM.equals(connector)) {
					from = d;
				} else if (XML_ELEMENT_ATTRIB_TO.equals(connector)) {
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

			if (n.getNodeType() == Node.ELEMENT_NODE && XML_ELEMENT_POINT.equals(n.getNodeName())) {
				int x = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_X);
				int y = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_Y);

				points.add(new Point(x, y));
			}
		}

		return points;
	}

	private boolean parseConnectors(SimulationFlowModel model) {
		boolean error = false;

		for (Node node : flowConnectors) {
			if (XML_ELEMENT_FLOW_CONNECTOR.equals(node.getNodeName())) {
				try {
					FlowConnector c = new FlowConnector(null, null);
					parseConnector(node, c, model);

					Node nValue = node.getAttributes().getNamedItem(XML_ELEMENT_ATTRIB_VALUE);
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
			if (XML_ELEMENT_CONNECTOR.equals(node.getNodeName())) {
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
		int x = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_X);
		int y = XmlHelper.getAttributeInt(node, XML_ELEMENT_ATTRIB_Y);

		o.setX(x);
		o.setY(y);
		o.setName(XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_NAME));

		Node value = node.getAttributes().getNamedItem(XML_ELEMENT_ATTRIB_VALUE);
		if (value != null) {
			o.setFormula(value.getNodeValue());
		}
	}

	/**
	 * Parses the contents of a flow model
	 * 
	 * @param flowModel
	 *            The model to be overwritten
	 * @param root
	 *            The root node of the model
	 * @throws Exception
	 *             If something went wrong, the file cannot be read
	 */
	public boolean load(SimulationFlowModel flowModel, Node root) throws Exception {
		flowConnectors.clear();
		parameterConnectors.clear();

		if (root == null) {
			throw new NullPointerException("root == null");
		}

		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseNode(n, flowModel);
			}
		}

		boolean result = parseConnectors(flowModel);

		flowModel.calculateIds();

		return result;
	}

	/**
	 * Parses the contents of a flow model
	 * 
	 * @param xyModel
	 *            The model to be overwritten
	 * @param root
	 *            The root node of the model
	 * @throws Exception
	 *             If something went wrong, the file cannot be read
	 */
	public boolean load(XYModel xyModel, Node root) {
		if (root == null) {
			throw new NullPointerException("root == null");
		}

		// TODO: parse densisty
		
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseNode(n, xyModel);
			}
		}

		return true;
	}

}
