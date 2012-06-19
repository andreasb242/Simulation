package ch.zhaw.simulation.inexport.madonna.xmlformat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlArc {
	private int from;
	private int to;

	public XmlArc(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseValue(n.getNodeName(), n);
			}
		}
	}

	private void parseValue(String nodeName, Node n) {
		if ("ArcIds".equals(nodeName)) {
			String v = XmlHelper.getContents(n);
			String[] vals = v.split(" ");
			from = Integer.parseInt(vals[0]);
			to = Integer.parseInt(vals[1]);
		}
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}
