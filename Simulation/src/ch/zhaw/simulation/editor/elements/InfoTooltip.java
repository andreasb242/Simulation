package ch.zhaw.simulation.editor.elements;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JToolTip;

import org.jdesktop.swingx.VerticalLayout;

import ch.zhaw.simulation.dialog.aboutdlg.GradientPanel;
import ch.zhaw.simulation.gui.HeaderPanel;


public class InfoTooltip extends JToolTip {
	private static final long serialVersionUID = 1L;

	private JLabel lbTitle = new JLabel("Titel");
	private GradientPanel contents = new GradientPanel();
	private HeaderPanel header = new HeaderPanel();
	private JLabel lbFormula = new JLabel();
	private JLabel lbState = new JLabel();

	private VerticalLayout layout;

	public InfoTooltip() {
		layout = new VerticalLayout();
		setLayout(layout);
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
		header.add(lbTitle);
		lbTitle.setFont(lbTitle.getFont().deriveFont(Font.BOLD));

		contents.setLayout(new VerticalLayout());
		
		contents.add(lbFormula);
		contents.add(lbState);

		add(header);
		add(contents);
	}

	public void setTitle(String title) {
		lbTitle.setText(title);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return layout.preferredLayoutSize(this);
	}
	
	@Override
	public void setTipText(String tipText) {
	}


	public void setStatus(String text) {
		lbState.setText(text);
	}

	public void setFormula(String formula) {
		// TODO: low prio: Code formatieren und highlieghten
		lbFormula.setText(formula);
	}
}