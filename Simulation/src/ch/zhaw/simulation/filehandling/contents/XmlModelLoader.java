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
import ch.zhaw.simulation.model.flow.BezierConnectorData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
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

		// even if some elements are not allowed within a XY Model, don't check
		// if the file is manipulated the model may not work, but there is no
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
			// parse the connectors at the end, when we have all elements
			// already read
			parameterConnectors.add(node);
		} else if (XML_ELEMENT_FLOW_CONNECTOR.equals(name)) {
			// parse the connectors at the end, when we have all elements
			// already read
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
		SimulationObject from = null;
		SimulationObject to = null;

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if (XML_ELEMENT_SOURCE.equals(n.getNodeName())) {
				parseHelperPoint(c.getBezierSource(), n);
				from = parseFlowConnectorEnd(n, model, XML_ELEMENT_ATTRIB_FROM);

			} else if (XML_ELEMENT_TARGET.equals(n.getNodeName())) {
				parseHelperPoint(c.getBezierTarget(), n);

				to = parseFlowConnectorEnd(n, model, XML_ELEMENT_ATTRIB_TO);

			} else if (XML_ELEMENT_VALVE.equals(n.getNodeName())) {
				c.getValve().setX(XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_X));
				c.getValve().setY(XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_Y));

			} else {
				System.err.println("Unexpected element within flow connector: «" + n.getNodeName() + "»");
			}
		}

		if (from == null || to == null) {
			throw new RuntimeException("Error parsing FlowConnector: Target and Source has to be valid!");
		}

		c.setSource(from);
		c.setTarget(to);
	}

	private SimulationObject parseFlowConnectorEnd(Node node, AbstractSimulationModel<?> model, String attribTarget) {
		String target = XmlHelper.getAttribute(node, attribTarget);

		if (target != null) {
			NamedSimulationObject object = model.getByName(target);
			if (object == null) {
				throw new RuntimeException("Within flow connector: " + attribTarget + "; Element «" + target + "» not found!");
			}
			
			return object;
		}

		InfiniteData infinite = null;

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if (XML_ELEMENT_INFINITE.equals(n.getNodeName())) {
				int x = XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_X);
				int y = XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_Y);
				infinite = new InfiniteData(x, y);

			} else {
				System.err.println("Unexpected element within flow source / target: «" + n.getNodeName() + "»");
			}
		}

		if (infinite == null) {
			throw new RuntimeException("Wihtin flow start / end no target and no infinite found!");
		}

		model.addData(infinite);

		return infinite;
	}

	private void parseHelperPoint(BezierConnectorData bezier, Node node) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if (XML_ELEMENT_HELPER_POINT.equals(n.getNodeName())) {
				bezier.setHelperPoint(new Point(XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_X), XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_Y)));
			} else {
				System.err.println("Unexpected element within " + XML_ELEMENT_HELPER_POINT + " «" + n.getNodeName() + "»");
			}
		}

	}

	private boolean parseConnectors(SimulationFlowModel model) {
		boolean error = false;

		for (Node node : flowConnectors) {
			if (XML_ELEMENT_FLOW_CONNECTOR.equals(node.getNodeName())) {
				try {
					FlowConnector c = new FlowConnector(null, null);
					parseConnector(node, c, model);

					String value = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_VALUE);
					String name = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_NAME);
					FlowValve valve = c.getValve();
					valve.setFormula(value);
					valve.setName(name);

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
					Point point = parseHelperPoint(node);
					c.setHelperPoint(point);

					model.addConnector(c);
				} catch (Exception e) {
					Errorhandler.logError(e, "connector ignored");
				}
			}
		}

		return !error;
	}

	private Point parseHelperPoint(Node node) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE && XML_ELEMENT_HELPER_POINT.equals(n.getNodeName())) {
				int x = XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_X);
				int y = XmlHelper.getAttributeInt(n, XML_ELEMENT_ATTRIB_Y);

				return new Point(x, y);
			}
		}

		return null;
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
