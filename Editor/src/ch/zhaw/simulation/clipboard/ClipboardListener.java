package ch.zhaw.simulation.clipboard;

/**
 * Listener for clipboard actions
 * 
 * @author Andreas Butti
 */
public interface ClipboardListener {
	/**
	 * If paste is enabled (some "pastable" content in the clipboard)
	 * 
	 * @param enabled
	 *            true if enabled
	 */
	public void pasteEnabled(boolean enabled);

	/**
	 * If there is content to copy or cut selected
	 * 
	 * @param enabled
	 *            true if enabled
	 */
	public void cutCopyEnabled(boolean enabled);
}
