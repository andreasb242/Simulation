package ch.zhaw.simulation.window.flow;

import javax.swing.JMenu;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.gui.FlowEditorView;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.gui.control.FlowToolbar;
import ch.zhaw.simulation.menu.flow.FlowMenubar;
import ch.zhaw.simulation.window.SimulationWindow;

public class FlowWindow extends SimulationWindow<FlowMenubar, FlowToolbar, FlowEditorView> {
	private static final long serialVersionUID = 1L;

	public FlowWindow() {
	}
	
	public void init(FlowEditorControl control, ClipboardInterface clipboard) {
		FlowMenubar menubar = new FlowMenubar(control.getSysintegration(), um, clipboard);
		menubar.initMenusToolbar(new JMenu(), true);

		FlowToolbar tb = new FlowToolbar(control.getSysintegration(), true);
		tb.initToolbar();

		FlowEditorView view = new FlowEditorView(control);

		init(menubar, tb, view);
	}

}
