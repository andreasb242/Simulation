package simulation.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class SimulationProgress extends JDialog {
	private static final long serialVersionUID = 1L;
	private JProgressBar progress = new JProgressBar();
	private JButton btCancel = new JButton("Abbrechen");

	public SimulationProgress(JFrame parent) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Status");

		setLayout(new BorderLayout());

		add(progress, BorderLayout.CENTER);

		add(btCancel, BorderLayout.EAST);

		pack();
		setLocationRelativeTo(parent);
	}

	public JProgressBar getProgress() {
		return progress;
	}
	
	public JButton getCancelButton() {
		return btCancel;
	}
}
