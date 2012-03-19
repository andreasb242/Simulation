package ch.zhaw.simulation.window.flow.sidebar;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;

public class FlowFormulaConfiguration extends NameFormulaConfiguration {
	private static final long serialVersionUID = 1L;

	private FlowEditorControl control;

	public FlowFormulaConfiguration(FlowEditorControl control, AbstractSimulationModel<?> model, SelectionModel selectionModel) {
		super(model, selectionModel);

		this.control = control;
	}

	@Override
	public void showFormulaEditor(AbstractNamedSimulationData data) {
		this.control.showFormulaEditor(getData());
	}

}
