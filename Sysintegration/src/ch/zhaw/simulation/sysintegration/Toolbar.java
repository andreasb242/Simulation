package ch.zhaw.simulation.sysintegration;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import org.jdesktop.swingx.action.TargetableAction;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.icon.IconLoader;

public interface Toolbar {
	public JComponent getComponent();

	public JButton add(ToolbarAction action);

	public void addSeparator();

	public static abstract class ToolbarAction {
		private String name;
		private Icon toolbarIcon;
		private JMenuItem menuitem;
		private String iconName;

		public ToolbarAction(String name, String iconName) {
			this.name = name;
			this.iconName = iconName;
		}

		public ToolbarAction(String name, Icon toolbarIcon) {
			this.name = name;
			this.toolbarIcon = toolbarIcon;
		}

		public Icon getToolbarIcon(int defaultIconSize) {
			if (toolbarIcon == null) {
				toolbarIcon = IconLoader.getIcon(iconName, defaultIconSize);
			}
			return toolbarIcon;
		}

		public String getName() {
			return name;
		}

		public void run(ActionEvent e) {
			try {
				actionPerformed(e);
			} catch (Exception ex) {
				Errorhandler.showError(ex);
			}
		}

		protected abstract void actionPerformed(ActionEvent e);

		public JMenuItem getMenuItem() {
			if (menuitem == null) {
				menuitem = new JMenuItem(name);
				menuitem.setIcon(IconLoader.getIcon(iconName));
			}
			return menuitem;
		}
	}

	public void add(JComponent component);

	public JButton add(Action action);

	public JToggleButton addToogleAction(TargetableAction action);

	int getDefaultIconSize();
}
