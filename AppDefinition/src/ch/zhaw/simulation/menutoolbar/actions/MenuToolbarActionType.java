package ch.zhaw.simulation.menutoolbar.actions;

/**
 * The Menu Actions
 * 
 * @author Andreas Butti
 */
public enum MenuToolbarActionType {
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
	 * Close the current window (without quit the application)
	 */
	CLOSE,
	
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

	// TODO Copy & Paste not working on OS X
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
	 * Loads last results
	 */
	LOAD_RESULTS,

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
	 * Shows all supported operations
	 */
	MATH_OVERVIEW,
	
	/**
	 * The Look & Feel was changed, the new name is provided as string
	 */
	LOOK_AND_FEEL_CHANGED,
	
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	// Editor specific enums
	
	EDITOR_ADD_GLOBAL,
	EDITOR_ADD_TEXT,

	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	// Flow specific enums
	
	FLOW_ADD_CONTAINER,
	FLOW_ADD_PARAMETER,
	FLOW_ADD_FLOW,
	FLOW_ADD_CONNECTOR,
	FLOW_ADD_DENSITY,

	FLOW_SHOW_GRID,

	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	// XY specific enums
	
	XY_ADD_MESO,
	XY_MESO_POPUP,
	XY_MODEL_SIZE,

	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	// Layout specific enums

	LAYOUT_CENTER_VERTICAL,
	LAYOUT_CENTER_HORIZONTAL,
	LAYOUT_RIGHT,
	LAYOUT_LEFT,
	LAYOUT_TOP,
	LAYOUT_BOTTOM
	
}
