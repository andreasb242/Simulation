package ch.zhaw.simulation.window.flow.sidebar;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.window.sidebar.config.ConfigurationSidebarPanel;

public class FlowFormulaConfiguration extends ConfigurationSidebarPanel<AbstractSimulationModel<?>> {
	private static final long serialVersionUID = 1L;

	private FlowEditorControl control;

	public FlowFormulaConfiguration(AbstractSimulationModel<?> model, FlowEditorControl control, SelectionModel selectionModel) {
		super(model, selectionModel);

		this.control = control;
	}

	@Override
	public void showFormulaEditor(NamedFormulaData data) {
		this.control.showFormulaEditor(data);
	}

}
