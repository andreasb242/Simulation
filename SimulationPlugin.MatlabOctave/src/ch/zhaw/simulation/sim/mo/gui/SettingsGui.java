package ch.zhaw.simulation.sim.mo.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import butti.filechooser.TxtDirChooser;
import butti.javalibs.config.Settings;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.sim.mo.SimulationTool;

public class SettingsGui extends JPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private DefaultComboBoxModel toolModel = new DefaultComboBoxModel();
	private JComboBox cbTool = new JComboBox(toolModel);

	private TxtDirChooser txtWorkpath;

	public SettingsGui(final Settings settings, Window parent) {
		gbm = new GridBagManager(this);
		txtWorkpath = new TxtDirChooser(parent, false);

		setBorder(new TitledBorder("Software"));

		for (SimulationTool t : SimulationTool.values()) {
			toolModel.addElement(t);
		}

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Tool"));
		gbm.setX(1).setY(0).setWeightY(0).setComp(cbTool);

		String tool = settings.getSetting("tool", "Octave");
		SimulationTool t = SimulationTool.fromString(tool);
		if (t != null) {
			cbTool.setSelectedItem(t);
		}

		cbTool.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting("tool", cbTool.getSelectedItem().toString());
			}
		});

		gbm.setX(0).setY(2).setWeightY(0).setWeightX(0).setComp(new JLabel("Workpath"));
		gbm.setX(1).setY(2).setWeightY(0).setComp(txtWorkpath);

		String defaultWorkpath = new File(".").getAbsolutePath() + File.separator + "workpath";
		String workpath = settings.getSetting("workpath", defaultWorkpath);

		txtWorkpath.setPath(workpath);

		txtWorkpath.addChangeListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting("workpath", txtWorkpath.getPath());
			}
		});
	}
}
