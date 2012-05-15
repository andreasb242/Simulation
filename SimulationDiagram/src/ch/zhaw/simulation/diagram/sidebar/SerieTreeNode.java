package ch.zhaw.simulation.diagram.sidebar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;

import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SerieTreeNode implements TreeNode {
	private TreeNode parent;
	private String name;
	private SimulationSerie serie;
	private AbstractXYItemRenderer renderer;

	protected SerieTreeNode(String name) {
		this.name = name;
	}

	public SerieTreeNode(SimulationSerie s, AbstractXYItemRenderer renderer) {
		this(s.getName());
		this.serie = s;
		this.renderer = renderer;
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

	public boolean isSerieVisible() {
		if (serie == null) {
			return false;
		}

		return renderer.isSeriesVisible(serie.getChartId());
	}

	public void setSerieVisible(boolean visible) {
		renderer.setSeriesVisible(serie.getChartId(), visible);
	}

	/**
	 * @return true if toggled, false if it's not possible to toggle
	 */
	public boolean toggleSelection() {
		setSerieVisible(!isSerieVisible());
		return true;
	}

	public Color getColor() {
		if (serie == null) {
			return null;
		}

		return (Color) renderer.lookupSeriesPaint(serie.getChartId());
	}

	public BasicStroke getStroke() {
		if (serie == null) {
			return null;
		}
		return (BasicStroke) renderer.lookupSeriesStroke(serie.getChartId());
	}

	@Override
	public String toString() {
		return name;
	}

	public SimulationSerie getSerie() {
		return serie;
	}

	public int getChartId() {
		return serie.getChartId();
	}

}
