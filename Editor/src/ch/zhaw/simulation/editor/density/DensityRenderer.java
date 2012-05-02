package ch.zhaw.simulation.editor.density;

import java.awt.Graphics2D;

import butti.javalibs.controls.AbstractListCellRenderer;
import ch.zhaw.simulation.model.xy.DensityData;

public class DensityRenderer extends AbstractListCellRenderer<DensityData> {
	public DensityRenderer() {
		super(25);
	}

	private static final long serialVersionUID = 1L;

	private static final int MAX_NAME_LEN = 40;
	private static final int MAX_DESCRIPTION_LEN = 150;

	@Override
	protected void calculateMinWidth(DensityData data) {
		setMinWidth(5, data.getName(), true);
		String desc = data.getDescription();
		if (desc == null) {
			desc = "";
		}
		int w = setMinWidth(MAX_NAME_LEN + 10, desc, false);
		if (w > MAX_NAME_LEN + MAX_DESCRIPTION_LEN) {
			setMinWidthAbsolut(MAX_NAME_LEN + MAX_DESCRIPTION_LEN);
		}
	}

	@Override
	protected void paintData(Graphics2D g, DensityData data) {
		setTitleFont();
		drawStringFilter(data.getName(), 5, 18, MAX_NAME_LEN, 0);

		setDefaultFont();

		String desc = data.getDescription();
		if (desc == null) {
			desc = "";
		}
		drawStringFilter(desc, MAX_NAME_LEN + 10, 18, MAX_DESCRIPTION_LEN, 0);
		fadeOutString(MAX_NAME_LEN + MAX_DESCRIPTION_LEN + 10, 18);
	}

}
