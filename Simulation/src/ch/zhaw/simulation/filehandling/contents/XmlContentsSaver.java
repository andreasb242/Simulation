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
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;

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

	private void visitXyModel(Element root, SimulationXYModel xyModel) {
		Element xmlModel = createModelElement(root, XML_MODEL_TYPE_XY);

		xmlModel.setAttribute(XML_MODEL_XY_GRID, "" + xyModel.getGrid());
		xmlModel.setAttribute(XML_MODEL_XY_SHOW_GRID, "" + xyModel.isShowGrid());
		xmlModel.setAttribute(XML_MODEL_XY_DENSITY_VIEW_TYPE, "" + xyModel.getDensityViewType());
		xmlModel.setAttribute(XML_MODEL_XY_ZERO_X, "" + xyModel.getZero().x);
		xmlModel.setAttribute(XML_MODEL_XY_ZERO_Y, "" + xyModel.getZero().y);
		xmlModel.setAttribute(XML_MODEL_XY_WIDTH, "" + xyModel.getWidth());
		xmlModel.setAttribute(XML_MODEL_XY_HEIGHT, "" + xyModel.getHeight());
		
		visitSimulationData(xmlModel, xyModel);

		for (DensityData d : xyModel.getDensity()) {
			visitDensity(xmlModel, d);
		}

		for (SubModel s : xyModel.getSubmodels()) {
			visitSubModel(xmlModel, s);
		}
	}

	private void visitSubModel(Element xmlModel, SubModel s) {
		Element elem = visitFlowModel(xmlModel, s.getModel());
		elem.setAttribute(XML_ELEMENT_ATTRIB_NAME, s.getName());
	}

	private void visitDensity(Element xmlModel, DensityData d) {
		Element density = document.createElement(XML_MODEL_XY_ELEMENT_DENSITY);
		density.setAttribute(XML_ELEMENT_ATTRIB_NAME, d.getName());
		density.setAttribute(XML_ELEMENT_ATTRIB_VALUE, d.getFormula());
		density.setAttribute(XML_ELEMENT_ATTRIB_TEXT, d.getDescription());
		density.setAttribute(XML_ELEMENT_ATTRIB_LOG, Boolean.toString(d.isDisplayLogarithmic()));

		xmlModel.appendChild(density);
	}

	private Element visitFlowModel(Element root, SimulationFlowModel model) {
		Element xmlModel = createModelElement(root, XML_MODEL_TYPE_FLOW);

		visitSimulationData(xmlModel, model);

		for (AbstractConnectorData<?> c : model.getConnectors()) {
			if (c instanceof FlowConnectorData) {
				visitFlowConnector(xmlModel, (FlowConnectorData) c);
			} else if (c instanceof ParameterConnectorData) {
				visitParameterConnector(xmlModel, (ParameterConnectorData) c);
			} else {
				throw new RuntimeException("type " + c.getClass().getName() + " not available in visitor!");
			}
		}

		return xmlModel;
	}

	private void visitSimulationData(Element xmlModel, AbstractSimulationModel<?> model) {
		for (AbstractSimulationData o : model.getData()) {
			if (o instanceof SimulationParameterData) {
				visitSimulationParameter(xmlModel, (SimulationParameterData) o);
			} else if (o instanceof SimulationDensityContainerData) {
				visitSimulationDensityContainer(xmlModel, (SimulationDensityContainerData) o);
			} else if (o instanceof SimulationContainerData) {
				visitSimulationContainer(xmlModel, (SimulationContainerData) o);
			} else if (o instanceof SimulationGlobalData) {
				visitSimulationGlobal(xmlModel, (SimulationGlobalData) o);
			} else if (o instanceof TextData) {
				visitTextdata(xmlModel, (TextData) o);
			} else if (o instanceof MesoData) {
				visitMesoData(xmlModel, (MesoData) o);
			} else if (o instanceof InfiniteData) {
			} else if (o instanceof FlowValveData) {
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

	private Element createConnectorElement(String element, AbstractConnectorData<? extends AbstractNamedSimulationData> c) {
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

	private void visitParameterConnector(Element root, ParameterConnectorData c) {
		Element connector = createConnectorElement(XML_ELEMENT_CONNECTOR, c);

		connector.appendChild(visitPoint(c.getHelperPoint()));

		root.appendChild(connector);
	}

	private void visitFlowConnector(Element root, FlowConnectorData c) {
		Element connector = document.createElement(XML_ELEMENT_FLOW_CONNECTOR);
		connector.setAttribute(XML_ELEMENT_ATTRIB_NAME, c.getValve().getName());
		connector.setAttribute(XML_ELEMENT_ATTRIB_VALUE, c.getValve().getFormula());

		AbstractSimulationData source = c.getSource();

		Element xmlSource = document.createElement(XML_ELEMENT_SOURCE);
		connector.appendChild(xmlSource);
		xmlSource.appendChild(visitPoint(c.getBezierSource().getHelperPoint()));

		if (source instanceof AbstractNamedSimulationData) {
			xmlSource.setAttribute(XML_ELEMENT_ATTRIB_FROM, ((AbstractNamedSimulationData) source).getName());
		} else if (source instanceof InfiniteData) {
			Element infinite = document.createElement(XML_ELEMENT_INFINITE);

			infinite.setAttribute(XML_ELEMENT_ATTRIB_X, "" + source.getX());
			infinite.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + source.getY());

			xmlSource.appendChild(infinite);
		} else {
			throw new RuntimeException("Type of flow connector endpoint is unknown: " + source.getClass().getName());
		}

		AbstractSimulationData target = c.getTarget();

		Element xmlTarget = document.createElement(XML_ELEMENT_TARGET);
		connector.appendChild(xmlTarget);
		xmlTarget.appendChild(visitPoint(c.getBezierTarget().getHelperPoint()));

		if (target instanceof AbstractNamedSimulationData) {
			xmlTarget.setAttribute(XML_ELEMENT_ATTRIB_TO, ((AbstractNamedSimulationData) target).getName());
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

	private void visitSimulationDensityContainer(Element root, SimulationDensityContainerData c) {
		DensityData density = c.getDensity();
		if (density != null) {
			c.setFormula(density.getName());
		} else {
			c.setFormula("");
		}

		Element xml = createXmlElement(c, XML_ELEMENT_DENSITY_CONTAINER);
		c.setFormula("");

		root.appendChild(xml);
	}

	private void visitSimulationContainer(Element root, SimulationContainerData c) {
		Element xml = createXmlElement(c, XML_ELEMENT_CONTAINER);

		root.appendChild(xml);
	}

	private void visitSimulationParameter(Element root, SimulationParameterData p) {
		Element xml = createXmlElement(p, XML_ELEMENT_PARAMETER);

		root.appendChild(xml);
	}

	private void visitSimulationGlobal(Element root, SimulationGlobalData p) {
		Element xml = createXmlElement(p, XML_ELEMENT_GLOBAL);

		root.appendChild(xml);
	}

	private void visitMesoData(Element root, MesoData m) {
		if (m.getSubmodel() != null) {
			m.setFormula(m.getSubmodel().getName());
		} else {
			m.setFormula("");
		}

		Element xml = createXmlElement(m, XML_ELEMENT_MESO);
		xml.setAttribute(XML_ELEMENT_ATTRIB_MESO_X, m.getDataX().getFormula());
		xml.setAttribute(XML_ELEMENT_ATTRIB_MESO_Y, m.getDataY().getFormula());
		xml.setAttribute(XML_ELEMENT_ATTRIB_MESO_DERIVATIVE, m.getDerivative().toString());

		m.setFormula("");

		root.appendChild(xml);
	}

	private Element createXmlElement(AbstractNamedSimulationData o, String type) {
		Element xml = document.createElement(type);

		xml.setAttribute(XML_ELEMENT_ATTRIB_X, "" + o.getX());
		xml.setAttribute(XML_ELEMENT_ATTRIB_Y, "" + o.getY());
		xml.setAttribute(XML_ELEMENT_ATTRIB_NAME, o.getName());

		xml.setAttribute(XML_ELEMENT_ATTRIB_VALUE, o.getFormula());
		return xml;
	}
}
