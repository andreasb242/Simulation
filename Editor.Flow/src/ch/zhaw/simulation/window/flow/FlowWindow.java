package ch.zhaw.simulation.window.flow;

import javax.swing.JMenu;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.gui.FlowEditorView;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.gui.control.FlowToolbar;
import ch.zhaw.simulation.menu.flow.FlowMenubar;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.flow.sidebar.SimulationConfigurationPanel;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;

public class FlowWindow extends SimulationWindow<FlowMenubar, FlowToolbar, FlowEditorView> {
	private static final long serialVersionUID = 1L;

	public FlowWindow(boolean mainWindow) {
		super(mainWindow);
	}

	public void init(FlowEditorControl control, ClipboardInterface clipboard) {
		FlowMenubar menubar = new FlowMenubar(control.getSysintegration(), um, clipboard);
		menubar.initMenusToolbar(new JMenu(), true);

		FlowEditorView view = new FlowEditorView(control);

		FlowToolbar tb = new FlowToolbar(control.getSysintegration(), mainWindow);
		tb.initToolbar(view.getUndoHandler());

		init(menubar, tb, view);
	}

	@Override
	protected void initSidebar(FrameSidebar sidebar) {
		FlowEditorControl control = view.getControl();

		NameFormulaConfiguration formulaConfiguration = new NameFormulaConfiguration(control.getModel(), control.getSelectionModel());
		sidebar.add(formulaConfiguration);

		view.getControl().getSelectionModel().addSelectionListener(formulaConfiguration);

		SimulationConfigurationPanel simConfig = new SimulationConfigurationPanel(control);
		sidebar.add(simConfig);
	}

}
