package ch.zhaw.simulation.diagram.csvview;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private DecimalFormat format = new DecimalFormat("0.00;-0.00");

	public TableRenderer() {
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
	}

	@Override
	protected void setValue(Object value) {
		if (value instanceof Double) {
			super.setValue(format.format((Double) value));
		} else {
			super.setValue(value);
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		return this;
	}

	public void setPrecision(int p) {
		format.setMaximumFractionDigits(p);
		format.setMinimumFractionDigits(p);
	}

}
