package ch.zhaw.simulation.plugin.data;

import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

import java.util.HashMap;

/**
 * This class converts a single SimulationCollection with x SimulationSeries (temporary files in workpath)
 * into a single XYResultList with y XYResultEntries (y = number of meso in xy-model) and attach each submodel
 * to the related meso (= XYResultEntry)
 */
public class XYResultChooser {
	private SimulationXYModel xyModel;
	private SimulationCollection collection;

	public XYResultChooser(SimulationXYModel xyModel, SimulationCollection collection) {

		if (xyModel == null || collection == null) {
			throw new NullPointerException("must have a valid object");
		}

		this.xyModel = xyModel;
		this.collection = collection;
	}

	public XYResultList chooseMesoPart() {
		String name;
		String xStr;
		String yStr;
		String submodel;
		String serieName;
		SimulationSerie xSerie;
		SimulationSerie ySerie;
		XYResultList  resultList;
		XYResultEntry resultEntry;
		XYResultStepEntry stepEntry;
		HashMap<Double, Integer> yMap;
		Integer x;
		Integer y;
		int step;
		int colorId = 0;

		resultList = new XYResultList();
		resultList.setModelSize(xyModel.getWidth(), xyModel.getHeight());

		for (MesoData meso : xyModel.getMeso()) {
			name = meso.getName();
			xStr = name + ".position.approx.x";
			yStr = name + ".position.approx.y";
			submodel = name + ".submodel.";
			xSerie = null;
			ySerie = null;
			resultEntry = new XYResultEntry(colorId++);

			// loop over all series in the collection
			// and choose only series from the current meso
			for (SimulationSerie serie : collection) {
				serieName = serie.getName();
				// look for meso x-position
				if (serieName.equals(xStr)) {
					xSerie = serie;
				// look for meso y-position
				} else if (serieName.equals(yStr)) {
					ySerie = serie;
				} else if (serieName.startsWith(submodel)) {
					serie.setName(serieName.replaceAll(submodel, ""));
					resultEntry.addSerie(serie);
				}
			}

			// if it couldn't find at least one meso position (x and y)
			// throw away meso infos and continue with another meso
			if (xSerie == null || ySerie == null) {
				// TODO exception werfen?
				continue;
			}

			// this is a complicated way to attach x and y to a
			// specific time. In general case, it's too much, because
			// every SimulationSerie is in step with each other.
			yMap = new HashMap<Double, Integer>(ySerie.size());

			for (SimulationEntry ySerieEntry : ySerie.getData()) {
				yMap.put(ySerieEntry.time, Double.valueOf(ySerieEntry.value).intValue());
			}

			// reset step count, so every XYResultStepEntry starts with zero
			step = 0;

			// usually, SimulationEntry starts at 0 (start time) and runs until end time
			for (SimulationEntry xSerieEntry : xSerie.getData()) {
				// match y to x over time
				if ((y = yMap.get(xSerieEntry.time)) != null) {
					x = Double.valueOf(xSerieEntry.value).intValue();
					// create new XYResultStepEntry and add it to a XYResultEntry
					stepEntry = new XYResultStepEntry(step++, x, y, resultEntry);
					resultEntry.addStep(stepEntry);
				}
			}
			//

			resultList.add(resultEntry);

		}

		return resultList;
	}
}
