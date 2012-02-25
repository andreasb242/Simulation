package ch.zhaw.simulation.window;

import javax.swing.JMenu;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.menu.xy.XYMenubar;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.toolbar.XYToolbar;

public class XYWindow extends SimulationWindow<XYMenubar, XYToolbar> {
	private static final long serialVersionUID = 1L;

	public XYWindow(Sysintegration sysintegration, ClipboardInterface clipboard) {
		XYMenubar menubar = new XYMenubar(sysintegration, um, clipboard);
		menubar.initMenusToolbar(new JMenu(), true);
		
		XYToolbar tb = new XYToolbar(sysintegration, true);
		tb.initToolbar();
		
		init(menubar, tb);
	}

}
