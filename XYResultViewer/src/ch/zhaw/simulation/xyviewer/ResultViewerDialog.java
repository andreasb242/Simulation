package ch.zhaw.simulation.xyviewer;

import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	protected JSlider slider;
	private XYViewer viewer;
	private XYViewerSidebar sidebar;

	public ResultViewerDialog(SimulationXYModel xyModel, XYResultList resultList, Vector<XYDensityRaw> rawList) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		// HACK: parse density-files
		viewer = new XYViewer(this, resultList, rawList);

		slider = new JSlider(0, resultList.getStepCount());
		slider.setValue(0);

		sidebar = new XYViewerSidebar(rawList);

		add(new JScrollPane(viewer), BorderLayout.CENTER);
		add(slider, BorderLayout.SOUTH);
		add(sidebar, BorderLayout.WEST);

		slider.addChangeListener(viewer);

		pack();
		setLocationRelativeTo(null);
	}
}
