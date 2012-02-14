package ch.zhaw.simulation.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;


import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;

public class RecentMenu {
	private static final String RECENT_TAG = "opensave.recent";
	private static final int COUNT = 10;

	private JMenu menu;

	private Vector<RecentItem> data = new Vector<RecentItem>();

	private Settings settings;

	private JMenuItem itClear = new JMenuItem("Verlauf löschen");
	private SimulationControl control;

	public RecentMenu(SimulationControl control) {
		this.settings = control.getSettings();
		this.control = control;

		menu = new JMenu("Zuletzt geöffnete");
		menu.setIcon(IconSVG.getIcon("open-recent"));

		itClear.setIcon(IconSVG.getIcon("editclear"));

		itClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				data.clear();
				settings.setSetting(RECENT_TAG, "");

				updateMenu();
			}
		});
		loadSettings();
		updateMenu();
	}

	public String getNewstEntry() {
		if(data.size() == 0) {
			return null;
		}
		
		return data.firstElement().path;
	}
	
	private void updateMenu() {
		menu.setEnabled(data.size() != 0);

		menu.removeAll();

		for (RecentItem d : data) {
			menu.add(d.getMenuitem());
		}

		menu.addSeparator();
		menu.add(itClear);
	}

	public void addFile(String path) {
		RecentItem ri = new RecentItem(path);

		// ggf. alten Eintrag entfernen
		data.remove(ri);
		data.add(0, ri);

		if (data.size() > COUNT) {
			data.setSize(COUNT);
		}

		updateMenu();
		saveSettings();
	}

	private void saveSettings() {
		StringBuffer buf = new StringBuffer();
		for (RecentItem ri : data) {
			buf.append(';');
			buf.append(ri.getPath());
		}

		String str = buf.toString();

		if (buf.length() != 0) {
			str = buf.substring(1);
		}

		settings.setSetting(RECENT_TAG, str);
	}

	private void loadSettings() {
		for (String s : settings.getSetting(RECENT_TAG, "").split(";")) {
			if (s.isEmpty()) {
				continue;
			}
			data.add(new RecentItem(s));
		}
	}

	public JMenu getMenu() {
		return menu;
	}

	class RecentItem {
		private String path;

		private JMenuItem item;

		public RecentItem(String path) {
			this.path = path;

			File f = new File(path);

			item = new JMenuItem(f.getName());
			if (!f.exists()) {
				item.setIcon(IconSVG.getIcon("close"));
				item.setEnabled(false);
			}

			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						control.open(RecentItem.this.path);
					} catch (Exception ex) {
						Errorhandler.showError(ex);
					}
				}
			});
		}

		public String getPath() {
			return path;
		}

		public JMenuItem getMenuitem() {
			return item;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof String) {
				return obj.equals(path);
			}

			if (obj instanceof RecentItem) {
				return ((RecentItem) obj).path.equals(path);
			}

			return super.equals(obj);
		}
	}
}
