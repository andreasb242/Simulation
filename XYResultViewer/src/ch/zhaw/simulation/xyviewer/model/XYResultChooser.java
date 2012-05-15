package ch.zhaw.simulation.xyviewer.model;

import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

import java.util.HashMap;

public class XYResultChooser {
	private SimulationXYModel xyModel;
	private SimulationCollection collection;
	private double diff;

	public XYResultChooser(SimulationXYModel xyModel, SimulationCollection collection) {

		if (xyModel == null || collection == null) {
			throw new NullPointerException("must have a valid object");
		}

		this.xyModel = xyModel;
		this.collection = collection;
		this.diff = collection.getEndTime() - collection.getStartTime();
	}


	/*


			// set x/y
			out.println(meso.getName() + ".position.exact.x.value = " + meso.getXCenter() + ";");
			out.println(meso.getName() + ".position.exact.y.value = " + meso.getYCenter() + ";");
			out.println(meso.getName() + ".position.approx.x.value = uint32(" + meso.getName() + ".position.exact.x.value);");
			out.println(meso.getName() + ".position.approx.y.value = uint32(" + meso.getName() + ".position.exact.y.value);");

	 */
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
		//HashMap<Double, Integer> xMap;
		HashMap<Double, Integer> yMap;
		Integer x;
		Integer y;
		int step;

		resultList = new XYResultList();
		resultList.setModelSize(xyModel.getWidth(), xyModel.getHeight());

		XYResultEntry.resetColorCounter();

		for (MesoData meso : xyModel.getMeso()) {
			name = meso.getName();
			xStr = name + ".position.approx.x";
			yStr = name + ".position.approx.y";
			submodel = name + ".submodel.";
			xSerie = null;
			ySerie = null;
			resultEntry = new XYResultEntry();
			for (SimulationSerie serie : collection) {
				serieName = serie.getName();
				if (serieName.equals(xStr)) {
					xSerie = serie;
				} else if (serieName.equals(yStr)) {
					ySerie = serie;
				} else if (serieName.startsWith(submodel)) {
					serie.setName(serieName.replaceAll(submodel, ""));
					resultEntry.addSerie(serie);
				}
			}

			if (xSerie == null || ySerie == null) {
				// TODO exception werfen?
				continue;
			}

			//xMap = new HashMap<Double, Integer>();
			yMap = new HashMap<Double, Integer>(ySerie.size());

			for (SimulationEntry ySerieEntry : ySerie.getData()) {
				yMap.put(ySerieEntry.time, (Integer) Double.valueOf(ySerieEntry.value).intValue());
			}

			step = 0;
			for (SimulationEntry xSerieEntry : xSerie.getData()) {
				if ((y = yMap.get(xSerieEntry.time)) != null) {
					x = Double.valueOf(xSerieEntry.value).intValue();
					stepEntry = new XYResultStepEntry(step++, x, y, resultEntry);
					resultEntry.addStep(stepEntry);
				}
			}

			resultList.add(resultEntry);

		}

		return resultList;
	}
}
