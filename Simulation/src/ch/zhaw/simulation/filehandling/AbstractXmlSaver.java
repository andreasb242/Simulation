package ch.zhaw.simulation.filehandling;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Baseclass for all XML Saver
 * 
 * @author Andreas Butti
 */
public class AbstractXmlSaver {
	protected Document document;

	public AbstractXmlSaver() {
	}

	protected Element initDocument(String rootName) throws ParserConfigurationException {
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		document.setXmlStandalone(true);

		Element root = document.createElement(rootName);
		document.appendChild(root);

		return root;
	}

	protected void saveOutDocument(OutputStream out) throws TransformerException {
		DOMSource ds = new DOMSource(document);
		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer trans = tf.newTransformer();

		trans.setOutputProperty("indent", "yes"); // Java XML Indent
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		trans.transform(ds, new StreamResult(out));
	}

}
