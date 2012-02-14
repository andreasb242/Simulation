package ch.zhaw.simulation.startup;


import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.*;

import ch.zhaw.simulation.htmleditor.InternImageView;

import java.awt.*;
import java.net.URL;
import java.lang.reflect.Field;

public class CustomTag {
	public static String htmlText = "<html>\n" + "<body>\n" + "<p>\n" + "Text before button\n" + "<button>Text for &lt;button&gt; tag</button>\n"
			+ "Text after button\n" + "</p><img id=\"$123\" src=\"file:///home/andreas/icons/plus.png\">\n" + "</body>\n" + "</html>";
	JEditorPane edit = new JEditorPane();

	public CustomTag() {
		JFrame frame = new JFrame("Custom tag in HTMLEditorKit");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JScrollPane(edit));

		edit.setEditorKit(new MyHTMLEditorKit());
		edit.setText(htmlText);

		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		new CustomTag();
	}

}

class MyHTMLEditorKit extends HTMLEditorKit {

	public MyHTMLEditorKit() {
		super();
	}

	public Document createDefaultDocument() {
		StyleSheet styles = getStyleSheet();
		StyleSheet ss = new StyleSheet();

		ss.addStyleSheet(styles);

		MyHTMLDocument doc = new MyHTMLDocument(ss);
		doc.setParser(getParser());
		doc.setAsynchronousLoadPriority(4);
		doc.setTokenThreshold(100);
		return doc;
	}

	public ViewFactory getViewFactory() {
		return new MyHTMLFactory();
	}

	Parser defaultParser;

	protected Parser getParser() {
		if (defaultParser == null) {
			defaultParser = new MyParserDelegator();
		}
		return defaultParser;
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

			if (kind instanceof HTML.UnknownTag && element.getName().equals("button")) {

				return new ComponentView(element) {
					protected Component createComponent() {
						JButton button = new JButton("Button : text unknown");

						try {
							int start = getElement().getStartOffset();
							int end = getElement().getEndOffset();
							String text = getElement().getDocument().getText(start, end - start);
							button.setText(text);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}

						return button;
					}
				};

			}
			return super.create(element);
		}
	}
}

class MyHTMLDocument extends HTMLDocument {
	private static final long serialVersionUID = 1L;

	public MyHTMLDocument(StyleSheet styles) {
		super(styles);
	}

	public HTMLEditorKit.ParserCallback getReader(int pos) {
		Object desc = getProperty(Document.StreamDescriptionProperty);
		if (desc instanceof URL) {
			setBase((URL) desc);
		}
		return new MyHTMLReader(pos);
	}

	class MyHTMLReader extends HTMLDocument.HTMLReader {
		public MyHTMLReader(int offset) {
			super(offset);
		}

		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			if (t.toString().equals("button")) {
				registerTag(t, new BlockAction());
			}
			super.handleStartTag(t, a, pos);
		}
	}
}

class MyParserDelegator extends ParserDelegator {
	private static final long serialVersionUID = 1L;

	public MyParserDelegator() {
		try {
			Field f = javax.swing.text.html.parser.ParserDelegator.class.getDeclaredField("dtd");
			f.setAccessible(true);
			DTD dtd = (DTD) f.get(null);
			javax.swing.text.html.parser.Element div = dtd.getElement("div");
			dtd.defineElement("button", div.getType(), true, true, div.getContent(), null, null, div.getAttributes());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
