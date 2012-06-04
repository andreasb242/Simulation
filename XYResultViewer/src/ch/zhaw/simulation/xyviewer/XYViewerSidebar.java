package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.jfree.ui.tabbedui.VerticalLayout;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;

public class XYViewerSidebar extends JComponent {
	private static final long serialVersionUID = 1L;

	public XYViewerSidebar(final XYViewer viewer, Vector<XYDensityRaw> rawList) {
		setLayout(new BorderLayout());

		ButtonGroup radioGroup = new ButtonGroup();
		JPanel radioPanel = new JPanel(new VerticalLayout());

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
		add(new JScrollPane(radioPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
	}
}
