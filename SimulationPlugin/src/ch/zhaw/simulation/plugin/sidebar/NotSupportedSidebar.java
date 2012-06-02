package ch.zhaw.simulation.plugin.sidebar;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.SimulationType;

public class NotSupportedSidebar extends JXTaskPane implements SidebarPosition {
	private static final long serialVersionUID = 1L;

	public NotSupportedSidebar(SimulationType t, String pluginName) {
		JLabel lb = new JLabel("<html>Der Simulationstype " + "«" + t + "»<br>" + "wird nicht unterstützt von<br>" + "«" + pluginName + "»<html>");
		add(lb);
	}

	@Override
	public int getSidebarPosition() {
		return 1000;
	}
}
