package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.zhaw.simulation.xyviewer.model.XYResult;
import ch.zhaw.simulation.xyviewer.model.XYResultStep;
import ch.zhaw.simulation.xyviewer.model.dummy.XYResultDummy;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JSlider slider;
	private XYViewer viewer = new XYViewer();

	public ResultViewerDialog(final XYResult result) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		viewer.init(result);

		slider = new JSlider(0, result.getCount());

		JComponent comp = new JComponent() {
			private static final long serialVersionUID = 1L;

			{
				setPreferredSize(result.getModelSize());
			}

			@Override
			protected void paintComponent(Graphics g) {
				XYResultStep step = result.getStep(slider.getValue());
				viewer.draw((Graphics2D) g, step);
			}

		};

		add(BorderLayout.CENTER, new JScrollPane(comp));
		add(BorderLayout.SOUTH, slider);

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});

		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		XYResult result = new XYResultDummy();
		new ResultViewerDialog(result).setVisible(true);
	}
}
