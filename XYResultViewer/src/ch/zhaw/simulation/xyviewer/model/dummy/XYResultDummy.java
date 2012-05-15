package ch.zhaw.simulation.xyviewer.model.dummy;

import java.awt.Color;
import java.awt.Dimension;

import ch.zhaw.simulation.model.xy.ColorCalculator;
import ch.zhaw.simulation.xyviewer.model.attic.XYResult;
import ch.zhaw.simulation.xyviewer.model.attic.XYResultMeso;
import ch.zhaw.simulation.xyviewer.model.attic.XYResultStep;

public class XYResultDummy implements XYResult {
	private XYResultMeso m1 = new XYResultMeso(0, 1, 1);
	private XYResultMeso m2 = new XYResultMeso(1, 100, 50);
	private int add = 1;

	private int lastStep = 0;

	private XYResultStep step = new XYResultStep() {

		@Override
		public int getMesoCount() {
			return 2;
		}

		@Override
		public XYResultMeso getMeso(int n) {
			if (n == 0) {
				return m1;
			}
			return m2;
		}
	};

	@Override
	public int getCount() {
		return 1000;
	}

	@Override
	public XYResultStep getStep(int n) {
		if (lastStep < n) {
			nextStep();
		}
		lastStep = n;

		m2.setX((int) (Math.sin(n / 10.0) * 30.0) + 80);
		m2.setY((int) (Math.cos(n / 10.0) * 40.0) + 80);

		return step;
	}

	private void nextStep() {
		m1.setX(m1.getX() + 1);
		m1.setY(m1.getY() + add);

		if (m1.getY() > 300) {
			add = -1;
		}
	}

	@Override
	public Dimension getModelSize() {
		return new Dimension(600, 300);
	}

	@Override
	public Color[] getColors() {
		return ColorCalculator.calcColors(2);
	}

}
