package ch.zhaw.simulation.diagram.csv;

import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.TreeMap;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

public class CSVSaver {
	private Settings settings;
	private Window parent;
	private SimulationCollection collection;
	private int serieCount;

	private SimFileFilter filterCSV = new SimFileFilter() {
		@Override
		public boolean accept(File f) {
			return (f.isDirectory() || f.getName().endsWith(".csv"));
		}

		@Override
		public String getDescription() {
			return "CSV Tabelle";
		}

		@Override
		public String getExtension() {
			return ".csv";
		}
	};

	public CSVSaver(Window parent, Settings settings, SimulationCollection collection) {
		this.parent = parent;
		this.settings = settings;
		this.collection = collection;
	}

	// TODO !!!!!!!!!!!!!!!!!!!! Konstante ausgeben
	private void save(File file) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(file));

		DecimalFormat format = new DecimalFormat("0.############");

		TreeMap<Double, CsvEntry> data = new TreeMap<Double, CSVSaver.CsvEntry>();

		this.serieCount = collection.size();

		out.write("time;");

		for (int x = 0; x < serieCount; x++) {
			SimulationSerie s = collection.getSerie(x);
			out.write(s.getName());
			out.write(";");

			for (SimulationEntry d : s.getData()) {
				CsvEntry e = data.get(d.time);
				if (e == null) {
					e = new CsvEntry(d.time);
					data.put(d.time, e);
				}

				e.data[x] = d.value;
			}
		}
		out.write("\n");

		for (CsvEntry e : data.values()) {
			out.write(format.format(e.time));
			out.write(";");

			for (double d : e.data) {
				if (!Double.isNaN(d)) {
					out.write(format.format(d));
				}
				out.write(";");
			}

			out.write("\n");
		}

		out.flush();
		out.close();
	}

	public void save() {
		String lastSavePath = settings.getSetting("table.lastSavePath");
		
		File file = SysintegrationFactory.getSysintegration().showSaveDialog(parent, filterCSV, lastSavePath);

		if (file == null) {
			return;
		}

		settings.setSetting("table.lastSavePath", file.getParent());

		try {
			save(file);
		} catch (IOException e) {
			Errorhandler.showError(e, "Speichern ist fehlgeschlagen!");
		}
	}

	class CsvEntry {
		private double[] data;
		private double time;

		public CsvEntry(double time) {
			this.time = time;
			data = new double[serieCount];
			for (int i = 0; i < serieCount; i++) {
				data[i] = Double.NaN;
			}
		}
	}
}
