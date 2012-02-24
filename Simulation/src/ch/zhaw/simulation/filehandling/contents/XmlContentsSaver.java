package ch.zhaw.simulation.filehandling.contents;

import java.awt.Point;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import ch.zhaw.simulation.filehandling.AbstractXmlSaver;
import ch.zhaw.simulation.model.flow.InfiniteData;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationContainer;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.SimulationGlobal;
import ch.zhaw.simulation.model.flow.SimulationObject;
import ch.zhaw.simulation.model.flow.SimulationParameter;
import ch.zhaw.simulation.model.flow.CommentData;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;

public class XmlContentsSaver extends AbstractXmlSaver implements XmlContentsNames {

	public void saveContents(OutputStream out, SimulationFlowModel model) throws ParserConfigurationException, TransformerException {
		Element root = initDocument(XML_ROOT);

		saveModel(root, model);

		saveOutDocument(out);
	}

	private void saveModel(Element root, SimulationFlowModel model) {
		for (SimulationObject o : model.getData()) {
			if (o instanceof SimulationParameter) {
				visitSimulationParameter(root, (SimulationParameter) o);
			} else if (o instanceof SimulationContainer) {
				visitSimulationcontainer(root, (SimulationContainer) o);
			} else if (o instanceof SimulationGlobal) {
				visitSimulationGlobal(root, (SimulationGlobal) o);
			} else if (o instanceof CommentData) {
				visitTextdata(root, (CommentData) o);
			} else if (o instanceof InfiniteData) {
			} else if (o instanceof FlowValve) {
			} else {
				throw new RuntimeException("type " + o.getClass().getName() + " not available in visitor!");
			}
		}

		for (Connector<?> c : model.getConnectors()) {
			if (c instanceof FlowConnector) {
				visitFlowConnector(root, (FlowConnector) c);
			} else if (c instanceof ParameterConnector) {
				visitParameterConnector(root, (ParameterConnector) c);
			} else {
				throw new RuntimeException("type " + c.getClass().getName() + " not available in visitor!");
			}
		}
	}

	private void visitTextdata(Element root, CommentData o) {
		Element text = document.createElement("text");
		text.setAttribute("x", "" + o.getX());
		text.setAttribute("y", "" + o.getY());

		text.setAttribute("width", "" + o.getWidth());
		text.setAttribute("height", "" + o.getHeight());

		text.setAttribute("text", o.getText());

		root.appendChild(text);
	}

	private Element createConnectorElement(String element, Connector<? extends NamedSimulationObject> c) {
		Element connector = document.createElement(element);

		connector.setAttribute("from", c.getSource().getName());
		connector.setAttribute("to", c.getTarget().getName());

		return connector;
	}

	private Element visitPoint(Point p) {
		Element point = document.createElement("point");
		point.setAttribute("x", "" + p.x);
		point.setAttribute("y", "" + p.y);

		return point;
	}

	private void visitParameterConnector(Element root, ParameterConnector c) {
		Element connector = createConnectorElement(XML_ELEMENT_CONNECTOR, c);

		connector.appendChild(visitPoint(c.getConnectorPoint()));

		root.appendChild(connector);
	}

	private void visitFlowConnector(Element root, FlowConnector c) {
		Element connector = document.createElement(XML_ELEMENT_FLOW_CONNECTOR);
		connector.setAttribute("name", c.getValve().getName());
		connector.setAttribute("value", c.getValve().getFormula());

		SimulationObject source = c.getSource();

		if (source instanceof NamedSimulationObject) {
			connector.setAttribute("from", ((NamedSimulationObject) source).getName());
		} else if (source instanceof InfiniteData) {
			Element infinite = document.createElement("infinite");

			infinite.setAttribute("connector", "from");
			infinite.setAttribute("x", "" + source.getX());
			infinite.setAttribute("y", "" + source.getY());

			connector.appendChild(infinite);
		} else {
			throw new RuntimeException("Type of flow connector endpoint is unknown: " + source.getClass().getName());
		}

		SimulationObject to = c.getTarget();

		if (to instanceof NamedSimulationObject) {
			connector.setAttribute("to", ((NamedSimulationObject) to).getName());
		} else if (to instanceof InfiniteData) {
			Element infinite = document.createElement("infinite");

			infinite.setAttribute("connector", "to");
			infinite.setAttribute("x", "" + to.getX());
			infinite.setAttribute("y", "" + to.getY());

			connector.appendChild(infinite);
		} else {
			throw new RuntimeException("Type of flow connector endpoint is unknown: " + to.getClass().getName());
		}

		connector.appendChild(visitPoint(c.getValve().getPoint()));

		root.appendChild(connector);
	}

	private void visitSimulationcontainer(Element root, SimulationContainer c) {
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

		xml.setAttribute("x", "" + o.getX());
		xml.setAttribute("y", "" + o.getY());
		xml.setAttribute("name", o.getName());

		xml.setAttribute("value", o.getFormula());
		return xml;
	}
}
