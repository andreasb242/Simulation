package ch.zhaw.simulation.app;

import javax.swing.JFrame;
import javax.swing.JMenu;

import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.menu.MenuActionListener;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
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

	public JMenu getRecentMenu();

	public void updateTitle();

	public SimulationManager getManager();

	public JFrame getMainFrame();

	FunctionHelp getFunctionHelp();

	public void openFlowEditor(SimulationFlowModel model);
}
