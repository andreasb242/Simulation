package ch.zhaw.simulation.sysintegration.mac;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import ch.zhaw.simulation.sysintegration.SysMenuShortcuts;

public class SysMenuShortcutsMacOSX extends SysMenuShortcuts {

	@Override
	protected int getCommandModifier() {
		return KeyEvent.META_MASK;
	}

	public KeyStroke getEditDelete() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
	}
}
