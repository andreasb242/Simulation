package ch.zhaw.simulation.diagram.tableview;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SimulationTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private SimulationSerie[] data;
	private SimulationSerie timeSerie;

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
			if (timeSerie.getData().size() > rowIndex) {
				return timeSerie.getData().get(rowIndex).time;
			} else {
				return "";
			}
		}

		if (data.length <= columnIndex - 1) {
			return "";
		}

		SimulationSerie serie = data[columnIndex - 1];
		
		if(serie.isConstValue()) {
			return serie.getConstValue();
		}

		Vector<SimulationEntry> row = serie.getData();
		
		if (rowIndex >= row.size()) {
			return ""; // No simulation Data
		}

		return row.get(rowIndex).value;
	}

	public void setSeries(SimulationCollection series) {
		data = series.getSeries();
		rowCount = 0;
		
		for (SimulationSerie d : data) {
			int s = d.getData().size();
			if (rowCount < s) {
				rowCount = s;
				timeSerie = d;
			}
		}
		
		fireTableStructureChanged();
	}
}
