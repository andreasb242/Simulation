package ch.zhaw.simulation.sysintegration.bookmarks;

public class Bookmark {
	private String name;
	private String path;
	// Add separator after this element
	private boolean separator;

	public Bookmark(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public boolean isSeparator() {
		return separator;
	}

	public void setSeparator(boolean separator) {
		this.separator = separator;
	}
}
