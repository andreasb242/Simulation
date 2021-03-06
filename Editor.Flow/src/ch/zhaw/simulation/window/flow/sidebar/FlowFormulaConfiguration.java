package ch.zhaw.simulation.window.flow.sidebar;

import javax.swing.undo.UndoManager;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.window.sidebar.config.ConfigurationSidebarPanel;

public class FlowFormulaConfiguration extends ConfigurationSidebarPanel<SimulationFlowModel, FlowEditorControl> {
	private static final long serialVersionUID = 1L;

	private DensityConfigurationField densityConfig;

	public FlowFormulaConfiguration(SimulationFlowModel model, FlowEditorControl control, SelectionModel selectionModel, UndoManager undo) {
		super(model, selectionModel, undo, control);
		
		densityConfig.init(control.getDoc().getXyModel());
	}

	@Override
	protected void instanceConfigurationFields() {
		super.instanceConfigurationFields();

		densityConfig = new DensityConfigurationField(getModel());
		addConfigurationField(densityConfig);
	}

	public DensityConfigurationField getDensityConfigurationField() {
		return densityConfig;
	}
	
}
