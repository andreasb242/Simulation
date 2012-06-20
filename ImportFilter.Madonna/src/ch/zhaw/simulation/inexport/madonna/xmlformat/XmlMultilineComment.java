package ch.zhaw.simulation.inexport.madonna.xmlformat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlMultilineComment {
	private String text;
	private int id;
	private int x;
	private int y;
	private int width;
	private int height;

	public XmlMultilineComment(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseSubnode(n.getNodeName(), n);
			}
		}
	}

	private boolean parseSubnode(String nodeName, Node n) {
		if ("PageEntry".equals(nodeName)) {
			parsePageEntrys(n);
		} else if ("MovableNotes".equals(nodeName)) {
			parseMovableNotes(n);
		}

		return false;
	}

	private void parseMovableNotes(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("jeditorpane".equals(n.getNodeName())) {
					this.text = getTextContents(n);
				} else if ("Bounds".equals(n.getNodeName())) {
					String s = XmlHelper.getContents(n);
					String[] data = s.split(" ");
					this.x = Integer.parseInt(data[0]);
					this.y = Integer.parseInt(data[1]);
					this.width = Integer.parseInt(data[2]);
					this.height = Integer.parseInt(data[3]);
				}
			}
		}
	}

	private String getTextContents(Node node) {
		NodeList nodes = node.getChildNodes();

		StringBuilder txt = new StringBuilder();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.TEXT_NODE) {
				txt.append(n.getTextContent());
			}
		}

		return txt.toString().trim();
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

	public String getText() {
		return text;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
