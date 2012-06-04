package ch.zhaw.simulation.plugin.matlab;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;

import java.io.*;
import java.util.Vector;

public class XYDensityParser {

	private SimulationXYModel xyModel;

	public XYDensityParser(SimulationXYModel xyModel) {
		this.xyModel = xyModel;
	}

	public Vector<XYDensityRaw> parse(String workpath) {
		Vector<XYDensityRaw> rawList;
		XYDensityRaw raw;
		BufferedReader reader;
		String line;
		String cell[];
		int x;
		int y;

		rawList = new Vector<XYDensityRaw>();

		for (DensityData density : xyModel.getDensity()) {
			String densityName = density.getName();
			boolean logView = density.isDisplayLogarithmic();

			try {
				reader = new BufferedReader(new FileReader(new File(workpath + File.separator + "data_density." + densityName + ".txt")));

				// Prefetch xlen and ylen
				if ((line = reader.readLine()) == null) {
					Errorhandler.logError("file of density " + densityName + " is empty!");
					continue;
				}

				cell = line.split(" ");
				if (cell.length != 2) {
					Errorhandler.logError("can't extract dimension of density " + densityName);
					continue;
				}
				x = Integer.valueOf(cell[0]);
				y = Integer.valueOf(cell[0]);
				raw = new XYDensityRaw(densityName, logView,  x, y);

				for (y = 0; (line = reader.readLine()) != null; y++) {
					cell = line.split(" ");
					for (x = 0; x < cell.length; x++) {
						raw.setMatrixValue(x, y, Double.valueOf(cell[x]));
					}
				}
				rawList.add(raw);
			} catch (FileNotFoundException e) {
				Errorhandler.logError(e, e.getMessage());
			} catch (IOException e) {
				Errorhandler.logError(e, e.getMessage());
			}
		}

		return rawList;
	}
}
