package ch.zhaw.simulation.diagram;

import ch.zhaw.simulation.plugin.data.SimulationCollection;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramSidebar sidebar;
	private DiagramPlot plot;

	public DiagramFrame(SimulationCollection collection) {

		plot = new DiagramPlot();
		sidebar = new DiagramSidebar(collection, plot);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Diagram");
		setLayout(new BorderLayout());

		add(BorderLayout.WEST, sidebar);
		add(BorderLayout.CENTER, new JScrollPane(plot));

		setSize(640, 480);
		setLocationRelativeTo(null);// center
	}

	public void updateSimulationCollection(SimulationCollection collection) {
		plot.updateSimulationCollection(collection);
	}

}
