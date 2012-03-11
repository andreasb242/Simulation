package ch.zhaw.simulation.help.model;

import java.io.FileInputStream;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import butti.javalibs.errorhandler.Errorhandler;

public class FunctionHelp {
	private TreeMap<String, Vector<FunctionInformation>> data = new TreeMap<String, Vector<FunctionInformation>>();

	public FunctionHelp() {
		try {
			parseXML();
		} catch (Exception e) {
			Errorhandler.showError(e, "Die Hilfe konnte nicht eingelesen werden!");
		}
	}

	private void parseXML() throws Exception {
		FileInputStream in = new FileInputStream("help/functions.xml");
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

		if (!"functions".equals(root.getNodeName())) {
			throw new Exception("Root node name != functions!");
		}

		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE && "class".equals(n.getNodeName())) {
				parseClass(n);
			}
		}
	}

	private void parseClass(Node node) {
		NodeList nodes = node.getChildNodes();
		String name = node.getAttributes().getNamedItem("name").getNodeValue();

		Vector<FunctionInformation> list = data.get(name);
		if (list == null) {
			list = new Vector<FunctionInformation>();
			data.put(name, list);
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if ("function".equals(n.getNodeName())) {
				parseNode(n, list);
			}
		}
	}

	private void parseNode(Node node, Vector<FunctionInformation> list) {
		NodeList nodes = node.getChildNodes();
		FunctionInformation info = new FunctionInformation();

		info.setName(node.getAttributes().getNamedItem("name").getNodeValue());
		info.setFunctionClass(node.getAttributes().getNamedItem("class").getNodeValue());

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if ("description".equals(n.getNodeName())) {
				info.setDescription(n.getChildNodes().item(0).getNodeValue());
			} else if ("param".equals(n.getNodeName())) {
				String def = null;
				Node defN = n.getAttributes().getNamedItem("default");
				if (defN != null) {
					def = defN.getNodeValue();
				}
				info.addParam(n.getChildNodes().item(0).getNodeValue(), def);
			} else if ("undefinedParam".equals(n.getNodeName())) {
				info.addUndefinedParam();
			}
		}
		list.add(info);
	}

	public TreeMap<String, Vector<FunctionInformation>> getData() {
		return data;
	}
}
