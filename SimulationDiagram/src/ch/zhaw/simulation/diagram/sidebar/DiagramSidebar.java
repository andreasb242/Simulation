package ch.zhaw.simulation.diagram.sidebar;

import java.awt.Color;
import java.awt.Paint;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
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

				if (visible) {
					DiagramSidebar.this.model.enableSerie(node.getSerie());
				} else {
					DiagramSidebar.this.model.disableSerie(node.getSerie());
				}

				DiagramSidebar.this.renderer.setSeriesVisible(node.getId(), visible);
			}
		};

		this.renderer.addChangeListener(renderListener);

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
		serieTreeNodes.clear();

		SimulationCollection collection = model.getCollection();

		for (int i = 0; i < collection.size(); i++) {
			SimulationSerie s = collection.getSerie(i);

			Paint paint = renderer.lookupSeriesPaint(i);
			s.setPaint(paint);

			SerieTreeNode n = new SerieTreeNode(s, i);
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
		for(int i = 0; i < serieTreeNodes.size();i++) {
			SerieTreeNode n = serieTreeNodes.get(i);
			n.setColor((Color) this.renderer.getSeriesPaint(i));
		}
	}

	public void dispose() {
		this.renderer.removeChangeListener(renderListener);
	}
}
