package ch.zhaw.simulation.diagram.sidebar;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramSidebar extends JScrollPane {
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private DefaultTreeModel treeModel;
	private GroupTreeNode root = new GroupTreeNode("<root>");
	private AbstractXYItemRenderer renderer;

	private SimulationCollection collection;

	/**
	 * The nodes, with the same Index as the series in the diagram
	 */
	private Vector<SerieTreeNode> serieTreeNodes = new Vector<SerieTreeNode>();

	private RendererChangeListener renderListener = new RendererChangeListener() {

		@Override
		public void rendererChanged(RendererChangeEvent event) {
			updateData();
		}
	};

	public DiagramSidebar(SimulationCollection collection, AbstractXYItemRenderer renderer) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		this.collection = collection;
		this.renderer = renderer;
		this.treeModel = new DefaultTreeModel(root) {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueForPathChanged(TreePath path, Object newValue) {
				SerieTreeNode node = (SerieTreeNode) path.getLastPathComponent();

				boolean visible = (Boolean) newValue;

				node.setSerieVisible(visible);

				nodeChanged(node);
			}
		};

		this.treeModel.addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeStructureChanged(TreeModelEvent e) {
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
			}

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				for (Object o : e.getChildren()) {
					SerieTreeNode node = (SerieTreeNode) o;
					DiagramSidebar.this.renderer.setSeriesVisible(node.getChartId(), node.isSerieVisible());
				}
			}
		});

		loadData();

		tree = new JTree(this.treeModel);

		tree.setCellRenderer(new SerieTreeCellRenderer());
		tree.setCellEditor(new SerieTreeCellEditor());
		tree.setEditable(true);
		tree.setShowsRootHandles(false);

		setViewportView(tree);
		tree.setRootVisible(false);

		tree.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == ' ' || e.getKeyChar() == '\n') {
					if (tree.getSelectionPath() == null) {
						return;
					}

					SerieTreeNode element = (SerieTreeNode) tree.getSelectionPath().getLastPathComponent();
					if (element.toggleSelection()) {
						treeModel.nodeChanged(element);
						tree.repaint();
						e.consume();
					}
				}
			}
		});

		this.renderer.addChangeListener(renderListener);
	}

	private void loadData() {
		root.removeAllChildren();
		serieTreeNodes.clear();

		for (int i = 0; i < collection.size(); i++) {
			SimulationSerie s = collection.getSerie(i);

			SerieTreeNode n = new SerieTreeNode(s, renderer);

			serieTreeNodes.add(n);
			root.add(n);
		}

		treeModel.nodeStructureChanged(root);
	}

	private void updateData() {
		if (tree != null) {
			tree.repaint();
		}
	}

	public void dispose() {
		this.renderer.removeChangeListener(renderListener);
	}
}
