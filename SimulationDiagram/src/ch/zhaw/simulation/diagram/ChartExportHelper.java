package ch.zhaw.simulation.diagram;

import java.io.File;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.dialog.snapshot.SnapshotDialog;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class ChartExportHelper {
	private JFrame parent;
	private Settings settings;
	private Sysintegration sys;

	private static SimFileFilter htmlFileFilter = new SimFileFilter() {

		@Override
		public String getDescription() {
			return "HTML Dokument";
		}

		@Override
		public boolean accept(File f) {
			return (f.isDirectory() || f.getName().endsWith(".html"));
		}

		@Override
		public String getExtension() {
			return ".html";
		}
	};
	public ChartExportHelper(JFrame parent, Settings settings, Sysintegration sys) {
		this.parent = parent;
		this.settings = settings;
		this.sys = sys;
	}

	public void exportChart(ChartPanel chart, String name, int width, int height) {
		ChartExporter expor = new ChartExporter(chart);
		SnapshotDialog dlg = new SnapshotDialog(parent, this.settings, sys, expor, name);
		dlg.setVisible(true);

		// TODO !!! LEGE EXPORT
	}
}
