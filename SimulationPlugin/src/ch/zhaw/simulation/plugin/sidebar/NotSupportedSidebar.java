package ch.zhaw.simulation.plugin.sidebar;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.SimulationType;

public class NotSupportedSidebar extends JXTaskPane implements SidebarPosition {
	private static final long serialVersionUID = 1L;

	public NotSupportedSidebar(SimulationType t, String pluginName) {
		JXLabel lb = new JXLabel("Der Simulationstype «" + t + "» wird nicht unterstützt von «" + pluginName + "»");
		lb.setLineWrap(true);
		add(lb);
	}

	@Override
	public int getSidebarPosition() {
		return 1000;
	}
}
