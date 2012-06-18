package ch.zhaw.simulation.gui.codeditor.library;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public class LibraryNode<T extends LibraryNode<?>> implements TreeNode, Iterable<T> {
	private Vector<T> children = new Vector<T>();
	private TreeNode parent;

	@Override
	public T getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return this.children.indexOf(node);
	}

	public void addChild(T node) {
		this.children.add(node);
		node.setParent(this);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return this.children.size() == 0;
	}

	@Override
	public Enumeration<T> children() {
		return children.elements();
	}

	@Override
	public Iterator<T> iterator() {
		return children.iterator();
	}

	protected void order() {
		Collections.sort(children, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}

		});
	}

}
