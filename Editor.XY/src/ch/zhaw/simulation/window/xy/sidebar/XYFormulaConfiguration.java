package ch.zhaw.simulation.window.xy.sidebar;

import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.window.sidebar.config.ConfigurationSidebarPanel;
import ch.zhaw.simulation.window.xy.sidebar.config.SubmodelConfigurationField;

public abstract class XYFormulaConfiguration extends ConfigurationSidebarPanel<SimulationXYModel> {
	private static final long serialVersionUID = 1L;

	// TODO: private JLabel lbxDot = new JLabel("ẋ");
	// TODO: private JLabel lbyDot = new JLabel("ẏ");

	public XYFormulaConfiguration(SimulationXYModel model, SelectionModel selectionModel) {
		super(model, selectionModel);
	}
	
	@Override
	protected void instanceConfigurationFields() {
		super.instanceConfigurationFields();
		
		addConfigurationField(new SubmodelConfigurationField(getModel()));
	}
	
}
