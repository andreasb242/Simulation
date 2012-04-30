package ch.zhaw.simulation.filehandling.contents;

import java.awt.Point;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.filehandling.XmlHelper;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.BezierConnectorData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.MesoData.Derivative;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.model.xy.SubModelList;

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
			SimulationContainerData o = new SimulationContainerData(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if (XML_ELEMENT_PARAMETER.equals(name)) {
			SimulationParameterData o = new SimulationParameterData(0, 0);
			parseSimulationObject(node, o);

			model.addData(o);
		} else if (XML_ELEMENT_GLOBAL.equals(name)) {
			SimulationGlobalData o = new SimulationGlobalData(0, 0);
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
			SimulationDensityContainerData o = new SimulationDensityContainerData(0, 0);
			parseSimulationObject(node, o);
			model.addData(o);
		} else if (XML_ELEMENT_MESO.equals(name)) {
			MesoData o = new MesoData(0, 0);
			parseSimulationObject(node, o);

			String formulax = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_MESO_X);
			if (formulax == null) {
				formulax = "";
			}
			String formulay = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_MESO_Y);
			if (formulay == null) {
				formulay = "";
			}

			o.getDataX().setFormula(formulax);
			o.getDataY().setFormula(formulay);

			Derivative derivative = Derivative.valueOf(XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_MESO_DERIVATIVE));
			if (derivative == null) {
				derivative = Derivative.FIRST_DERIVATIVE;
			}
			o.setDerivative(derivative);

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

	private void parseConnector(Node node, ParameterConnectorData c, SimulationFlowModel model) {
		String sFrom = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_FROM);
		String sTo = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_TO);

		AbstractNamedSimulationData from = model.getByName(sFrom);
		AbstractNamedSimulationData to = model.getByName(sTo);

		if (from == null) {
			throw new RuntimeException("from not available! \"" + sFrom + "\"");
		}
		if (to == null) {
			throw new RuntimeException("to not available! \"" + sTo + "\"");
		}

		c.setSource(from);
		c.setTarget(to);
	}

	private void parseConnector(Node node, FlowConnectorData c, SimulationFlowModel model) {
		AbstractSimulationData from = null;
		AbstractSimulationData to = null;

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

	private AbstractSimulationData parseFlowConnectorEnd(Node node, AbstractSimulationModel<?> model, String attribTarget) {
		String target = XmlHelper.getAttribute(node, attribTarget);

		if (target != null) {
			AbstractNamedSimulationData object = model.getByName(target);
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
			} else if (XML_ELEMENT_HELPER_POINT.equals(n.getNodeName())) {
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
			} else if (XML_ELEMENT_INFINITE.equals(n.getNodeName())) {
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
					FlowConnectorData c = new FlowConnectorData(null, null);
					parseConnector(node, c, model);

					String value = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_VALUE);
					String name = XmlHelper.getAttribute(node, XML_ELEMENT_ATTRIB_NAME);
					FlowValveData valve = c.getValve();
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
					ParameterConnectorData c = new ParameterConnectorData(null, null);
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

	private void parseSimulationObject(Node node, AbstractNamedSimulationData o) {
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
	public boolean load(SimulationFlowModel flowModel, Node root) {
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
	public boolean load(SimulationXYModel xyModel, Node root) {
		if (root == null) {
			throw new NullPointerException("root == null");
		}

		xyModel.setGrid(XmlHelper.getAttributeInt(root, XML_MODEL_XY_GRID));

		Point zero = new Point(XmlHelper.getAttributeInt(root, XML_MODEL_XY_ZERO_X), XmlHelper.getAttributeInt(root, XML_MODEL_XY_ZERO_Y));

		xyModel.setZero(zero);
		xyModel.setWidth(XmlHelper.getAttributeInt(root, XML_MODEL_XY_WIDTH));
		xyModel.setHeight(XmlHelper.getAttributeInt(root, XML_MODEL_XY_HEIGHT));

		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if (XML_MODEL_XY_ELEMENT_DENSITY.equals(n.getNodeName())) {
					parseDensity(n, xyModel);
				} else if (XML_MODEL.equals(n.getNodeName())) {
					parseSubmodel(n, xyModel);
				} else {
					parseNode(n, xyModel);
				}
			}
		}

		// assign submodel to meso elements
		SubModelList submodel = xyModel.getSubmodels();
		for (MesoData m : xyModel.getMeso()) {
			if ("".equals(m.getFormula())) {
				continue;
			}

			SubModel sub = submodel.getByName(m.getFormula());

			if (sub == null) {
				System.err.println("Submodel «" + m.getFormula() + "» for «" + m.getName() + "» could not be assigned");
			} else {
				m.setSubmodel(sub);
			}
		}

		return true;
	}

	private void parseSubmodel(Node root, SimulationXYModel xyModel) {
		String name = XmlHelper.getAttribute(root, XML_ELEMENT_ATTRIB_NAME);
		String type = XmlHelper.getAttribute(root, XML_MODEL_TYPE);

		if (!XML_MODEL_TYPE_FLOW.equals(type)) {
			System.err.println("unexcpected submodel: «" + type + "»");
			return;
		}

		SubModel submodel = new SubModel();
		if (!load(submodel.getModel(), root)) {
			throw new RuntimeException("Coult not load submodel: «" + name + "»");
		}
		submodel.setName(name);
		xyModel.getSubmodels().addModel(submodel);
	}

	private void parseDensity(Node n, SimulationXYModel xyModel) {
		DensityData data = new DensityData();
		data.setName(XmlHelper.getAttribute(n, XML_ELEMENT_ATTRIB_NAME));
		data.setFormula(XmlHelper.getAttribute(n, XML_ELEMENT_ATTRIB_VALUE));
		data.setDescription(XmlHelper.getAttribute(n, XML_ELEMENT_ATTRIB_TEXT));
	}

}
