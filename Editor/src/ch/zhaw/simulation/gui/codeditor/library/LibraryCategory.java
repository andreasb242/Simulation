package ch.zhaw.simulation.gui.codeditor.library;

import ch.zhaw.simulation.jep.CategoryType;

public class LibraryCategory extends LibraryNode<LibraryNode<?>> {

	private CategoryType cat;

	public LibraryCategory(CategoryType cat) {
		this.cat = cat;
	}

	public CategoryType getCategory() {
		return cat;
	}

	@Override
	public String toString() {
		return cat.toString();
	}

}
