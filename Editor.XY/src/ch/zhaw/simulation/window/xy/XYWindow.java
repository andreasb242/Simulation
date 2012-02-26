package ch.zhaw.simulation.window.xy;

import javax.swing.JMenu;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.menu.xy.XYMenubar;
import ch.zhaw.simulation.toolbar.XYToolbar;
import ch.zhaw.simulation.window.SimulationWindow;

public class XYWindow extends SimulationWindow<XYMenubar, XYToolbar, XYEditorView> {
	private static final long serialVersionUID = 1L;

	public XYWindow(boolean mainWindow) {
		super(mainWindow);
	}

	public void init(XYEditorControl control, ClipboardInterface clipboard) {
		XYMenubar menubar = new XYMenubar(control.getSysintegration(), um, clipboard);
		menubar.initMenusToolbar(new JMenu(), true);
		XYEditorView view = new XYEditorView(control);

		XYToolbar tb = new XYToolbar(control.getSysintegration(), mainWindow);
		tb.initToolbar(view.getUndoHandler());

		init(menubar, tb, view);
	}

	@Override
	protected void initSidebar(FrameSidebar sidebar) {
		// TODO Auto-generated method stub
		
	}

}
