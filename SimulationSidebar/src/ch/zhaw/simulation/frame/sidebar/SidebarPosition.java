package ch.zhaw.simulation.frame.sidebar;

/**
 * Interface for Sidebar contents
 * 
 * @author Andreas Butti
 */
public interface SidebarPosition {

	/**
	 * @return The prefered position, the smaller the higher on the sidebar, 0
	 *         is the topmost panel
	 */
	public int getSidebarPosition();
}
