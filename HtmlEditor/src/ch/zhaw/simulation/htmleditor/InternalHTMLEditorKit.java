package ch.zhaw.simulation.htmleditor;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;


public class InternalHTMLEditorKit extends HTMLEditorKit {
	private static final long serialVersionUID = 1L;

	public InternalHTMLEditorKit() {
		super();
	}

	public ViewFactory getViewFactory() {
		return new MyHTMLFactory();
	}

	class MyHTMLFactory extends HTMLFactory implements ViewFactory {
		public MyHTMLFactory() {
			super();
		}

		public View create(Element element) {
			HTML.Tag kind = (HTML.Tag) (element.getAttributes().getAttribute(javax.swing.text.StyleConstants.NameAttribute));

			if (kind == HTML.Tag.IMG) {
				return new InternImageView(element);
			}

			return super.create(element);
		}
	}
}