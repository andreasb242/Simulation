package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public ResultViewerDialog(Window parent, XYResultList resultList, Vector<XYDensityRaw> rawList) {
		super(parent);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		XYViewer viewer = new XYViewer(resultList, rawList);

		PositionControlPanel positionControl = new PositionControlPanel(resultList.getStepCount());

		XYViewerSidebar sidebar = new XYViewerSidebar(viewer, rawList);

		add(new JScrollPane(viewer), BorderLayout.CENTER);
		add(positionControl, BorderLayout.SOUTH);
		add(sidebar, BorderLayout.WEST);

		pack();
		setLocationRelativeTo(null);
	}
}
