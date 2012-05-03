package ch.zhaw.simulation.status;

public interface StatusListener {

	/**
	 * Clear status text
	 */
	public void clearStatus();

	/**
	 * Sets the startus text
	 * 
	 * @param text
	 *            The text
	 */
	public void setStatusText(String text);

	/**
	 * Sets the status text and sets an additional info icon
	 * 
	 * @param text
	 *            The text
	 */
	public void setStatusTextInfo(String text);

	/**
	 * Sets the status text and sets an additional error icon
	 * 
	 * @param text
	 *            The text
	 */
	public void setStatusTextError(String text);

}
