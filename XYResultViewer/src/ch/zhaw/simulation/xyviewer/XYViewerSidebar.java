package ch.zhaw.simulation.xyviewer;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class XYViewerSidebar extends JScrollPane {

	private ResultViewerDialog dialog;
	private JLabel lblTitle;
	private ButtonGroup radioGroup;
	private JPanel radioPanel;

	public XYViewerSidebar(ResultViewerDialog dialog, Vector<XYDensityRaw> rawList) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		JRadioButton radioButton;

		this.dialog = dialog;

		viewport.setLayout(new FlowLayout());

		lblTitle = new JLabel("End-Dichte:");

		radioGroup = new ButtonGroup();
		radioPanel = new JPanel(new GridLayout(0, 1));

		for (XYDensityRaw raw : rawList) {
			System.out.println(raw.getDensityName());
			radioButton = new JRadioButton(raw.getDensityName());
			radioButton.addActionListener(dialog.viewer);
			radioGroup.add(radioButton);
			radioPanel.add(radioButton);
		}

		viewport.add(lblTitle);
		viewport.add(radioPanel);
	}
}
