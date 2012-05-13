package ch.zhaw.simulation.diagram.strokeeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ch.zhaw.simulation.diagram.DiagramStrokeFactory;

public class DashComboboxRenderer extends JComponent implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	private Dash dash;
	private boolean isSelected;
	private Color selectionBackground;
	private Color selectionForground;
	private Color background;
	private Color foreground;

	public DashComboboxRenderer() {
		setPreferredSize(new Dimension(100, 25));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this.isSelected = isSelected;
		this.selectionBackground = list.getSelectionBackground();
		this.selectionForground = list.getSelectionForeground();
		this.background = list.getBackground();
		this.foreground = list.getForeground();

		if (value instanceof Dash) {
			this.dash = (Dash) value;
		}

		return this;
	}

	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		int w = getWidth();
		int h = getHeight();

		if (isSelected) {
			g.setColor(selectionBackground);
		} else {
			g.setColor(background);
		}
		g.fillRect(0, 0, w, h);

		if (isSelected) {
			g.setColor(selectionForground);
		} else {
			g.setColor(foreground);
		}

		if (this.dash != null) {
			g.setStroke(DiagramStrokeFactory.createStroke(this.dash.getDash()));
		}

		int y = h / 2 - 1;

		g.drawLine(0, y, w, y);
	}
}
