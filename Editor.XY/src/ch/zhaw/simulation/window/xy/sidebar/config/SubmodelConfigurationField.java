package ch.zhaw.simulation.window.xy.sidebar.config;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.undo.UndoManager;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.undo.action.xy.SubModelAssignUndoAction;
import ch.zhaw.simulation.window.sidebar.config.MultipleConfigurationField;
import ch.zhaw.simulation.window.xy.sidebar.SubmodelComboboxModel;

public class SubmodelConfigurationField extends MultipleConfigurationField {
	private JComboBox cbSubmodel;

	private boolean submodelComboboxChanging = false;

	private Object lastSelectedObject;

	private SimulationXYModel model;
	private UndoManager undo;

	public SubmodelConfigurationField(SimulationXYModel model, UndoManager undo) {
		this.model = model;
		this.undo = undo;
	}

	@Override
	public int getOrder() {
		return 300;
	}

	@Override
	public void init(GroupLayout layout, SequentialGroup g, ParallelGroup leftGroup, ParallelGroup rightGroup) {
		SubmodelComboboxModel cbModel = new SubmodelComboboxModel(model.getSubmodels());
		cbSubmodel = new JComboBox(cbModel);
		cbSubmodel.setRenderer(new SubModelRenderer());

		cbSubmodel.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// ignor our own events
				if (submodelComboboxChanging) {
					return;
				}

				if (ItemEvent.SELECTED == e.getStateChange()) {
					if (cbSubmodel.getSelectedItem() != lastSelectedObject) {
						lastSelectedObject = cbSubmodel.getSelectedItem();

						setSubmodel((SubModel) lastSelectedObject);
					}
				}
			}
		});

		JLabel title = new JLabel("Submodel");
		leftGroup.addComponent(title);
		addComponent(title);

		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(title).addComponent(cbSubmodel));
		addComponent(cbSubmodel);

		rightGroup.addComponent(cbSubmodel);
	}

	@Override
	protected boolean canHandleData(AbstractNamedSimulationData data) {
		return data instanceof MesoData;
	}

	@Override
	protected void loadData() {
		SubModel selected = ((MesoData) getData().get(0)).getSubmodel();

		for (AbstractNamedSimulationData d : getData()) {
			MesoData m = (MesoData) d;
			SubModel sub = m.getSubmodel();
			if (sub != selected) {
				selected = null;
				break;
			}
		}

		submodelComboboxChanging = true;
		cbSubmodel.setSelectedItem(selected);
		submodelComboboxChanging = false;
	}

	protected void setSubmodel(SubModel submodel) {
		Vector<AbstractNamedSimulationData> data = getData();

		if (data.size() != 0) {
			undo.addEdit(new SubModelAssignUndoAction(getData(), submodel, model));
		}
	}
}
