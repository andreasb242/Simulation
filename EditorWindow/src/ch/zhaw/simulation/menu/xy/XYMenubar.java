package ch.zhaw.simulation.menu.xy;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.undo.UndoHandler;

public class XYMenubar extends AbstractMenubar {

	public XYMenubar(Sysintegration sysintegration, UndoHandler um, ClipboardInterface clipboard) {
		super(sysintegration, um, clipboard);
	}

}
