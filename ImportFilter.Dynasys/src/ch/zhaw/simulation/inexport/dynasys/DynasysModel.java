package ch.zhaw.simulation.inexport.dynasys;

import butti.javalibs.config.Settings;

/**
 * Dynasys Konfiguration
 * 
 * @author Andreas Butti
 */
public class DynasysModel {
	private Settings settings;

	private static final double DEFAULT_SCALE_FACTOR = 1.9;
	private static final boolean DEFAULT_REALIGN = true;
	private static final int DEFAULT_PADDING_LEFT = 10;
	private static final int DEFAULT_PADDING_TOP = 10;
	private static final int DEFAULT_FLOWPOINT_MOVE = -28;

	private double scaleFactor = DEFAULT_SCALE_FACTOR;
	private boolean realign = DEFAULT_REALIGN;
	private int paddingLeft = DEFAULT_PADDING_LEFT;
	private int paddingTop = DEFAULT_PADDING_TOP;
	private int flowPointMove = DEFAULT_FLOWPOINT_MOVE;

	public DynasysModel(Settings settings) {
		this.settings = settings;
		scaleFactor = settings.getSetting("import.dynasys.scale", scaleFactor);
		realign = settings.isSetting("import.dynasys.realign", true);
		paddingLeft = (int) settings.getSetting("import.dynasys.paddingLeft", paddingLeft);
		paddingTop = (int) settings.getSetting("import.dynasys.paddingTop", paddingTop);
		flowPointMove = (int) settings.getSetting("import.dynasys.flowPointMove", flowPointMove);
	}

	public void reset() {
		setScaleFactor(DEFAULT_SCALE_FACTOR);
		setRealign(DEFAULT_REALIGN);
		setPaddingLeft(DEFAULT_PADDING_LEFT);
		setPaddingTop(DEFAULT_PADDING_TOP);
		setFlowPointMove(DEFAULT_FLOWPOINT_MOVE);
	}

	public void setScaleFactor(double scaleFactor) {
		if (this.scaleFactor == scaleFactor) {
			return;
		}
		this.scaleFactor = scaleFactor;
		settings.setSetting("import.dynasys.scale", scaleFactor);
	}

	public void setRealign(boolean realign) {
		if (this.realign == realign) {
			return;
		}
		this.realign = realign;
		settings.setSetting("import.dynasys.realign", realign);
	}

	public void setPaddingLeft(int paddingLeft) {
		if (this.paddingLeft == paddingLeft) {
			return;
		}
		this.paddingLeft = paddingLeft;
		settings.setSetting("import.dynasys.paddingLeft", paddingLeft);
	}

	public void setPaddingTop(int paddingTop) {
		if (this.paddingTop == paddingTop) {
			return;
		}
		this.paddingTop = paddingTop;
		settings.setSetting("import.dynasys.paddingTop", paddingTop);
	}

	public void setFlowPointMove(int flowPointMove) {
		if (this.flowPointMove == flowPointMove) {
			return;
		}
		this.flowPointMove = flowPointMove;
		settings.setSetting("import.dynasys.flowPointMove", flowPointMove);
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	public boolean isRealign() {
		return realign;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public int getFlowPointMove() {
		return flowPointMove;
	}
}
