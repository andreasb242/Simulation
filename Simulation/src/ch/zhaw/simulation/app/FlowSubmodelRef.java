package ch.zhaw.simulation.app;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.window.flow.FlowWindow;
import ch.zhaw.simulation.window.flow.sidebar.DensityConfigurationField;
import ch.zhaw.simulation.window.flow.sidebar.FlowFormulaConfiguration;

public class FlowSubmodelRef {

	private FlowWindow win;
	private ApplicationControl app;
	private FlowEditorControl control;

	public FlowSubmodelRef(ApplicationControl app, SimulationDocument doc, Settings settings, SimulationFlowModel model) {
		this.app = app;
		this.win = new FlowWindow(false);
		this.control = new FlowEditorControl(app, model, doc, win, settings);
		app.addListener(control);
		win.init(control);
		win.addListener(control);

		FlowFormulaConfiguration flowFormulaConfiguration = (FlowFormulaConfiguration) this.win.getConfigurationSidebar();
		DensityConfigurationField densityConfigurationField = flowFormulaConfiguration.getDensityConfigurationField();
		densityConfigurationField.updateDensityComboBox(doc.getXyModel().getDensity());
	}

	public FlowWindow getWin() {
		return win;
	}
	
	public void dispose() {
		this.app.removeListener(control);
		this.win.dispose();
		this.control.dispose();
	}
}
