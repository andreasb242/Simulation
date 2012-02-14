package butti.plugin.definition;

/**
 * Plugin Schnittstelle
 * 
 * @author Andreas Butti
 * 
 */
public interface AbstractPlugin {
	/**
	 * Wird aufgerufen wenn das Plugin geladen wird
	 * 
	 * @return true wenn OK, false wenn es nicht geladen werden konnte
	 * @throws Exception
	 *             Wenn ein Fehler auftritt, return = false.
	 */
	public boolean load() throws Exception;

	/**
	 * Wird aufgerufen bevor das Plugin entfernt wird
	 */
	public void unload();
}
