package ch.zhaw.simulation.window.flow;

import java.awt.BorderLayout;

import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.gui.FlowEditorView;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.menu.flow.FlowMenubar;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.toolbar.xy.FlowToolbar;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.flow.sidebar.SimulationConfigurationPanel;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;

public class FlowWindow extends SimulationWindow<FlowMenubar, FlowToolbar, FlowEditorView> {
	private static final long serialVersionUID = 1L;

	public FlowWindow(boolean mainWindow) {
		super(mainWindow);
	}

	public void init(FlowEditorControl control) {
		FlowEditorView view = new FlowEditorView(control);
		control.setView(view);

		FlowMenubar menubar = new FlowMenubar(control.getSysintegration(), um, control.getClipboard());
		menubar.initMenusToolbar(control.getRecentMenu().getMenu(), true);

		FlowToolbar tb = new FlowToolbar(control.getSysintegration(), mainWindow);
		tb.initToolbar(view.getUndoHandler());

		init(menubar, tb, view);
		
		
		add(BorderLayout.SOUTH, control.getStatusBar());
	}

	@Override
	protected void initSidebar(FrameSidebar sidebar) {
		final FlowEditorControl control = view.getControl();

		NameFormulaConfiguration formulaConfiguration = new NameFormulaConfiguration(control.getModel(), control.getSelectionModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void showFormulaEditor(NamedSimulationObject data) {
				control.showFormulaEditor(getData());
			}
			
		};
		sidebar.add(formulaConfiguration);

		view.getControl().getSelectionModel().addSelectionListener(formulaConfiguration);

		SimulationConfigurationPanel simConfig = new SimulationConfigurationPanel(control);
		sidebar.add(simConfig);
	}

}
