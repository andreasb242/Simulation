package ch.zhaw.simulation.diagram;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramSidebar diagram = new DiagramSidebar();
	private DiagramView view = new DiagramView();

	public DiagramFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Diagram");
		setLayout(new BorderLayout());

		add(BorderLayout.WEST, diagram);
		add(BorderLayout.CENTER, new JScrollPane(view));

		setSize(640, 480);
		setLocationRelativeTo(null);// center
	}

}
