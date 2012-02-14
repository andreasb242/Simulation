package simulation.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import simulation.data.SimulationCollection;
import simulation.data.SimulationEntry;
import simulation.data.SimulationSerie;

public class SimulationTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private SimulationSerie[] data;
	private int rowCount = 0;

	public SimulationTableModel(SimulationCollection series) {
		setSeries(series);
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Zeit";
		}

		return data[column - 1].getName();
	}

	@Override
	public int getColumnCount() {
		return data.length + 1; // + Zeit
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// Wenn das Feld editierbar ist kann einfach ein Wert kopiert werden, es
		// kann nicht wirklich etwas editiert werden, die Werte werden nicht
		// gespeichert...

		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return data[0].getData().get(rowIndex).time;
		}

		Vector<SimulationEntry> row = data[columnIndex - 1].getData();

		if (rowIndex >= row.size()) {
			return ""; // No simulation Data
		}

		return row.get(rowIndex).value;
	}

	public void setSeries(SimulationCollection series) {
		data = series.getSeries();
		rowCount = 0;
		if (data.length > 0) {
			rowCount = data[0].getData().size();
		}
		fireTableStructureChanged();
	}
}
