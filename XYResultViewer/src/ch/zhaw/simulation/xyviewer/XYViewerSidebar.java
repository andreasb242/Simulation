package ch.zhaw.simulation.xyviewer;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;

public class XYViewerSidebar extends JComponent {
	private static final long serialVersionUID = 1L;

	private JLabel lblTitle;
	private ButtonGroup radioGroup;
	private JPanel radioPanel;

	public XYViewerSidebar(final XYViewer viewer, Vector<XYDensityRaw> rawList) {
		setLayout(new FlowLayout());

		lblTitle = new JLabel("End-Dichte:");

		radioGroup = new ButtonGroup();
		radioPanel = new JPanel(new GridLayout(0, 1));

		for (final XYDensityRaw raw : rawList) {
			System.out.println(raw.getDensityName());
			JRadioButton radioButton = new JRadioButton(raw.getDensityName());
			radioButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					viewer.setDensitySelected(raw);
				}
			});
			radioGroup.add(radioButton);
			radioPanel.add(radioButton);
		}

		add(lblTitle);
		add(radioPanel);
	}
}
