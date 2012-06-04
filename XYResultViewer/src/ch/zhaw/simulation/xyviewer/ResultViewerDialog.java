package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import butti.javalibs.config.Settings;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private PositionModel model;

	private PositionControlPanel positionControl;

	private XYViewer viewer;

	public ResultViewerDialog(Window parent, Settings settings, XYResultList resultList, Vector<XYDensityRaw> rawList) {
		super(parent);

		this.model = new PositionModel(resultList.getStepCount());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		this.viewer = new XYViewer(resultList, rawList, this.model);

		this.positionControl = new PositionControlPanel(this.model, settings);

		XYViewerSidebar sidebar = new XYViewerSidebar(viewer, rawList);

		add(new JScrollPane(viewer), BorderLayout.CENTER);
		add(positionControl, BorderLayout.SOUTH);
		add(sidebar, BorderLayout.WEST);

		model.firePosition(0);

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void dispose() {
		this.positionControl.dispose();
		this.viewer.dispose();

		super.dispose();
	}
}
