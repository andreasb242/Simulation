package ch.zhaw.simulation.sysintegration.bookmarks;

import javax.swing.Icon;

public class Bookmark {
	private String name;
	private String path;
	private Icon icon;

	// Add separator after this element
	private boolean separator;

	public Bookmark(String name, String path, Icon icon) {
		this.name = name;
		this.path = path;
		this.icon = icon;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}
	
	public Icon getIcon() {
		return icon;
	}

	public boolean isSeparator() {
		return separator;
	}

	public void setSeparator(boolean separator) {
		this.separator = separator;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
