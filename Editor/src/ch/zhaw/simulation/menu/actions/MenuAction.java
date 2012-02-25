package ch.zhaw.simulation.menu.actions;

/**
 * A Menu action was performed
 * 
 * @author Andreas Butti
 */
public class MenuAction {

	/**
	 * The type of the action
	 */
	private MenuActionType type;

	/**
	 * Additional provided data
	 */
	private Object data;

	public MenuAction(MenuActionType type) {
		this.type = type;
	}

	public MenuAction(MenuActionType type, Object data) {
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
	public MenuActionType getType() {
		return type;
	}
}
