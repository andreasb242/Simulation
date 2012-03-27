package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;

public abstract class XYFormulaConfiguration extends NameFormulaConfiguration {
	private static final long serialVersionUID = 1L;

	private JLabel lbxDot = new JLabel("ẋ");
	private JLabel lbyDot = new JLabel("ẏ");

	private JComboBox cbSubmodel;

	private Object lastSelectedObject;

	public XYFormulaConfiguration(SimulationXYModel model, SelectionModel selectionModel) {
		super(model, selectionModel);

		gbm.setX(0).setY(50).setWidth(3).setWeightY(0).setComp(lbxDot);
		gbm.setX(0).setY(51).setWidth(3).setWeightY(0).setComp(lbyDot);

		gbm.remove(this.lbValue);
		gbm.remove(this.btEdit);

		SubmodelComboboxModel cbModel = new SubmodelComboboxModel(model.getSubmodels());
		cbSubmodel = new JComboBox(cbModel);
		cbSubmodel.setRenderer(new SubModelRenderer());

		cbSubmodel.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.SELECTED == e.getStateChange()) {
					if (cbSubmodel.getSelectedItem() != lastSelectedObject) {
						lastSelectedObject = cbSubmodel.getSelectedItem();

						setSubmodel((SubModel) lastSelectedObject);
					}
				}
			}
		});

		gbm.setX(0).setY(1000).setWidth(2).setComp(cbSubmodel);
	}

	protected void setSubmodel(SubModel selected) {
		if (this.data == null) {
			return;
		}

		((MesoData) this.data).setSubmodel(selected);
		model.fireObjectChanged(this.data);
	}

	@Override
	public void setSelected(AbstractNamedSimulationData data) {
		super.setSelected(data);

		if (data instanceof MesoData) {
			MesoData m = (MesoData) data;
			cbSubmodel.setSelectedItem(m.getSubmodel());
			cbSubmodel.setVisible(true);
		} else {
			cbSubmodel.setVisible(false);
		}
	}
}
