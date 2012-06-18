package ch.zhaw.simulation.gui.codeditor.library;

import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.math.Parser;

public class LibraryRootNode extends LibraryNode<LibraryCategory> {

	public LibraryRootNode(Parser parser) {
		// TODO Operator

		LibraryCategory consts = getCatSubnode(CategoryType.CONSTS);

		for (Constant c : parser.getConst()) {
			consts.addChild(new LibraryConstNode(c));
		}

		for (Function f : parser.getFunctionlist()) {
			CategoryType cat = f.getCategory();
			LibraryCategory lc = getCatSubnode(cat);
			LibraryFunctionNode n = new LibraryFunctionNode(f);
			lc.addChild(n);
		}

		order();
		for (LibraryCategory c : this) {
			c.order();
		}
	}

	private LibraryCategory getCatSubnode(CategoryType cat) {
		for (LibraryCategory c : this) {
			if (c.getCategory() == cat) {
				return c;
			}
		}

		LibraryCategory n = new LibraryCategory(cat);
		addChild(n);
		return n;
	}

	@Override
	public String toString() {
		return "root";
	}

}
