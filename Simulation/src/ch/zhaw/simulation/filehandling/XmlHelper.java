package ch.zhaw.simulation.filehandling;

import org.w3c.dom.Node;

public class XmlHelper {
	public static int getAttributeInt(Node node, String name) {
		return Integer.parseInt(getAttribute(node, name));
	}

	public static String getAttribute(Node node, String name) {
		if (node == null) {
			return null;
		}

		Node val = node.getAttributes().getNamedItem(name);

		if (val == null) {
			return null;
		}

		return val.getNodeValue();
	}

}
