package ch.zhaw.simulation.inexport.madonna.xmlformat;

import org.w3c.dom.Node;

public class XmlHelper {
	public static String getContents(Node node) {
		String s = node.getChildNodes().item(0).getTextContent();
		if (s != null) {
			return s.trim();
		}
		return null;
	}

}
