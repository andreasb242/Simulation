package ch.zhaw.simulation.plugin.matlab.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.util.OpenFileBrowser;
import ch.zhaw.simulation.filechooser.ExecDirChooser;
import ch.zhaw.simulation.filechooser.TxtDirChooser;
import ch.zhaw.simulation.plugin.matlab.MatlabParameter;
import ch.zhaw.simulation.plugin.matlab.MatlabTool;

public class SettingsGui extends JPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private DefaultComboBoxModel toolModel = new DefaultComboBoxModel();
	private JComboBox cbTool = new JComboBox(toolModel);

	private TxtDirChooser txtWorkpath;
	private ExecDirChooser execMatlabPath;
	private ExecDirChooser execOctavePath;
	private ExecDirChooser execScilabPath;

	public SettingsGui(final Settings settings, Window parent) {
		String defaultPath;
		String path;
		MatlabTool matlabTool;

		gbm = new GridBagManager(this);

		setBorder(new TitledBorder("Software"));

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

		cbTool.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting(MatlabParameter.TOOL, cbTool.getSelectedItem().toString());
			}
		});

		/*** Workpath ***/
		txtWorkpath = new TxtDirChooser(parent, false);

		gbm.setX(0).setY(2).setWeightY(0).setWeightX(0).setComp(new JLabel("Workpath"));
		gbm.setX(1).setY(2).setWeightY(0).setComp(txtWorkpath);

		path = settings.getSetting(MatlabParameter.WORKPATH, MatlabParameter.DEFAULT_WORKPATH);

		txtWorkpath.setPath(path);

		txtWorkpath.addChangeListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting(MatlabParameter.WORKPATH, txtWorkpath.getPath());
			}
		});

		/*** Matlab Path ***/
		execMatlabPath = new ExecDirChooser(parent, false);

		gbm.setX(0).setY(4).setWeightY(0).setWeightX(0).setComp(new JLabel("Matlab Path"));
		gbm.setX(1).setY(4).setWeightY(0).setComp(execMatlabPath);

		if (OpenFileBrowser.getOs().equals(OpenFileBrowser.OS.WINDOWS)) {
			defaultPath = MatlabParameter.DEFAULT_EXEC_MATLAB_PATH + ".exe";
		} else {
			defaultPath = MatlabParameter.DEFAULT_EXEC_MATLAB_PATH;
		}

		path = settings.getSetting(MatlabParameter.EXEC_MATLAB_PATH, defaultPath);

		execMatlabPath.setPath(path);

		execMatlabPath.addChangeListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting(MatlabParameter.EXEC_MATLAB_PATH, execMatlabPath.getPath());
			}
		});

		/*** Octave Path ***/
		execOctavePath = new ExecDirChooser(parent, false);

		gbm.setX(0).setY(6).setWeightY(0).setWeightX(0).setComp(new JLabel("Octave Path"));
		gbm.setX(1).setY(6).setWeightY(0).setComp(execOctavePath);


		if (OpenFileBrowser.getOs().equals(OpenFileBrowser.OS.WINDOWS)) {
			defaultPath = MatlabParameter.DEFAULT_EXEC_OCTAVE_PATH + ".exe";
		} else {
			defaultPath = MatlabParameter.DEFAULT_EXEC_OCTAVE_PATH;
		}

		path = settings.getSetting(MatlabParameter.EXEC_OCTAVE_PATH, defaultPath);

		execOctavePath.setPath(path);

		execOctavePath.addChangeListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting(MatlabParameter.EXEC_OCTAVE_PATH, execOctavePath.getPath());
			}
		});

		/*** Scilab Path ***/
		execScilabPath = new ExecDirChooser(parent, false);

		gbm.setX(0).setY(8).setWeightY(0).setWeightX(0).setComp(new JLabel("Scilab Path"));
		gbm.setX(1).setY(8).setWeightY(0).setComp(execScilabPath);


		if (OpenFileBrowser.getOs().equals(OpenFileBrowser.OS.WINDOWS)) {
			defaultPath = MatlabParameter.DEFAULT_EXEC_SCILAB_PATH + ".exe";
		} else {
			defaultPath = MatlabParameter.DEFAULT_EXEC_SCILAB_PATH;
		}

		path = settings.getSetting(MatlabParameter.EXEC_SCILAB_PATH, defaultPath);

		execScilabPath.setPath(path);

		execScilabPath.addChangeListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting(MatlabParameter.EXEC_SCILAB_PATH, execScilabPath.getPath());
			}
		});

	}
}
