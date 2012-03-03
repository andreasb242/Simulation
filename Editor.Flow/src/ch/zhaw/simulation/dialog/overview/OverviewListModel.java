package ch.zhaw.simulation.dialog.overview;


import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

import butti.javalibs.controls.listcontrol.AbstractSortableTableModel;
import butti.javalibs.controls.listcontrol.searchmodules.SearchModul;
import butti.javalibs.controls.listcontrol.searchmodules.Textsearch;


public class OverviewListModel extends AbstractSortableTableModel {
	private static final long serialVersionUID = 1L;
	private SimulationFlowModel doc;
	private AbstractNamedSimulationData[] data = new AbstractNamedSimulationData[] {};
	private DataComparator comparator = new DataComparator();
	private static final String[] HEADER = new String[] { "Typ", "Name", "Formel" };
	private static final SearchModul[] SEARCHMODULES = new SearchModul[] { new TypeSearch(new GuiConfig()), new Textsearch(HEADER[1]),
			new Textsearch(HEADER[2]) };

	public OverviewListModel(SimulationFlowModel doc) {
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
		AbstractNamedSimulationData d = data[rowIndex];

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
		Vector<AbstractNamedSimulationData> objects = doc.getNamedSimulationObject();
		Collections.sort(objects, comparator);

		data = objects.toArray(new AbstractNamedSimulationData[] {});
		fireTableDataChanged();
	}

	@Override
	public AbstractNamedSimulationData getElementAt(int index) {
		return data[index];
	}

	public static class DataComparator implements Comparator<AbstractNamedSimulationData> {

		private int getRanking(AbstractNamedSimulationData o) {
			if (o instanceof SimulationGlobalData) {
				return 1;
			}
			if (o instanceof SimulationParameterData) {
				return 2;
			}
			if (o instanceof SimulationContainerData) {
				return 3;
			}
			if (o instanceof FlowValveData) {
				return 4;
			}

			System.out.println("DataComparator: Unknown type: " + o.getClass().getName());

			return 0;
		}

		@Override
		public int compare(AbstractNamedSimulationData o1, AbstractNamedSimulationData o2) {
			int i1 = getRanking(o1);
			int i2 = getRanking(o2);

			if (i1 == i2) {
				return o1.getName().compareTo(o2.getName());
			}

			return i1 - i2;
		}
	}
}
