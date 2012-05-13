package ch.zhaw.simulation.diagram.sidebar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SerieTreeNode implements TreeNode {
	private TreeNode parent;
	private String name;
	private boolean selected;
	private Color color;
	private BasicStroke stroke;
	private SimulationSerie serie;
	private int id;

	protected SerieTreeNode(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public SerieTreeNode(SimulationSerie s, int id) {
		this(s.getName(), (Color) s.getPaint());
		this.serie = s;
		this.id = id;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Enumeration<?> children() {
		return null;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Color getColor() {
		return color;
	}

	public BasicStroke getStroke() {
		return stroke;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setStroke(BasicStroke stroke) {
		this.stroke = stroke;
	}

	@Override
	public String toString() {
		return name;
	}

	public SimulationSerie getSerie() {
		return serie;
	}

	public int getId() {
		return id;
	}
}
