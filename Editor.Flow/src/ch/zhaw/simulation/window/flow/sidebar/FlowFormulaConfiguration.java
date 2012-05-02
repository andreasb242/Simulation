package ch.zhaw.simulation.window.flow.sidebar;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.window.sidebar.config.ConfigurationSidebarPanel;

public class FlowFormulaConfiguration extends ConfigurationSidebarPanel<SimulationFlowModel> {
	private static final long serialVersionUID = 1L;

	private FlowEditorControl control;
	private DensityConfigurationField densityConfig;

	public FlowFormulaConfiguration(SimulationFlowModel model, FlowEditorControl control, SelectionModel selectionModel) {
		super(model, selectionModel);
		this.control = control;
		
		densityConfig.init(this.control.getDoc().getXyModel());
	}

	@Override
	public void showFormulaEditor(NamedFormulaData data) {
		this.control.showFormulaEditor(data);
	}

	@Override
	protected void instanceConfigurationFields() {
		super.instanceConfigurationFields();

		densityConfig = new DensityConfigurationField(getModel());
		addConfigurationField(densityConfig);
	}

}
