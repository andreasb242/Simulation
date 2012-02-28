package ch.zhaw.simulation.window.flow;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.flow.gui.FlowEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.menu.flow.FlowMenubar;
import ch.zhaw.simulation.toolbar.xy.FlowToolbar;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.flow.sidebar.SimulationConfigurationPanel;

public class FlowWindow extends SimulationWindow<FlowMenubar, FlowToolbar, FlowEditorView> {
	private static final long serialVersionUID = 1L;

	public FlowWindow(boolean mainWindow) {
		super(mainWindow);
	}

	public void init(FlowEditorControl control) {
		FlowEditorView view = new FlowEditorView(control);
		control.setView(view);

		FlowMenubar menubar = new FlowMenubar(control.getSysintegration(), um, control.getClipboard());
		menubar.initMenusToolbar(control.getRecentMenu(), mainWindow);

		FlowToolbar tb = new FlowToolbar(control.getSysintegration(), mainWindow);
		tb.initToolbar(view.getUndoHandler());

		init(menubar, tb, view);
	}

	@Override
	protected void initSidebar(FrameSidebar sidebar) {
		super.initSidebar(sidebar);

		SimulationConfigurationPanel simConfig = new SimulationConfigurationPanel(view.getControl());
		sidebar.add(simConfig);
	}

}
