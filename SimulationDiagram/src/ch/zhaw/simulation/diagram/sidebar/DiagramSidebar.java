package ch.zhaw.simulation.diagram.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.jdesktop.swingx.VerticalLayout;

import ch.zhaw.simulation.diagram.DiagramConfigModel;
import ch.zhaw.simulation.diagram.DiagramPlot;
import ch.zhaw.simulation.diagram.colorcheckbox.ColorCheckbox;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramSidebar extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private DiagramConfigModel model;

	private JTree tree;

	public DiagramSidebar(DiagramConfigModel model) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		this.model = model;

		tree = new JTree();
		setViewportView(tree);
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
		// TODO Auto-generated method stub

	}
}
