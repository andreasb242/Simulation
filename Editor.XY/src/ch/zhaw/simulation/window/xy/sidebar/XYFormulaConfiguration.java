package ch.zhaw.simulation.window.xy.sidebar;

import javax.swing.undo.UndoManager;

import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.window.sidebar.config.ConfigurationSidebarPanel;
import ch.zhaw.simulation.window.xy.sidebar.config.MoveConfigurationField;
import ch.zhaw.simulation.window.xy.sidebar.config.SubmodelConfigurationField;

public abstract class XYFormulaConfiguration extends ConfigurationSidebarPanel<SimulationXYModel> {
	private static final long serialVersionUID = 1L;

	public XYFormulaConfiguration(SimulationXYModel model, SelectionModel selectionModel, UndoManager undo) {
		super(model, selectionModel, undo);
	}

	@Override
	protected void instanceConfigurationFields() {
		super.instanceConfigurationFields();

		SimulationXYModel m = getModel();
		addConfigurationField(new SubmodelConfigurationField(m, getUndo()));
		addConfigurationField(new MoveConfigurationField(m));
	}

}
