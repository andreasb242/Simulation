package ch.zhaw.simulation.app;

import javax.swing.JMenu;

import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.plugin.SimulationManager;

/**
 * The Interface for the application base implementation
 * 
 * @author Andreas Butti
 */
public interface SimulationApplication extends MenuActionListener {

	/**
	 * Creates a new Simulation document
	 * 
	 * @param flowSimulation
	 *            The type
	 */
	public void newFile(SimulationType flowSimulation);

	/**
	 * Saves the simulation document
	 * 
	 * @return true if successfully
	 */
	public boolean save();

	public void open(String path);

	public boolean saveAs();

	public void open();

	public JMenu getRecentMenu();

	public void setLookAndFeel(String lookAndFeel);

	public void updateTitle();

	SimulationManager getManager();
}
