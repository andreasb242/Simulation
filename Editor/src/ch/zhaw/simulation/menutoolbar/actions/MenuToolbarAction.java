package ch.zhaw.simulation.menutoolbar.actions;

/**
 * A Menu action was performed
 * 
 * @author Andreas Butti
 */
public class MenuToolbarAction {

	/**
	 * The type of the action
	 */
	private MenuToolbarActionType type;

	/**
	 * Additional provided data
	 */
	private Object data;

	public MenuToolbarAction(MenuToolbarActionType type) {
		this.type = type;
	}

	public MenuToolbarAction(MenuToolbarActionType type, Object data) {
		this(type);
		this.data = data;
	}

	/**
	 * Returs optional data, if any
	 * 
	 * @return Data or <code>null</code>
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @return The type of the action
	 */
	public MenuToolbarActionType getType() {
		return type;
	}
}
