package ch.zhaw.simulation.diagram.sidebar;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
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

import ch.zhaw.simulation.diagram.DiagramConfigModel;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramSidebar extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private DiagramConfigModel model;

	private JTree tree;
	private DefaultTreeModel treeModel;
	private GroupTreeNode root = new GroupTreeNode("<root>");
	private AbstractXYItemRenderer renderer;

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

	public DiagramSidebar(DiagramConfigModel model, AbstractXYItemRenderer renderer) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		this.model = model;
		this.renderer = renderer;
		this.treeModel = new DefaultTreeModel(root) {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueForPathChanged(TreePath path, Object newValue) {
				SerieTreeNode node = (SerieTreeNode) path.getLastPathComponent();

				boolean visible = (Boolean) newValue;

				node.setSelected(visible);

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

					if (node.isSelected()) {
						DiagramSidebar.this.model.enableSerie(node.getSerie());
					} else {
						DiagramSidebar.this.model.disableSerie(node.getSerie());
					}

					DiagramSidebar.this.renderer.setSeriesVisible(node.getId(), node.isSelected());
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

		SimulationCollection collection = model.getCollection();

		for (int i = 0; i < collection.size(); i++) {
			SimulationSerie s = collection.getSerie(i);

			if (s.getPaint() != null) {
				renderer.setSeriesPaint(i, s.getPaint());
			} else {
				Paint paint = this.renderer.lookupSeriesPaint(i);
				s.setPaint(paint);
			}

			SerieTreeNode n = new SerieTreeNode(s, i);
			updateTreeNode(n, i);

			serieTreeNodes.add(n);

			boolean enabled = model.isEnabled(s);
			if (enabled) {
				n.setSelected(true);
			}
			DiagramSidebar.this.renderer.setSeriesVisible(i, enabled);
			root.add(n);
		}

		treeModel.nodeStructureChanged(root);
	}

	private void updateData() {
		for (int i = 0; i < serieTreeNodes.size(); i++) {
			SerieTreeNode n = serieTreeNodes.get(i);
			updateTreeNode(n, i);
		}

		if (tree != null) {
			tree.repaint();
		}
	}

	private void updateTreeNode(SerieTreeNode n, int i) {
		Paint paint = this.renderer.getSeriesPaint(i);
		n.getSerie().setPaint(paint);

		Stroke stroke = this.renderer.getSeriesStroke(i);

		if (stroke == null) {
			stroke = this.renderer.getBaseStroke();
		}

		if (stroke instanceof BasicStroke) {
			n.setStroke((BasicStroke) stroke);
		} else {
			System.err.println("DiagramSidebar.updateData: stroke.class = " + stroke.getClass());
		}
	}

	public void dispose() {
		this.renderer.removeChangeListener(renderListener);
	}
}
