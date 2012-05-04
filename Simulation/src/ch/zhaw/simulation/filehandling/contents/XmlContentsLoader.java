package ch.zhaw.simulation.filehandling.contents;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.zhaw.simulation.filehandling.XmlHelper;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;


public class XmlContentsLoader implements XmlContentsNames {

	public XmlContentsLoader() {
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
				return parseNode(n, doc);
			}
		}

		return false;
	}

	/**
	 * 
	 * @param n
	 *            The node
	 * @param doc
	 *            The document to overwrite
	 * @return true if everthing OK, false if some problems occured, but the
	 *         file was read anyway
	 * @throws Exception
	 *             If something went wrong, the file cannot be read
	 */
	private boolean parseNode(Node n, SimulationDocument doc) throws Exception {
		if (!XML_MODEL.equals(n.getNodeName())) {
			throw new RuntimeException("Unexpected Node: " + n.getNodeName());
		}

		String type = XmlHelper.getAttribute(n, XML_MODEL_TYPE);

		if (XML_MODEL_TYPE_FLOW.equals(type)) {
			doc.setType(SimulationType.FLOW_SIMULATION);

			XmlModelLoader loader = new XmlModelLoader();
			return loader.load(doc.getFlowModel(), null, n);

		} else if (XML_MODEL_TYPE_XY.equals(type)) {
			doc.setType(SimulationType.XY_MODEL);

			XmlModelLoader loader = new XmlModelLoader();
			return loader.load(doc.getXyModel(), n);
		} else {
			throw new RuntimeException("Unknown model type: «" + type + "»");
		}
	}
}
