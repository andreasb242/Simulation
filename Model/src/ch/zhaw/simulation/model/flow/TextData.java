package ch.zhaw.simulation.model.flow;

public class TextData extends NamedSimulationObject {
	private int width = 80;
	private int height = 120;
	private String text = "";

	public TextData(int x, int y) {
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

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public String getDefaultName() {
		return "txt";
	}
}
