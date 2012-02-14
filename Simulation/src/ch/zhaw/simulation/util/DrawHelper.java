package ch.zhaw.simulation.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class DrawHelper {
	public static Graphics2D antialisingOn(Graphics g) {
		antialisingOn((Graphics2D) g);
		return (Graphics2D) g;
	}

	public static Graphics2D antialisingOff(Graphics g) {
		antialisingOff((Graphics2D) g);
		return (Graphics2D) g;
	}

	public static void antialisingOn(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public static void antialisingOff(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
}
