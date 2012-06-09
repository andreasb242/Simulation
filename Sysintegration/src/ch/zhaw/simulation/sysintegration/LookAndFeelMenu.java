package ch.zhaw.simulation.sysintegration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import butti.javalibs.listener.ListenerList;
import ch.zhaw.simulation.icon.IconLoader;

/**
 * The Look & Fell menu to select the current Look & Feel
 * 
 * @author Andreas Butti
 */
public class LookAndFeelMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	private ListenerList listener = new ListenerList();

	public LookAndFeelMenu() {
		super("Look & Feel");
		setIcon(IconLoader.getIcon("style"));

		addMenuItem("System LAF", "system", "sys");
		addMenuItem("Nimbus", "Java-Logo", "nimbus");
		addMenuItem("Java LAF", "Java-Logo", "java");
	}

	private void addMenuItem(String name, String icon, final String lookAndFeelName) {
		JMenuItem it = new JMenuItem(name, IconLoader.getIcon(icon));
		it.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(new ActionEvent(this, 0, lookAndFeelName));
			}
		});

		add(it);
	}

	public void addListener(ActionListener listener) {
		this.listener.addListener(listener);
	}

	public boolean removeListener(ActionListener listener) {
		return this.listener.removeListener(listener);
	}

}
