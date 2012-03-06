package ch.zhaw.simulation.editor.xy.density;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import butti.javalibs.gui.GridBagManager;

public class EditorPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private GridBagManager gbm;
	
	private JTextField txtName = new JTextField();
	private JTextField txtDescription = new JTextField();
	
	public EditorPanel() {
		gbm = new GridBagManager(this, true);
		
		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Name"));
		gbm.setX(0).setY(1).setWeightY(0).setWeightX(0).setComp(new JLabel("Beschreibung"));
		
		gbm.setX(1).setY(0).setWeightY(0).setComp(txtName);
		gbm.setX(1).setY(1).setWeightY(0).setComp(txtDescription);
		
	}

}
