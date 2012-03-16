package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.util.gui.HeaderPanel;

import butti.javalibs.controls.TitleLabel;


public class AboutHeader extends HeaderPanel {
	private static final long serialVersionUID = 1L;

	private ImageIcon icon;

    public AboutHeader() {
        setLayout(new BorderLayout());

        this.icon = IconLoader.getIcon("simulation", 128);

        JPanel titel = new JPanel(new GridLayout(0, 1));
        titel.setOpaque(false);
        titel.setBorder(new EmptyBorder(12, 0, 12, 0));

		TitleLabel titleLabel = new TitleLabel("Information über Simulation");
        Font police = titleLabel.getFont().deriveFont(Font.BOLD);
        titleLabel.setFont(police);
        titleLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
        titel.add(titleLabel);

        JLabel lbTitle;
        titel.add(lbTitle = new JLabel("Programmiert von Andreas Bachmann, Andreas Butti"));
        police = lbTitle.getFont().deriveFont(Font.PLAIN);
        lbTitle.setFont(police);
        lbTitle.setBorder(new EmptyBorder(0, 24, 0, 0));

        titel.add(lbTitle = new JLabel("ZHAW"));
        police = lbTitle.getFont().deriveFont(Font.PLAIN);
        lbTitle.setFont(police);
        lbTitle.setBorder(new EmptyBorder(0, 24, 0, 0));
        
        lbTitle = new JLabel(this.icon);
        lbTitle.setBorder(new EmptyBorder(0, 0, 0, 12));

        add(BorderLayout.WEST, titel);
        add(BorderLayout.EAST, lbTitle);

        setPreferredSize(new Dimension(500, this.icon.getIconHeight() + 24));
    }
}