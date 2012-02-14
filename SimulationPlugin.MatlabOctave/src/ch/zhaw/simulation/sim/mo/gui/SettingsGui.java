package ch.zhaw.simulation.sim.mo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.sim.mo.SimulationTool;

public class SettingsGui extends JPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private DefaultComboBoxModel toolModel = new DefaultComboBoxModel();
	private JComboBox cbTool = new JComboBox(toolModel);

	public SettingsGui(Settings settings) {
		gbm = new GridBagManager(this);

		setBorder(new TitledBorder("Software"));

		for (SimulationTool t : SimulationTool.values()) {
			toolModel.addElement(t);
		}

		gbm.setX(0).setY(0).setWeightY(0).setComp(new JLabel("Tool"));
		gbm.setX(1).setY(0).setWeightY(0).setComp(cbTool);

		String tool = settings.getSetting("sim.mo.tool", "Octave");
		SimulationTool t = SimulationTool.fromString(tool);
		if (t != null) {
			cbTool.setSelectedItem(t);
		}
		
		cbTool.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(cbTool.getSelectedItem());
			}
		});
	}
}
