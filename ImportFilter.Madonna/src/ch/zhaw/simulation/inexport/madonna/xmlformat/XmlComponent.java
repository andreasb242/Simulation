package ch.zhaw.simulation.inexport.madonna.xmlformat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.zhaw.simulation.inexport.madonna.FormulaImporter;

public class XmlComponent {
	private int x = -1;
	private int y = -1;

	private String formula;
	private int id;
	private int nameId;

	public XmlComponent(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				parseSubnode(n.getNodeName(), n);
			}
		}
	}

	private boolean parseSubnode(String nodeName, Node n) {
		if ("Pos".equals(nodeName)) {
			String p = XmlHelper.getContents(n);
			String[] vals = p.split(" ");
			x = Integer.parseInt(vals[0]);
			y = Integer.parseInt(vals[1]);
			return true;
		} else if ("Formula".equals(nodeName)) {
			this.formula = FormulaImporter.convert(XmlHelper.getContents(n));
		} else if ("PageEntry".equals(nodeName)) {
			parsePageEntrys(n);
		}

		return false;
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
		} else if ("NameEntry".equals(nodeName)) {
			this.nameId = Integer.parseInt(XmlHelper.getContents(n));
		}
	}

	public int getId() {
		return id;
	}

	public int getNameId() {
		return nameId;
	}

	public String getFormula() {
		return formula;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
