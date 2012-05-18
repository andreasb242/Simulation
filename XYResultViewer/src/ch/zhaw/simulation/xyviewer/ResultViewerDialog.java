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
	private XYViewer viewer;

	public ResultViewerDialog(XYResultList resultList) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		viewer = new XYViewer(resultList);

		slider = new JSlider(0, resultList.getStepCount());
		slider.setValue(0);

		add(new JScrollPane(viewer), BorderLayout.CENTER);
		add(slider, BorderLayout.SOUTH);

		slider.addChangeListener(viewer);

		pack();
		setLocationRelativeTo(null);
	}
}
