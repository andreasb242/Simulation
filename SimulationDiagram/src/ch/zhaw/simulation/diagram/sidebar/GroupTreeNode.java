package ch.zhaw.simulation.diagram.sidebar;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public class GroupTreeNode extends SerieTreeNode {
	private Vector<SerieTreeNode> children = new Vector<SerieTreeNode>();

	public GroupTreeNode(String name) {
		super(name, null);
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	@Override
	public Enumeration<?> children() {
		return this.children.elements();
	}

	public void removeAllChildren() {
		this.children.clear();
	}

	public void add(SerieTreeNode n) {
		this.children.add(n);
	}
}
