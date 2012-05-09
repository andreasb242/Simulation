package ch.zhaw.simulation.plugin;

/**
 * @author: bachi
 */
public interface ExecutionListener {
	enum FinishState {
		/**
		 * Succesfully run the simulation
		 */
		SUCCESSFULLY,

		/**
		 * Canceled by the user
		 */
		CANCELED,

		/**
		 * Something went wrong
		 */
		ERROR
	}

	/**
	 * Starts the execution with a Status message
	 */
	public void executionStarted(String message);

	/**
	 * Sets the state
	 * @param state 0 ... 100%
	 */
	public void setState(int state);
	
	/**
	 * Updates the "Lock" message
	 */
	public void setExecutionMessage(String message);

	/**
	 * The execution of the Simulation has finished
	 * 
	 * @param message
	 *            The message if state == ERROR
	 * @param state
	 *            The state
	 */
	public void executionFinished(String message, FinishState state);

}
