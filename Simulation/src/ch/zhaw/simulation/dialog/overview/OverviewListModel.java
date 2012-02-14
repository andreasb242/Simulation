package ch.zhaw.simulation.dialog.overview;


import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;

import butti.javalibs.controls.listcontrol.AbstractSortableTableModel;
import butti.javalibs.controls.listcontrol.searchmodules.SearchModul;
import butti.javalibs.controls.listcontrol.searchmodules.Textsearch;


public class OverviewListModel extends AbstractSortableTableModel {
	private static final long serialVersionUID = 1L;
	private SimulationDocument doc;
	private NamedSimulationObject[] data = new NamedSimulationObject[] {};
	private DataComparator comparator = new DataComparator();
	private static final String[] HEADER = new String[] { "Typ", "Name", "Formel" };
	private static final SearchModul[] SEARCHMODULES = new SearchModul[] { new TypeSearch(new GuiConfig()), new Textsearch(HEADER[1]),
			new Textsearch(HEADER[2]) };

	public OverviewListModel(SimulationDocument doc) {
		this.doc = doc;
	}

	@Override
	public Object[] getHeader() {
		return HEADER;
	}

	@Override
	public SearchModul getSearchModul(int index) {
		return SEARCHMODULES[index];
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		NamedSimulationObject d = data[rowIndex];

		if (columnIndex == 0) {
			return d.getClass();
		}
		if (columnIndex == 1) {
			return d.getName();
		}
		if (columnIndex == 2) {
			return d.getFormula();
		}

		return null;
	}

	public void refresh() {
		Vector<NamedSimulationObject> objects = doc.getNamedSimulationObject();
		Collections.sort(objects, comparator);

		data = objects.toArray(new NamedSimulationObject[] {});
		fireTableDataChanged();
	}

	@Override
	public NamedSimulationObject getElementAt(int index) {
		return data[index];
	}

	public static class DataComparator implements Comparator<NamedSimulationObject> {

		private int getRanking(NamedSimulationObject o) {
			if (o instanceof SimulationGlobal) {
				return 1;
			}
			if (o instanceof SimulationParameter) {
				return 2;
			}
			if (o instanceof SimulationContainer) {
				return 3;
			}
			if (o instanceof FlowParameterPoint) {
				return 4;
			}

			System.out.println("DataComparator: Unknown type: " + o.getClass().getName());

			return 0;
		}

		@Override
		public int compare(NamedSimulationObject o1, NamedSimulationObject o2) {
			int i1 = getRanking(o1);
			int i2 = getRanking(o2);

			if (i1 == i2) {
				return o1.getName().compareTo(o2.getName());
			}

			return i1 - i2;
		}
	}
}
