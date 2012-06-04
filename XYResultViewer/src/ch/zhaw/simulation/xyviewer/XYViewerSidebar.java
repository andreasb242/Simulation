package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.jfree.ui.tabbedui.VerticalLayout;

import butti.javalibs.controls.TitleLabel;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;

public class XYViewerSidebar extends JComponent {
	private static final long serialVersionUID = 1L;

	public XYViewerSidebar(final ResultViewerDialog resultViewer, Vector<XYDensityRaw> rawList) {
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		setLayout(new BorderLayout(5, 5));

		add(new TitleLabel("Dichte:"), BorderLayout.NORTH);

		ButtonGroup radioGroup = new ButtonGroup();
		JPanel radioPanel = new JPanel(new VerticalLayout());

		JRadioButton rbNoDensity = new JRadioButton("Keine");
		rbNoDensity.setFont(rbNoDensity.getFont().deriveFont(Font.ITALIC));
		rbNoDensity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resultViewer.setSelectedDensity(null);
			}
		});
		radioGroup.add(rbNoDensity);
		radioPanel.add(rbNoDensity);
		rbNoDensity.setSelected(true);

		for (final XYDensityRaw raw : rawList) {
			JRadioButton radioButton = new JRadioButton(raw.getDensityName());
			radioButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					resultViewer.setSelectedDensity(raw);
				}
			});
			radioGroup.add(radioButton);
			radioPanel.add(radioButton);
		}
		add(new JScrollPane(radioPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
	}
}
