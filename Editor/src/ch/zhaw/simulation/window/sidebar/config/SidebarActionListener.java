package ch.zhaw.simulation.window.sidebar.config;

public interface SidebarActionListener {
	public enum SidebarAction {
		SHOW_FORMULA_EDITOR,
		FIRE_SIMULATION_OBJECT_CHANGED
	}

	public void sidebarActionPerformed(SidebarAction action, Object data);
}
