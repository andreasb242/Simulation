package ch.zhaw.simulation.window.xy;

import javax.swing.JMenu;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorView;
import ch.zhaw.simulation.menu.xy.XYMenubar;
import ch.zhaw.simulation.toolbar.XYToolbar;
import ch.zhaw.simulation.window.SimulationWindow;

public class XYWindow extends SimulationWindow<XYMenubar, XYToolbar, XYEditorView> {
	private static final long serialVersionUID = 1L;

	public XYWindow(XYEditorControl control, ClipboardInterface clipboard) {
		XYMenubar menubar = new XYMenubar(control.getSysintegration(), um, clipboard);
		menubar.initMenusToolbar(new JMenu(), true);
		
		XYToolbar tb = new XYToolbar(control.getSysintegration(), true);
		tb.initToolbar();
		
		XYEditorView view = new XYEditorView(control);
		
		init(menubar, tb, view);
	}

}
