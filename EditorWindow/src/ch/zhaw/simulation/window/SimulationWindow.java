package ch.zhaw.simulation.window;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JFrame;

import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.toolbar.AbstractToolbar;
import ch.zhaw.simulation.undo.UndoHandler;

public class SimulationWindow<M extends AbstractMenubar, T extends AbstractToolbar> extends JFrame implements MenuActionListener {
	private static final long serialVersionUID = 1L;

	private Vector<MenuActionListener> listeners = new Vector<MenuActionListener>();

	private FrameSidebar sidebar = new FrameSidebar();

	protected UndoHandler um = new UndoHandler();

	protected M menubar;
	protected T toolbar;

	public SimulationWindow() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(IconSVG.getIcon("simulation", 128).getImage());

		add(BorderLayout.EAST, sidebar);

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	protected void init(M menubar, T toolbar) {
		this.menubar = menubar;
		this.toolbar = toolbar;
		setJMenuBar(menubar.getMenubar());

		this.menubar.addListener(this);
		this.toolbar.addListener(this);

		add(BorderLayout.NORTH, toolbar.getToolbar());
	}

	@Override
	public void menuActionPerformed(MenuToolbarAction action) {
		fireMenuActionPerformed(action);
	}

	public void addListener(MenuActionListener l) {
		listeners.add(l);
	}

	public void removeListener(MenuActionListener l) {
		listeners.remove(l);
	}

	public void fireMenuActionPerformed(MenuToolbarAction a) {
		for (MenuActionListener l : listeners) {
			l.menuActionPerformed(a);
		}
	}
}
