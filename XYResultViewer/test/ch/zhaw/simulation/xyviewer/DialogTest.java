package ch.zhaw.simulation.xyviewer;

import java.io.IOException;
import java.util.Vector;

import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultEntry;
import ch.zhaw.simulation.plugin.data.XYResultList;
import ch.zhaw.simulation.plugin.data.XYResultStepEntry;

public class DialogTest {

	public static void main(String[] args) throws IOException {
		Vector<XYDensityRaw> rawList = new Vector<XYDensityRaw>();
		XYResultList resultList = new XYResultList(640, 480);
		XYResultEntry re = new XYResultEntry(0);
		re.addStep(new XYResultStepEntry(0, 10, 10, re));
		resultList.add(re);

		XYDensityRaw raw = new XYDensityRaw("Test1", 640, 480);
		raw.setMatrixValue(60, 50, 1.2);
		raw.setMatrixValue(60, 51, 1.2);
		raw.setMatrixValue(60, 52, 1.2);
		raw.setMatrixValue(60, 53, 1.2);
		rawList.add(raw);
		raw = new XYDensityRaw("Test2", 640, 480);
		raw.setMatrixValue(50, 50, 1.2);
		raw.setMatrixValue(50, 51, 1.2);
		raw.setMatrixValue(50, 52, 1.2);
		raw.setMatrixValue(50, 53, 1.2);
		rawList.add(raw);

		ResultViewerDialog dlg = new ResultViewerDialog(resultList, rawList);

		dlg.setVisible(true);
	}

}
