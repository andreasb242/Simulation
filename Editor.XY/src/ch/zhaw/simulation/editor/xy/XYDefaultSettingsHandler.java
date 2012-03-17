package ch.zhaw.simulation.editor.xy;

import java.awt.Point;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class XYDefaultSettingsHandler {
	private Settings settings;

	private static final String PREFIX = "xymodel.defaultsettings.";

	public XYDefaultSettingsHandler(Settings settings) {
		this.settings = settings;
	}

	public void load(SimulationXYModel model) {
		model.setWidth((int) this.settings.getSetting(PREFIX + "width", 400));
		model.setHeight((int) this.settings.getSetting(PREFIX + "height", 400));

		Point zero = new Point();
		zero.x = (int) this.settings.getSetting(PREFIX + "zero.x", 200);
		zero.y = (int) this.settings.getSetting(PREFIX + "zero.y", 200);
		model.setZero(zero);

		model.setShowGrid(this.settings.isSetting(PREFIX + "grid.visible", true));
		model.setGrid((int) this.settings.getSetting(PREFIX + "grid.value", 20));

		model.setShowDensityArrow(this.settings.isSetting(PREFIX + "density.showarrow", false));
		model.setShowDensityColor(this.settings.isSetting(PREFIX + "density.showcolor", true));

		model.fireSizeRasterChanged();
	}

	public void save(SimulationXYModel model) {
		this.settings.setSetting(PREFIX + "width", model.getWidth());
		this.settings.setSetting(PREFIX + "height", model.getHeight());
		this.settings.setSetting(PREFIX + "zero.x", model.getZero().x);
		this.settings.setSetting(PREFIX + "zero.y", model.getZero().y);

		this.settings.setSetting(PREFIX + "grid.visible", model.isShowGrid());
		this.settings.setSetting(PREFIX + "grid.value", model.getGrid());

		this.settings.setSetting(PREFIX + "density.showarrow", model.isShowDensityArrow());
		this.settings.setSetting(PREFIX + "density.showcolor", model.isShowDensityColor());
	}

}
