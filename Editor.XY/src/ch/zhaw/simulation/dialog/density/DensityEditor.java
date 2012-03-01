package ch.zhaw.simulation.dialog.density;

import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.controls.listcontrol.SortableTable;
import butti.javalibs.gui.GridBagManager;

public class DensityEditor extends JDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;
	private SortableTable table;
	private ContentsModel model = new ContentsModel();
	
	public DensityEditor() {
		gbm = new GridBagManager(this, true);
		
		table = new SortableTable(model);
		table.setSearchEnabled(true);
		table.setCellRenderer(new DensityCellRenderer());
		
		gbm.setX(0).setY(0).setHeight(3).setComp(table);
		
		gbm.setX(1).setY(0).setWeightX(0).setWeightY(0).setComp(new TitleLabel("Name"));
		gbm.setX(1).setY(1).setWeightX(0).setWeightY(0).setComp(new TitleLabel("Beschreibung"));
		
		gbm.setX(2).setY(0).setWeightY(0).setComp(new JTextField());
		gbm.setX(2).setY(1).setWeightY(0).setComp(new JTextField());
		
		JPanel p = new JPanel();
		p.setBackground(Color.RED);
		p.setOpaque(true);
		
		gbm.setX(1).setWidth(2).setY(2).setComp(p);
		
		pack();
		setLocationRelativeTo(null);
		setModal(true);
	}
	
	public static void main(String[] args) {
		System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

		DensityEditor editor = new DensityEditor();
		editor.setVisible(true);
		System.exit(0);
	}
	
}
