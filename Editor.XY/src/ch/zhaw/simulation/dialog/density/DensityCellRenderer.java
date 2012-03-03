package ch.zhaw.simulation.dialog.density;

import java.awt.Graphics2D;

import butti.javalibs.controls.AbstractListCellRenderer;
import ch.zhaw.simulation.model.xy.DensityData;

public class DensityCellRenderer extends AbstractListCellRenderer<DensityData> {
	private static final long serialVersionUID = 1L;

	public DensityCellRenderer() {
		super(50);
	}

	@Override
	protected void calculateMinWidth(DensityData data) {
	}

	@Override
	protected void paintData(Graphics2D g, DensityData data) {
		setTitleFont();
		drawStringFilter(data.getName(), 40, 20, 0);

		setDefaultFont();
		drawStringFilter(data.getDescription(), 100, 20, 1);

		drawStringFilter(data.getFormula(), 40, 40, 1);
	}

}
