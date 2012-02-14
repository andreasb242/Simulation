package ch.zhaw.simulation.filehandling;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.zhaw.simulation.model.InfiniteData;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.TextData;
import ch.zhaw.simulation.model.connection.Connector;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;
import ch.zhaw.simulation.model.connection.ParameterConnector;

import butti.javalibs.errorhandler.Errorhandler;

public class XmlSaver {
	private Document document;
	public static final int VERSION = 1;
	public static final int VERSION_COMPATIBLE = 1;

	public XmlSaver() {
	}

	private Properties getAbout(SimulationDocument model) {
		Properties p = new Properties();

		for (String key : model.getMetainfKeys()) {
			p.put(key, model.getMetainf(key));
		}

		return p;
	}

	public boolean save(File file, SimulationDocument model) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fout = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream(fout);

		out.setComment("Simulation file");
		out.putNextEntry(new ZipEntry("mimetype"));
		out.write("application/butti.simulation.project".getBytes());
		out.putNextEntry(new ZipEntry("version"));
		out.write(("version=" + VERSION + "\ncompatible=" + VERSION_COMPATIBLE).getBytes());
		out.putNextEntry(new ZipEntry("metainf"));
		getAbout(model).store(out, "Simulation metainformations");

		out.putNextEntry(new ZipEntry("simulation.xml"));

		if (!generateXml(out, model)) {
			out.close();
			fout.close();
			file.delete();
			System.err.println("generateXml error");
			return false;
		}

		out.close();
		fout.close();

		return true;
	}

	private boolean generateXml(ZipOutputStream out, SimulationDocument model) {
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			document.setXmlStandalone(true);

			Element root = document.createElement("simulation");
			document.appendChild(root);

			saveModel(root, model);

			DOMSource ds = new DOMSource(document);
			TransformerFactory tf = TransformerFactory.newInstance();

			Transformer trans = tf.newTransformer();

			trans.setOutputProperty("indent", "yes"); // Java XML Indent
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			trans.transform(ds, new StreamResult(out));

		} catch (ParserConfigurationException e) {
			Errorhandler.showError(e);
			return false;
		} catch (TransformerConfigurationException e) {
			Errorhandler.showError(e);
			return false;
		} catch (TransformerException e) {
			Errorhandler.showError(e);
			return false;
		} catch (RuntimeException e) {
			Errorhandler.showError(e);
			return false;
		} catch (Exception e) {
			Errorhandler.showError(e);
			return false;
		}

		return true;
	}

	private void saveModel(Element root, SimulationDocument model) {
		for (SimulationObject o : model.getData()) {
			if (o instanceof SimulationParameter) {
				visitSimulationParameter(root, (SimulationParameter) o);
			} else if (o instanceof SimulationContainer) {
				visitSimulationcontainer(root, (SimulationContainer) o);
			} else if (o instanceof SimulationGlobal) {
				visitSimulationGlobal(root, (SimulationGlobal) o);
			} else if (o instanceof TextData) {
				visitTextdata(root, (TextData) o);
			} else if (o instanceof InfiniteData) {
			} else if (o instanceof FlowParameterPoint) {
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

	private void visitTextdata(Element root, TextData o) {
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
		Element connector = createConnectorElement("connector", c);

		connector.appendChild(visitPoint(c.getConnectorPoint()));

		root.appendChild(connector);
	}

	private void visitFlowConnector(Element root, FlowConnector c) {
		Element connector = document.createElement("flowConnector");
		connector.setAttribute("name", c.getParameterPoint().getName());
		connector.setAttribute("value", c.getParameterPoint().getFormula());

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

		connector.appendChild(visitPoint(c.getParameterPoint().getPoint()));

		root.appendChild(connector);
	}

	private void visitSimulationcontainer(Element root, SimulationContainer c) {
		Element xml = createXmlElement(c, "container");

		root.appendChild(xml);
	}

	private void visitSimulationParameter(Element root, SimulationParameter p) {
		Element xml = createXmlElement(p, "parameter");

		root.appendChild(xml);
	}

	private void visitSimulationGlobal(Element root, SimulationGlobal p) {
		Element xml = createXmlElement(p, "global");

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
