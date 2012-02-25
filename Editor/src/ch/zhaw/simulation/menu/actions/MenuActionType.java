package ch.zhaw.simulation.menu.actions;

/**
 * The Menu Actions
 * 
 * @author Andreas Butti
 */
public enum MenuActionType {
	/**
	 * Creates a new Simulation Model
	 */
	NEW_FILE,

	/**
	 * Open a file, supply an additional string to open specific file
	 */
	OPEN_FILE,

	/**
	 * Save the current model (with all submodels)
	 */
	SAVE,

	/**
	 * Save the current model and select file to save
	 */
	SAVE_AS,

	/**
	 * Saves the current diagram as image
	 */
	SNAPSHOT,

	/**
	 * Quits the application
	 */
	EXIT,

	/**
	 * Undues the last action
	 */
	UNDO,

	/**
	 * Redoes the last undone action
	 */
	REDO,

	/**
	 * Cuts the current selection
	 */
	CUT,
	/**
	 * Copies the current selection
	 */
	COPY,

	/**
	 * Paste from clipboard into the model
	 */
	PASTE,

	/**
	 * Selects all elements
	 */
	SELECT_ALL,

	/**
	 * Deletes the current selected objects
	 */
	DELETE_SELECTION,

	/**
	 * Shows or hide the sidebar, provide a boolean as parameter
	 */
	SHOW_SIDEBAR,

	/**
	 * Starts the simulation
	 */
	START_SIMULATION,

	/**
	 * Shows an overview window
	 */
	FORMULA_OVERVIEW,

	/**
	 * Shows a math console
	 */
	SHOW_MATH_CONSOLE,

	/**
	 * Shows the settings Dialog
	 */
	SETTINGS,

	/**
	 * Shows the about Dialog
	 */
	ABOUT,

	/**
	 * Shows the help
	 */
	HELP,

	/**
	 * The Look & Feel was changed, the new name is provided as string
	 */
	LOOK_AND_FEEL_CHANGED
}
