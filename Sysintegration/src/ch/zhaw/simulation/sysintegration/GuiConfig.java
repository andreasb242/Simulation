package ch.zhaw.simulation.sysintegration;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

public class GuiConfig {
	private Color objectBorder;
	private Color objectBorderFocus;

	public GuiConfig() {
		objectBorder = Color.GRAY;
		objectBorderFocus = Color.GRAY;
	}

	public Paint getObjectBorder(boolean selected) {
		if (selected) {
			return objectBorderFocus;
		}
		return objectBorder;
	}

	public Paint getParameterPaint(int width, int height, boolean selected) {
		if (selected) {
			return new GradientPaint(0, 0, new Color(0xffe0c3), 0, height, new Color(0xffa857), false);
		}
		return new GradientPaint(0, 0, new Color(0xfffec3), 0, height, new Color(0xfffd57), false);
	}

	public Paint getGlobalPaint(int width, int height, boolean selected) {
		if (selected) {
			return new GradientPaint(0, 0, new Color(0xb6f8c9), 0, height, new Color(0xf1e489), false);
		}
		return new GradientPaint(0, 0, new Color(0xb6f8c9), 0, height, new Color(0xb9dcc3), false);
	}

	public Paint getContainerPaintTop(int width, int height, boolean selected) {
		if (selected) {
			return new Color(0xffa857);
		}
		return new Color(0xeeeeee);
	}

	public Paint getContainerPaint(int width, int height, boolean selected) {
		if (selected) {
			return new GradientPaint(0, 0, new Color(0xffe0c3), width, 0, new Color(0xffa857), false);
		}
		return new GradientPaint(0, 0, new Color(0xffffff), width, 0, new Color(0xcccccc), false);
	}

	public Color getSelectionColor() {
		return new Color(0x7290ff);
	}

	public Color getSelectionForegroundColor() {
		return new Color(0xb9e4ff);
	}

	public float getSelectionAlpha() {
		return 0.5f;
	}

	public int getConnectorPointWidth() {
		return 10;
	}

	public Paint getConnectorPaint(int width, int height, boolean selected) {
		if (selected) {
			return new GradientPaint(0, 0, new Color(0xffe0c3), 0, height, new Color(0xffa857), false);
		}
		return new GradientPaint(0, 0, new Color(0xfffec3), 0, height, new Color(0xfffd57), false);
	}

	public Paint getConnectorBorderPaint(int width, int height, boolean selected) {
		return Color.BLACK;
	}

	public Color getConnectorLineColor() {
		return Color.BLACK;
	}

	public Paint getTextBackgroundPaint(int width, int height, boolean selected) {
		if (selected) {
			return new GradientPaint(0, 0, new Color(0xfff0d3), 0, height, new Color(0xffc877), false);
		}
		return new Color(0xfafafa);
	}

	public Color getTextLineColor(boolean selected) {
		return Color.GRAY;
	}

	public Color getConnectorLineColorSelected() {
		return new Color(0xffa857);
	}

	private static final Color FLOW_ARROW_C1 = new Color(0x1a80ff);
	private static final Color FLOW_ARROW_C2 = new Color(0xbedbff);

	public Paint getFlowArrowBackground(int width, int height) {
		return new GradientPaint(0, 0, FLOW_ARROW_C1, 0, height, FLOW_ARROW_C2, false);
	}

	public Paint getFlowArrowForeground(int width, int height) {
		return new GradientPaint(0, 0, FLOW_ARROW_C2, 0, height, FLOW_ARROW_C1, false);
	}

	public Paint getFlowArrowBorder(boolean selected) {
		return Color.BLACK;
	}

	public Paint getFlowArrowFill(boolean selected) {
		if (selected) {
			return new Color(215, 215, 215);
		}
		return Color.LIGHT_GRAY;
	}

	public int getFlowParameterSize() {
		return 50;
	}

	public Paint getFlowParameterBackground(int width, int height, boolean selected) {
		if (selected) {
			return new GradientPaint(0, 0, new Color(0xffe0c3), 0, height, new Color(0xffa857), false);
		}
		return new GradientPaint(0, 0, new Color(0xfffec3), 0, height, new Color(0xfffd57), false);
	}

	public Paint getFlowParameterBorder(int width, int heigth, boolean selected) {
		return Color.BLACK;
	}

	public Paint getDocumentBackground(int width, int height) {
		return new GradientPaint(0, 0, new Color(0xf7f7f7), width, 0, new Color(0xcbcbcb), false);
	}

	public int getFlowArrowSize() {
		return 50;
	}

	public Color getRasterColor() {
		return Color.LIGHT_GRAY;
	}
}
