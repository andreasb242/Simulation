package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public ResultViewerDialog(XYResultList resultList, Vector<XYDensityRaw> rawList) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		XYViewer viewer = new XYViewer(resultList, rawList);

		JSlider slider = new JSlider(0, resultList.getStepCount());
		slider.setValue(0);

		XYViewerSidebar sidebar = new XYViewerSidebar(rawList);

		add(new JScrollPane(viewer), BorderLayout.CENTER);
		add(slider, BorderLayout.SOUTH);
		add(sidebar, BorderLayout.WEST);

		slider.addChangeListener(viewer);

		pack();
		setLocationRelativeTo(null);
	}
}
