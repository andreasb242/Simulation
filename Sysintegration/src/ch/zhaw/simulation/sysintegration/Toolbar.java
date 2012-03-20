package ch.zhaw.simulation.sysintegration;


import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.jdesktop.swingx.action.TargetableAction;

import ch.zhaw.simulation.icon.IconLoader;

import butti.javalibs.errorhandler.Errorhandler;

public interface Toolbar {
	public JComponent getComponent();

	public ToolbarButton add(ToolbarAction action);

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

		public Icon getToolbarIcon() {
			if (toolbarIcon == null) {
				toolbarIcon = IconLoader.getIcon(iconName, 24);
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

	public interface ToolbarButton {
		public void setEnabled(boolean enabled);

		public void setText(String text);
		
		public void setIcon(Icon icon);
		
		public JComponent getComponent();
	}

	public void add(JComponent component);

	public ToolbarButton add(Action action);

	public ToolbarButton addToogleAction(TargetableAction action);
}
