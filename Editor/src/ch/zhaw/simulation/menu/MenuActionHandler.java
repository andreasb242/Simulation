package ch.zhaw.simulation.menu;

import java.util.Vector;

import ch.zhaw.simulation.menu.actions.MenuAction;

public class MenuActionHandler {
	private Vector<MenuActionListener> listeners = new Vector<MenuActionListener>();

	public void addListener(MenuActionListener l) {
		listeners.add(l);
	}

	public void removeListener(MenuActionListener l) {
		listeners.remove(l);
	}

	public void fireMenuActionPerformed(MenuAction a) {
		for (MenuActionListener l : listeners) {
			l.menuActionPerformed(a);
		}
	}
}
