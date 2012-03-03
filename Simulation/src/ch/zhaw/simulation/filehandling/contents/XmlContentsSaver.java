package ch.zhaw.simulation.filehandling.contents;

import java.awt.Point;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.zhaw.simulation.filehandling.AbstractXmlSaver;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.xy.Density;
import ch.zhaw.simulation.model.xy.XYModel;

/**
 * Saves an SimulationDocument to an XML file
 * 
 * @author Andreas Butti
 */
public class XmlContentsSaver extends AbstractXmlSaver implements XmlContentsNames {

	public void saveContents(OutputStream out, SimulationDocument doc) throws ParserConfigurationException, TransformerException {
		Element root = initDocument(XML_ROOT);

		if (doc.getType() == SimulationType.FLOW_SIMULATION) {
			visitFlowModel(root, doc.getFlowModel());
		} else if (doc.getType() == SimulationType.XY_MODEL) {
			visitXyModel(root, doc.getXyModel());
		} else {
			throw new RuntimeException("Unknown Model type: " + doc.getType());
		}

		saveOutDocument(out);
	}

	private void visitXyModel(Element root, XYModel xyModel) {
		Element xmlModel = createModelElement(root, XML_MODEL_TYPE_XY);

		xmlModel.setAttribute(XML_MODEL_XY_GRID, "" + xyModel.getGrid());
		xmlModel.setAttribute(XML_MODEL_XY_WIDTH, "" + xyModel.getWidth());
		xmlModel.setAttribute(XML_MODEL_XY_HEIGHT, "" + xyModel.getHeight());

		visitSimulationData(xmlModel, xyModel);

		for (Density d : xyModel.getDensity()) {
			visitDensity(xmlModel, d);
		}
	}

	private void visitDensity(Element xmlModel, Density d) {
		Element density = document.createElement(XML_MODEL_XY_ELEMENT_DENSITY);
		density.setAttribute(XML_ELEMENT_ATTRIB_NAME, d.getName());
		density.setAttribute(XML_ELEMENT_ATTRIB_VALUE, d.getFormula());
		density.setAttribute(XML_ELEMENT_ATTRIB_TEXT, d.getDescription());

		xmlModel.appendChild(density);
	}

	private void visitFlowModel(Element root, SimulationFlowModel model) {
		Element xmlModel = createModelElement(root, XML_MODEL_TYPE_FLOW);

		visitSimulationData(xmlModel, model);

		for (Connector<?> c : model.getConnectors()) {
			if (c instanceof FlowConnector) {
				visitFlowConnector(xmlModel, (FlowConnector) c);
			} else if (c instanceof ParameterConnector) {
				visitParameterConnector(xmlModel, (ParameterConnector) c);
			} else {
				throw new RuntimeException("type " + c.getClass().getName() + " not available in visitor!");
			}
		}
	}

	private void visitSimulationData(Element xmlModel, AbstractSimulationModel<?> model) {
		for (SimulationObject o : model.getData()) {
			if (o instanceof SimulationParameter) {
				visitSimulationParameter(xmlModel, (SimulationParameter) o);
			} else if (o instanceof SimulationDensityContainer) {
				visitSimulationDensityContainer(xmlModel, (SimulationDensityContainer) o);
			} else if (o instanceof SimulationContainer) {
				visitSimulationContainer(xmlModel, (SimulationContainer) o);
			} else if (o instanceof SimulationGlobal) {
				visitSimulationGlobal(xmlModel, (SimulationGlobal) o);
			} else if (o instanceof TextData) {
				visitTextdata(xmlModel, (TextData) o);
			} else if (o instanceof InfiniteData) {
			} else if (o instanceof FlowValve) {
			} else {
				throw new RuntimeException("type " + o.getClass().getName() + " not available in visitor!");
			}
		}
	}

	/**
	 * Creates a simulation model tag, for flow or xy model
	 */
	private Element createModelElement(Element root, String type) {
		Element elem = document.createElement(XML_MODEL);
		elem.setAttribute(XML_MODEL_TYPE, type);
		root.appendChild(elem);

		return elem;
	}

	/**
	 * Visit a text element ("comment")
	 */
	private void visitTextdata(Element root, TextData o) {
		Element text = document.createElement(XML_ELEMENT_TEXT);
		text.setAttribute(XML_ELEMENT_ATTRIB_X, "" + o.getX());
		text.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + o.getY());

		text.setAttribute(XML_ELEMENT_ATTRIB_WIDTH, "" + o.getWidth());
		text.setAttribute(XML_ELEMENT_ATTRIB_HEIGHT, "" + o.getHeight());

		text.setAttribute(XML_ELEMENT_TEXT, o.getText());

		root.appendChild(text);
	}

	private Element createConnectorElement(String element, Connector<? extends NamedSimulationObject> c) {
		Element connector = document.createElement(element);

		connector.setAttribute(XML_ELEMENT_ATTRIB_FROM, c.getSource().getName());
		connector.setAttribute(XML_ELEMENT_ATTRIB_TO, c.getTarget().getName());

		return connector;
	}

	private Element visitPoint(Point p) {
		Element point = document.createElement(XML_ELEMENT_HELPER_POINT);
		point.setAttribute(XML_ELEMENT_ATTRIB_X, "" + p.x);
		point.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + p.y);

		return point;
	}

	private void visitParameterConnector(Element root, ParameterConnector c) {
		Element connector = createConnectorElement(XML_ELEMENT_CONNECTOR, c);

		connector.appendChild(visitPoint(c.getHelperPoint()));

		root.appendChild(connector);
	}

	private void visitFlowConnector(Element root, FlowConnector c) {
		Element connector = document.createElement(XML_ELEMENT_FLOW_CONNECTOR);
		connector.setAttribute(XML_ELEMENT_ATTRIB_NAME, c.getValve().getName());
		connector.setAttribute(XML_ELEMENT_ATTRIB_VALUE, c.getValve().getFormula());

		SimulationObject source = c.getSource();

		Element xmlSource = document.createElement(XML_ELEMENT_SOURCE);
		connector.appendChild(xmlSource);
		xmlSource.appendChild(visitPoint(c.getBezierSource().getHelperPoint()));

		if (source instanceof NamedSimulationObject) {
			xmlSource.setAttribute(XML_ELEMENT_ATTRIB_FROM, ((NamedSimulationObject) source).getName());
		} else if (source instanceof InfiniteData) {
			Element infinite = document.createElement(XML_ELEMENT_INFINITE);

			infinite.setAttribute(XML_ELEMENT_ATTRIB_X, "" + source.getX());
			infinite.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + source.getY());

			xmlSource.appendChild(infinite);
		} else {
			throw new RuntimeException("Type of flow connector endpoint is unknown: " + source.getClass().getName());
		}

		SimulationObject target = c.getTarget();

		Element xmlTarget = document.createElement(XML_ELEMENT_TARGET);
		connector.appendChild(xmlTarget);
		xmlTarget.appendChild(visitPoint(c.getBezierTarget().getHelperPoint()));

		if (target instanceof NamedSimulationObject) {
			xmlTarget.setAttribute(XML_ELEMENT_ATTRIB_TO, ((NamedSimulationObject) target).getName());
		} else if (target instanceof InfiniteData) {
			Element infinite = document.createElement(XML_ELEMENT_INFINITE);

			infinite.setAttribute(XML_ELEMENT_ATTRIB_X, "" + target.getX());
			infinite.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + target.getY());

			xmlTarget.appendChild(infinite);
		} else {
			throw new RuntimeException("Type of flow connector endpoint is unknown: " + target.getClass().getName());
		}

		connector.appendChild(visitValvePoint(c.getValve().getPoint()));

		root.appendChild(connector);
	}

	private Node visitValvePoint(Point p) {
		Element valve = document.createElement(XML_ELEMENT_VALVE);
		valve.setAttribute(XML_ELEMENT_ATTRIB_X, "" + p.x);
		valve.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + p.y);

		return valve;
	}

	private void visitSimulationDensityContainer(Element root, SimulationDensityContainer c) {
		Element xml = createXmlElement(c, XML_ELEMENT_DENSITY_CONTAINER);

		root.appendChild(xml);
	}

	private void visitSimulationContainer(Element root, SimulationContainer c) {
		Element xml = createXmlElement(c, XML_ELEMENT_CONTAINER);

		root.appendChild(xml);
	}

	private void visitSimulationParameter(Element root, SimulationParameter p) {
		Element xml = createXmlElement(p, XML_ELEMENT_PARAMETER);

		root.appendChild(xml);
	}

	private void visitSimulationGlobal(Element root, SimulationGlobal p) {
		Element xml = createXmlElement(p, XML_ELEMENT_GLOBAL);

		root.appendChild(xml);
	}

	private Element createXmlElement(NamedSimulationObject o, String type) {
		Element xml = document.createElement(type);

		xml.setAttribute(XML_ELEMENT_ATTRIB_X, "" + o.getX());
		xml.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + o.getY());
		xml.setAttribute(XML_ELEMENT_ATTRIB_NAME, o.getName());

		xml.setAttribute(XML_ELEMENT_ATTRIB_VALUE, o.getFormula());
		return xml;
	}
}
