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
	private SimulationSerie serie;
	private int id;

	private BasicStroke stroke;

	protected SerieTreeNode(String name) {
		this.name = name;
	}

	public SerieTreeNode(SimulationSerie s, int id) {
		this(s.getName());
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

	/**
	 * @return true if toggled, false if it's not possible to toggle
	 */
	public boolean toggleSelection() {
		this.selected = !this.selected;
		return true;
	}

	public Color getColor() {
		if (serie == null) {
			return null;
		}
		return (Color) serie.getPaint();
	}

	public BasicStroke getStroke() {
		return stroke;
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
