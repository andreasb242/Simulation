package ch.zhaw.simulation.inexport.madonna.xmlformat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlPipe {
	private int from;
	private int to;
	private int id;

	public XmlPipe(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseValue(n.getNodeName(), n);
			}
		}
	}

	private void parseValue(String nodeName, Node n) {
		if ("PipeIds".equals(nodeName)) {
			String v = XmlHelper.getContents(n);
			String[] vals = v.split(" ");
			from = Integer.parseInt(vals[0]);
			to = Integer.parseInt(vals[1]);
		} else if ("PageEntry".equals(nodeName)) {
			parsePageEntrys(n);
		}
	}

	private void parsePageEntrys(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parsePageEntry(n.getNodeName(), n);
			}
		}
	}

	private void parsePageEntry(String nodeName, Node n) {
		if ("Id".equals(nodeName)) {
			this.id = Integer.parseInt(XmlHelper.getContents(n));
		}
	}

	public int getId() {
		return id;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}
