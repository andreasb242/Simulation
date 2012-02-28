package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextPane;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.editor.xy.DensityDraw;

public class DensitySidebar extends JXTaskPane {
	private static final long serialVersionUID = 1L;

	private JTextPane txtDensity = new JTextPane();
	private JButton btOk = new JButton("Formel parsern");
	
	public DensitySidebar(final DensityDraw draw, final JComponent comp) {
		setTitle("Dichte");
		
		add(txtDensity);
		add(btOk);
		
		btOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				draw.setFormula(txtDensity.getText());
				
				draw.updateImage();
				
				comp.repaint();
			}
		});
	}
}
