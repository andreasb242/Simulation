package ch.zhaw.simulation.dialog.density;

import java.util.Vector;

import butti.javalibs.controls.listcontrol.AbstractSortableTableModel;
import butti.javalibs.controls.listcontrol.searchmodules.SearchModul;
import butti.javalibs.controls.listcontrol.searchmodules.Textsearch;
import ch.zhaw.simulation.model.xy.Density;

public class ContentsModel extends AbstractSortableTableModel {
	private static final long serialVersionUID = 1L;

	private static final String[] HEADER = new String[] { "Name", "Beschreibung", "Formel" };
	private static final SearchModul[] SEARCHMODULES = new SearchModul[] { new Textsearch(HEADER[0]), new Textsearch(HEADER[1]), new Textsearch(HEADER[2]) };

	private Vector<Density> data = new Vector<Density>();

	public ContentsModel() {
		Density d = new Density();
		d.setName("a");
		d.setDescription("Gras");
		d.setFormula("sin(x)/x");
		data.add(d);

		d = new Density();
		d.setName("b");
		d.setDescription("Löwenzahn");
		d.setFormula("sqrt(x^2+y^2)");
		data.add(d);
	}

	@Override
	public Object[] getHeader() {
		return HEADER;
	}

	@Override
	public Object getElementAt(int index) {
		return data.get(index);
	}

	@Override
	public SearchModul getSearchModul(int index) {
		return SEARCHMODULES[index];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Density d = data.get(rowIndex);

		if (d == null) {
			return "";
		}

		if (columnIndex == 0) {
			return d.getName();
		}
		if (columnIndex == 1) {
			return d.getDescription();
		}
		if (columnIndex == 0) {
			return d.getFormula();
		}
		return "";
	}

}
