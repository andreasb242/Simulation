package ch.zhaw.simulation.diagram.sidebar;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import ch.zhaw.simulation.diagram.DiagramConfigModel;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramSidebar extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private DiagramConfigModel model;

	private JTree tree;
	private DefaultTreeModel treeModel;
	private GroupTreeNode root = new GroupTreeNode("<root>");

	public DiagramSidebar(DiagramConfigModel model) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		this.model = model;
		this.treeModel = new DefaultTreeModel(root) {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueForPathChanged(TreePath path, Object newValue) {
				SerieTreeNode aNode = (SerieTreeNode) path.getLastPathComponent();

				aNode.setSelected((Boolean) newValue);
				nodeChanged(aNode);

				if ((Boolean) newValue) {
					DiagramSidebar.this.model.enableSerie(aNode.getSerie());
				} else {
					DiagramSidebar.this.model.disableSerie(aNode.getSerie());
				}
			}
		};

		loadData();

		tree = new JTree(this.treeModel);
		tree.setCellRenderer(new SerieTreeCellRenderer());
		tree.setCellEditor(new SerieTreeCellEditor());
		tree.setEditable(true);
		tree.setShowsRootHandles(false);

		setViewportView(tree);
		tree.setRootVisible(false);
	}

	private void loadData() {
		root.removeAllChildren();

		SimulationCollection collection = model.getCollection();

		Color[] colors = calcColors(collection.size());

		for (int i = 0; i < collection.size(); i++) {
			SimulationSerie s = collection.getSerie(i);
			s.setColor(colors[i]);
			SerieTreeNode n = new SerieTreeNode(s);

			if (model.isEnabled(s)) {
				n.setSelected(true);
			}

			root.add(n);
		}

		treeModel.nodeStructureChanged(root);
	}

	public Color[] calcColors(int count) {
		Color[] colors = new Color[count];

		float step = 360.0f / count;
		float angle = 0;

		for (int i = 0; i < count; i++) {
			colors[i] = Color.getHSBColor(angle / 360.0f, 1.0f, 1.0f);
			angle += step;
		}

		return colors;
	}

	public void dispose() {
	}
}
