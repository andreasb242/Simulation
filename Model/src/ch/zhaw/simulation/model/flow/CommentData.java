package ch.zhaw.simulation.model.flow;

/**
 * This is a comment
 * 
 * A comment dont have a real name, and don't have a formula, but it was easier
 * to extend NamedSimulationObject instead of SimulationObject because of
 * inheritance within the view
 * 
 * @author Andreas Butti
 */
public class CommentData extends NamedSimulationObject {
	/**
	 * The width of this comment (initialized to a default value)
	 */
	private int width = 80;

	/**
	 * The height of this comment (initialized to a default value)
	 */
	private int height = 120;

	/**
	 * The HTML Text of this comment
	 */
	private String text = "";

	public CommentData(int x, int y) {
		super(x, y);
		setName("Text");
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (width < 20) {
			return;
		}
		this.width = width;
	}

	public void setHeight(int height) {
		if (height < 20) {
			return;
		}
		this.height = height;
	}

	/**
	 * Sets the text of this comment
	 * 
	 * @param text
	 *            HTML Text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Return the Text of this comment
	 * 
	 * @return HTML Text
	 */
	public String getText() {
		return text;
	}

	@Override
	public String getDefaultName() {
		return "txt";
	}
}
