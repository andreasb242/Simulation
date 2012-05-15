package ch.zhaw.simulation.xyviewer;

import ch.zhaw.simulation.xyviewer.model.XYResultList;
import ch.zhaw.simulation.xyviewer.model.XYResultStepList;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JSlider slider;
	private XYViewer viewer = new XYViewer();
	private XYResultList resultList;

	public ResultViewerDialog(XYResultList result) {
		resultList = result;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		viewer.init(result);

		slider = new JSlider(0, result.getStepCount());
		slider.setValue(0);

		JComponent comp = new JComponent() {
			private static final long serialVersionUID = 1L;

			{
				setPreferredSize(resultList.getModelSize());
			}

			@Override
			protected void paintComponent(Graphics g) {
				XYResultStepList stepList = resultList.getStep(slider.getValue());
				viewer.draw((Graphics2D) g, stepList);
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
}
