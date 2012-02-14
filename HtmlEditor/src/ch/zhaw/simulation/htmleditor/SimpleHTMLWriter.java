package ch.zhaw.simulation.htmleditor;

import javax.swing.text.Document;
import javax.swing.text.Element;

public class SimpleHTMLWriter {
	private StringBuilder html = new StringBuilder();

	private void write(Document doc) {
		for(Element e :doc.getRootElements()) {
			writeElement(e);
		}
	}
	
	private void writeElement(Element elem) {
		for(int i = 0; i < elem.getElementCount(); i++) {
			Element e = elem.getElement(i);
			
			
		}
	}

	public String get(Document document) {
		write(document);
		return html.toString();
	}
}
