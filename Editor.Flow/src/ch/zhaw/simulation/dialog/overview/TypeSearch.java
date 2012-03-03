package ch.zhaw.simulation.dialog.overview;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

import butti.javalibs.controls.listcontrol.searchmodules.AbstractSearchModul;



public class TypeSearch extends AbstractSearchModul {
	private JComboBox cbSearch = new JComboBox();

	public TypeSearch(GuiConfig config) {
		cbSearch.addItem("Typ filtern");
		cbSearch.addItem(SimulationGlobalData.class);
		cbSearch.addItem(SimulationParameterData.class);
		cbSearch.addItem(SimulationContainerData.class);
		cbSearch.addItem(FlowValveData.class);

		cbSearch.setRenderer(new TypeSearchRenderer(config, cbSearch.getRenderer()));

		cbSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireSearchChanged();
			}
		});
	}

	@Override
	public JComponent getComponent() {
		return cbSearch;
	}

	@Override
	public Object getValue() {
		return cbSearch.getSelectedItem();
	}

	@Override
	public void setFilter(Object filter) {
		cbSearch.setSelectedItem(filter);
	}

	@Override
	public boolean showField(Object object) {
		if (cbSearch.getSelectedIndex() == 0) {
			return true;
		}

		return object.equals(cbSearch.getSelectedItem());
	}

}
