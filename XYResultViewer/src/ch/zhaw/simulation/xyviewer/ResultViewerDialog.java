package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	protected JSlider slider;
	protected XYViewer viewer;
	protected XYViewerSidebar sidebar;

	public ResultViewerDialog(SimulationXYModel xyModel, XYResultList resultList, Vector<XYDensityRaw> rawList) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		// HACK: parse density-files
		viewer = new XYViewer(resultList, rawList);

		slider = new JSlider(0, resultList.getStepCount());
		slider.setValue(0);

		sidebar = new XYViewerSidebar(viewer, rawList);

		add(new JScrollPane(viewer), BorderLayout.CENTER);
		add(slider, BorderLayout.SOUTH);
		add(new JScrollPane(sidebar, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.WEST);

		slider.addChangeListener(viewer);

		pack();
		setLocationRelativeTo(null);
	}
}
