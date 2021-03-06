package ch.zhaw.simulation.plugin.matlab.gui;

import java.awt.Window;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.util.OS;
import ch.zhaw.simulation.dialog.settings.SettingsPanel;
import ch.zhaw.simulation.filechoosertextfield.FilechooserTextfield;
import ch.zhaw.simulation.plugin.matlab.MatlabParameter;
import ch.zhaw.simulation.plugin.matlab.MatlabTool;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

public class SettingsGui extends JPanel implements SettingsPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private DefaultComboBoxModel toolModel = new DefaultComboBoxModel();
	private JComboBox cbTool = new JComboBox(toolModel);

	private FilechooserTextfield txtWorkpath;
	private FilechooserTextfield execMatlabPath;
	private FilechooserTextfield execOctavePath;
	private FilechooserTextfield execScilabPath;
	private JCheckBox cbGenerate;

	private SimFileFilter filter = new SimFileFilter() {
		@Override
		public String getDescription() {
			return "Executable files";
		}

		@Override
		public boolean accept(File f) {
			return true;
		}

		@Override
		public String getExtension() {
			return null;
		}
	};

	private Settings settings;

	public SettingsGui(Settings settings, Window parent) {
		String defaultPath;
		String path;
		MatlabTool matlabTool;

		this.settings = settings;

		gbm = new GridBagManager(this);

		/*** Tool ***/

		for (MatlabTool t : MatlabTool.values()) {
			toolModel.addElement(t);
		}

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Tool"));
		gbm.setX(1).setY(0).setWeightY(0).setComp(cbTool);

		matlabTool = MatlabTool.fromString(settings.getSetting(MatlabParameter.TOOL, MatlabParameter.DEFAULT_TOOL));
		if (matlabTool != null) {
			cbTool.setSelectedItem(matlabTool);
		}

		Sysintegration sys = SysintegrationFactory.getSysintegration();

		/*** Workpath ***/
		txtWorkpath = new FilechooserTextfield(parent, sys, null, true, false, true);

		gbm.setX(0).setY(2).setWeightY(0).setWeightX(0).setComp(new JLabel("Workpath"));
		gbm.setX(1).setY(2).setWeightY(0).setComp(txtWorkpath);

		path = settings.getSetting(MatlabParameter.WORKPATH, MatlabParameter.DEFAULT_WORKPATH);

		txtWorkpath.setPath(path);

		/*** Matlab Path ***/
		execMatlabPath = new FilechooserTextfield(parent, sys, filter, false, false, true);

		gbm.setX(0).setY(4).setWeightY(0).setWeightX(0).setComp(new JLabel("Matlab Path"));
		gbm.setX(1).setY(4).setWeightY(0).setComp(execMatlabPath);

		if (OS.getOs() == OS.WINDOWS) {
			defaultPath = MatlabParameter.DEFAULT_EXEC_MATLAB_PATH + ".exe";
		} else {
			defaultPath = MatlabParameter.DEFAULT_EXEC_MATLAB_PATH;
		}

		path = settings.getSetting(MatlabParameter.EXEC_MATLAB_PATH, defaultPath);

		execMatlabPath.setPath(path);

		/*** Octave Path ***/
		execOctavePath = new FilechooserTextfield(parent, sys, filter, false, false, true);

		gbm.setX(0).setY(6).setWeightY(0).setWeightX(0).setComp(new JLabel("Octave Path"));
		gbm.setX(1).setY(6).setWeightY(0).setComp(execOctavePath);

		if (OS.getOs() == OS.WINDOWS) {
			defaultPath = MatlabParameter.DEFAULT_EXEC_OCTAVE_PATH + ".exe";
		} else {
			defaultPath = MatlabParameter.DEFAULT_EXEC_OCTAVE_PATH;
		}

		path = settings.getSetting(MatlabParameter.EXEC_OCTAVE_PATH, defaultPath);

		execOctavePath.setPath(path);

		/*** Scilab Path ***/
		execScilabPath = new FilechooserTextfield(parent, sys, filter, false, false, true);

		gbm.setX(0).setY(8).setWeightY(0).setWeightX(0).setComp(new JLabel("Scilab Path"));
		gbm.setX(1).setY(8).setWeightY(0).setComp(execScilabPath);

		if (OS.getOs() == OS.WINDOWS) {
			defaultPath = MatlabParameter.DEFAULT_EXEC_SCILAB_PATH + ".exe";
		} else {
			defaultPath = MatlabParameter.DEFAULT_EXEC_SCILAB_PATH;
		}

		path = settings.getSetting(MatlabParameter.EXEC_SCILAB_PATH, defaultPath);

		execScilabPath.setPath(path);

		/*** Just generate ***/
		boolean selected = settings.isSetting(MatlabParameter.JUST_GENERATE, MatlabParameter.DEFAULT_JUST_GENERATE);
		cbGenerate = new JCheckBox("<html>Simulationsfiles werden nur generiert,<br>" + "und können dann von Hand ausgeführt werden.<br>"
				+ "Alle Dateien werden im Ordner «Workpath» abgelegt.<br>" + "Resultate können in diesen Ordner gelegt werden und dann mit<br>"
				+ "<b>Simulation / Lade letzte Resultate</b> angezeigt werden.</html>", selected);

		gbm.setX(0).setY(9).setWeightY(0).setWeightX(0).setComp(new JLabel("Just generate"));
		gbm.setX(1).setY(9).setWeightY(0).setComp(cbGenerate);
	}

	@Override
	public JPanel getContentsPanel() {
		return this;
	}

	@Override
	public void saveSettings() {
		settings.setSetting(MatlabParameter.JUST_GENERATE, cbGenerate.isSelected());
		settings.setSetting(MatlabParameter.TOOL, cbTool.getSelectedItem().toString());
		settings.setSetting(MatlabParameter.WORKPATH, txtWorkpath.getPath());
		settings.setSetting(MatlabParameter.EXEC_MATLAB_PATH, execMatlabPath.getPath());
		settings.setSetting(MatlabParameter.EXEC_OCTAVE_PATH, execOctavePath.getPath());
		settings.setSetting(MatlabParameter.EXEC_SCILAB_PATH, execScilabPath.getPath());
	}
}
