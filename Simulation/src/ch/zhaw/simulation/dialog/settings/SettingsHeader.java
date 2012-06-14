package ch.zhaw.simulation.dialog.settings;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import butti.javalibs.controls.TitleLabel;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.util.gui.HeaderPanel;

public class SettingsHeader extends HeaderPanel {
	private static final long serialVersionUID = 1L;

	public SettingsHeader() {
		setLayout(new BorderLayout());

		ImageIcon icon = IconLoader.getIcon("preferences", 64);

		JPanel titel = new JPanel(new GridLayout(0, 1));
		titel.setOpaque(false);
		titel.setBorder(new EmptyBorder(12, 0, 12, 0));

		TitleLabel titleLabel = new TitleLabel("Einstellungen von Simulation");
		Font police = titleLabel.getFont().deriveFont(Font.BOLD);
		titleLabel.setFont(police);
		titleLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
		titel.add(titleLabel);

		JLabel lbTitle;
		titel.add(lbTitle = new JLabel("<html>Hier können Sie generelle Einstellungen<br>des Programmes festlegen</html>"));
		police = lbTitle.getFont().deriveFont(Font.PLAIN);
		lbTitle.setFont(police);
		lbTitle.setBorder(new EmptyBorder(0, 24, 0, 0));

		lbTitle = new JLabel(icon);
		lbTitle.setBorder(new EmptyBorder(0, 0, 0, 12));

		add(titel, BorderLayout.WEST);
		add(lbTitle, BorderLayout.EAST);
	}

}
