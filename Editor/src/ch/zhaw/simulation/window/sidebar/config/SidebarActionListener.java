package ch.zhaw.simulation.window.sidebar.config;

public interface SidebarActionListener {
	public enum SidebarAction {
		SHOW_FORMULA_EDITOR
	}

	public void sidebarActionPerformed(SidebarAction action, Object data);
}
