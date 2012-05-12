package ch.zhaw.simulation.diagram;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.dialog.snapshot.SnapshotDialog;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class ChartExportHelper {
	private JFrame parent;
	private Settings settings;
	private Sysintegration sys;

	public ChartExportHelper(JFrame parent, Settings settings, Sysintegration sys) {
		this.parent = parent;
		this.settings = settings;
		this.sys = sys;
	}

	public void exportChart(ChartPanel chart, String name, int width, int height) {
		ChartExporter expor = new ChartExporter(chart, name);
		SnapshotDialog dlg = new SnapshotDialog(parent, this.settings, sys, expor, name);
		dlg.setVisible(true);
	}
}
