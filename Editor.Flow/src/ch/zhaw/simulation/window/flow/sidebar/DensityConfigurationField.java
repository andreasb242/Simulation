package ch.zhaw.simulation.window.flow.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import ch.zhaw.simulation.editor.density.DensityListModel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.window.sidebar.config.SingleConfigurationField;

/**
 * The configruation filds to select density source
 * 
 * @author Andreas Butti
 */
public class DensityConfigurationField extends SingleConfigurationField {
	private DensityListModel densityModel;
	private JComboBox cbDensity = new JComboBox();
	private SimulationFlowModel model;

	public DensityConfigurationField(SimulationFlowModel model) {
		this.model = model;
	}

	@Override
	public void dispose() {
		if (this.densityModel != null) {
			this.densityModel.dispose();
			this.densityModel = null;
		}
	}

	@Override
	public int getOrder() {
		return 600;
	}

	@Override
	public void init(GroupLayout layout, SequentialGroup g, ParallelGroup leftGroup, ParallelGroup rightGroup) {
		JLabel lbValue = new JLabel("Dichte");

		leftGroup.addComponent(lbValue);
		rightGroup.addComponent(cbDensity);
		addComponent(lbValue);
		addComponent(cbDensity);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lbValue).addComponent(cbDensity));

		cbDensity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DensityData d = (DensityData) cbDensity.getSelectedItem();
				SimulationDensityContainerData dc = (SimulationDensityContainerData) getData();
				if (dc != null) {
					dc.setDensity(d);
					model.fireObjectChanged(dc);
				}
			}
		});
	}

	@Override
	protected void loadData() {
		SimulationDensityContainerData dc = (SimulationDensityContainerData) getData();
		DensityData d = dc.getDensity();
		cbDensity.setSelectedItem(d);
	}

	@Override
	protected boolean canHandleData(AbstractNamedSimulationData data) {
		return densityModel != null && data instanceof SimulationDensityContainerData;
	}

	public void init(SimulationXYModel xyModel) {
		if (xyModel != null) {
			densityModel = new DensityListModel(xyModel);
			cbDensity.setModel(densityModel);
			System.out.println("DensityConfigurationField::xyModel != null");
		} else {
			System.out.println("DensityConfigurationField::xyModel == null");
		}
	}

	public void updateDensityComboBox(Vector<DensityData> densityList) {
		cbDensity.removeAll();
		for (DensityData density : densityList) {
			cbDensity.addItem(density);
		}
	}
}
