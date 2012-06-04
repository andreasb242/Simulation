package ch.zhaw.simulation.xyviewer;

import java.io.IOException;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import butti.javalibs.config.Config;
import butti.javalibs.config.FileSettings;
import butti.javalibs.config.Settings;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultEntry;
import ch.zhaw.simulation.plugin.data.XYResultList;
import ch.zhaw.simulation.plugin.data.XYResultStepEntry;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

public class DialogTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

		Vector<XYDensityRaw> rawList = new Vector<XYDensityRaw>();
		XYResultList resultList = new XYResultList(640, 480);
		XYResultEntry re = new XYResultEntry(0);
		re.addStep(new XYResultStepEntry(0, 10, 10, re));
		resultList.add(re);

		XYDensityRaw raw = new XYDensityRaw("Test1", false, 640, 480);
		raw.setMatrixValue(60, 50, 1.2);
		raw.setMatrixValue(60, 51, 1.2);
		raw.setMatrixValue(60, 52, 1.2);
		raw.setMatrixValue(60, 53, 1.2);
		rawList.add(raw);
		raw = new XYDensityRaw("Test2", true, 640, 480);
		raw.setMatrixValue(50, 50, 1.2);
		raw.setMatrixValue(50, 51, 1.2);
		raw.setMatrixValue(50, 52, 1.2);
		raw.setMatrixValue(50, 53, 1.2);
		rawList.add(raw);

		Config.initConfig("(AB)²Simulation", "AB2Simulation");
		Settings settings = new FileSettings("settings.ini");

		Sysintegration sys = SysintegrationFactory.getSysintegration();

		ResultViewerDialog dlg = new ResultViewerDialog(null, settings, sys, resultList, rawList);

		dlg.setVisible(true);
	}

}
