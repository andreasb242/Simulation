package ch.zhaw.simulation.inexport.madonna.xmlformat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import butti.javalibs.util.FileUtil;
import butti.javalibs.util.StringUtil;
import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.inexport.madonna.FileImporter;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;

public class XmlImporter implements FileImporter {
	private Vector<XmlComponent> components = new Vector<XmlComponent>();
	private Vector<XmlValve> valve = new Vector<XmlValve>();
	private Vector<XmlArc> arcs = new Vector<XmlArc>();
	private Vector<XmlPipe> pipes = new Vector<XmlPipe>();
	private Vector<XmlMultilineComment> multilineComments = new Vector<XmlMultilineComment>();
	private Vector<XmlComment> comments = new Vector<XmlComment>();
	private HashMap<Integer, String> texts = new HashMap<Integer, String>();
	private HashMap<Integer, XmlCloud> clouds = new HashMap<Integer, XmlCloud>();
	private HashMap<Integer, AbstractNamedSimulationData> simElements = new HashMap<Integer, AbstractNamedSimulationData>();
	private Vector<XmlGlobal> globals = new Vector<XmlGlobal>();

	public XmlImporter() {
	}

	@Override
	public boolean load(SimulationFlowModel model) throws ImportException {
		model.clear();

		int gx = 0;
		int gy = 0;

		for (XmlComponent comp : components) {
			AbstractNamedSimulationData data;
			if (comp instanceof XmlContainer) {
				data = new SimulationContainerData(comp.getX(), comp.getY());
			} else if (comp instanceof XmlParameter) {
				data = new SimulationParameterData(comp.getX(), comp.getY());
			} else {
				throw new ImportException("MadonnaXmlImporter: Object type " + comp.getClass() + " not handled!");
			}

			data.setFormula(comp.getFormula());
			data.setName(texts.get(comp.getNameId()));

			model.addData(data);
			simElements.put(comp.getId(), data);

			gy = Math.max(gy, data.getY2());
		}

		for (XmlValve v : valve) {
			XmlPipe from = null;
			XmlPipe to = null;

			for (XmlPipe p : pipes) {
				if (v.getId() == p.getTo()) {
					from = p;
				} else if (v.getId() == p.getFrom()) {
					to = p;
				}
			}

			if (from == null || to == null) {
				System.err.println("MadonnaXmlImporter: Valve (id = " + v.getId() + ") not complete");
				continue;
			}

			AbstractSimulationData source = simElements.get(from.getFrom());
			AbstractSimulationData target = simElements.get(to.getTo());

			if (source == null) {
				XmlCloud c = clouds.get(from.getFrom());
				if (c != null) {
					source = new InfiniteData(c.getX(), c.getY());
					model.addData(source);

					gy = Math.max(gy, source.getY2());
				}
			}

			if (target == null) {
				XmlCloud c = clouds.get(to.getTo());

				if (c != null) {
					target = new InfiniteData(c.getX(), c.getY());
					model.addData(target);

					gy = Math.max(gy, target.getY2());
				}
			}

			if (source == null) {
				System.err.println("MadonnaXmlImporter: Valve (id = " + v.getId() + " / " + from.getFrom() + ") not complete2");
				continue;
			}
			if (target == null) {
				System.err.println("MadonnaXmlImporter: Valve (id = " + v.getId() + " / " + to.getTo() + ") not complete3");
				continue;
			}

			FlowConnectorData data = new FlowConnectorData(source, target);
			data.getValve().setFormula(v.getFormula());
			data.getValve().setName(texts.get(v.getNameId()));

			model.addConnector(data);
			simElements.put(v.getId(), data.getValve());
		}

		for (XmlArc a : arcs) {
			AbstractNamedSimulationData source = simElements.get(a.getFrom());
			if (source == null) {
				System.err.println("MadonnaXmlImporter: source for arc not found (id=" + a.getFrom() + ")");
				continue;
			}
			AbstractNamedSimulationData target = simElements.get(a.getTo());
			if (target == null) {
				System.err.println("MadonnaXmlImporter: target for arc not found (id=" + a.getTo() + ")");
				continue;
			}
			ParameterConnectorData data = new ParameterConnectorData(source, target);
			model.addConnector(data);
		}

		for (XmlMultilineComment c : multilineComments) {
			TextData text = new TextData(c.getX(), c.getY());
			text.setWidth(c.getWidth());
			text.setHeight(c.getHeight());

			String txt = StringUtil.replace(StringUtil.escapeHTML(c.getText()), "\n", "<br>");
			text.setText("<html>" + txt + "</html>");

			model.addData(text);
		}

		for (XmlComment c : comments) {
			TextData text = new TextData(c.getX(), c.getY());

			String txt = texts.get(c.getNameId());
			txt = StringUtil.replace(StringUtil.escapeHTML(txt), "\n", "<br>");
			text.setText("<html>" + txt + "</html>");
			text.setHeight(25);

			model.addData(text);
		}

		gy += 30;
		gx = 30;

		int row = 0;

		for (XmlGlobal g : globals) {
			SimulationGlobalData data = new SimulationGlobalData(gx, gy);
			data.setName(g.getName());
			data.setFormula(g.getFormula());
			model.addData(data);

			gx += 60;
			row++;

			if (row >= 10) {
				row = 0;
				gx = 30;
				gy += 60;
			}

		}

		return true;
	}

	@Override
	public void read(PushbackInputStream in) throws IOException, ImportException {
		String xmlContents = FileUtil.fileGetContents(in);

		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlContents.getBytes()));
		} catch (SAXException e) {
			System.err.println("Madonna file is not a valid XML File, try to fix it and parse again...");
			if (e.getMessage().contains("The element type \"MultiTextInfo\" must be terminated by the matching end-tag")) {
				document = fixMultiTextInfoBug(xmlContents);
			}
			if (document == null) {
				throw new ImportException(e);
			}
		} catch (ParserConfigurationException e) {
			throw new ImportException(e);
		}

		// Mögliche Kommentare überlesen
		NodeList rootNodes = document.getChildNodes();
		Node root = null;
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node n = rootNodes.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				root = n;
				break;
			}
		}

		if (root == null) {
			throw new ImportException("Root node not found!");
		}

		if (!"Document".equals(root.getNodeName())) {
			throw new ImportException("Root node name != Document!");
		}

		Node flowchart = null;
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("Version".equals(n.getNodeName())) {
					String version = XmlHelper.getContents(n);
					System.out.println("Imported Madonna Version: " + version);
				} else if ("Flowchart".equals(n.getNodeName())) {
					flowchart = n;
				}
			}
		}

		if (flowchart == null) {
			throw new ImportException("Model does not contain a Flowchart");
		}

		Node modelInfo = null;
		nodes = flowchart.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("ModelInfo".equals(n.getNodeName())) {
					modelInfo = n;
				}
			}

		}

		if (modelInfo == null) {
			throw new ImportException("Model does not contain <ModelInfo>");
		}

		Node entries = null;
		String globals = null;
		nodes = modelInfo.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("Entries".equals(n.getNodeName())) {
					entries = n;
				} else if ("Globals".equals(n.getNodeName())) {
					globals = XmlHelper.getContents(n);
				}
			}
		}

		if (entries == null) {
			throw new ImportException("Model does not contain <Entries>");
		}

		nodes = entries.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("ReservoirInfo".equals(n.getNodeName())) {
					components.add(new XmlContainer(n));
				} else if ("FlowInfo".equals(n.getNodeName())) {
					valve.add(new XmlValve(n));
				} else if ("ConverterInfo".equals(n.getNodeName())) {
					components.add(new XmlParameter(n));
				} else if ("CloudInfo".equals(n.getNodeName())) {
					XmlCloud c = new XmlCloud(n);
					clouds.put(c.getId(), c);
				} else if ("PipeInfo".equals(n.getNodeName())) {
					pipes.add(new XmlPipe(n));
				} else if ("ArcInfo".equals(n.getNodeName())) {
					arcs.add(new XmlArc(n));
				} else if ("MultiTextInfo".equals(n.getNodeName())) {
					multilineComments.add(new XmlMultilineComment(n));
				} else if ("ModelInfo".equals(n.getNodeName())) {
					comments.add(new XmlComment(n));
				} else if ("TextInfo".equals(n.getNodeName())) {
					parseTextInfo(n);
				} else {
					System.err.println("Unhandled entry: " + n.getNodeName());
				}
			}
		}

		if (globals != null) {
			parseGlobals(globals);
		}
	}

	/**
	 * Workaround, because BM closes <MultiTextInfo> with </TextInfo>
	 */
	private Document fixMultiTextInfoBug(String xml) throws ImportException, IOException {
		StringBuffer xml2 = new StringBuffer();

		String[] parts = xml.split("<MultiTextInfo>");

		xml2.append(parts[0]);

		for (int i = 1; i < parts.length; i++) {
			xml2.append("<MultiTextInfo>");
			String p = parts[i];
			int pos = p.indexOf("</TextInfo>");
			if (pos > 0) {
				xml2.append(p.substring(0, pos));
				xml2.append("</MultiTextInfo>");
				xml2.append(p.substring(pos + 11));
			} else {
				throw new ImportException("BM Workaround didn't work... Sorry...");
			}
		}

		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml2.toString().getBytes()));
		} catch (SAXException e) {
			throw new ImportException(e);
		} catch (ParserConfigurationException e) {
			throw new ImportException(e);
		}
	}

	private void parseGlobals(String globals) {
		for (String g : globals.split("\n")) {
			g = g.trim();
			if (g.startsWith(";") || g.isEmpty()) {
				continue;
			}

			int pos = g.indexOf("=");
			if (pos < 0) {
				continue;
			}

			String name = g.substring(0, pos);
			String formula = g.substring(pos + 1);

			this.globals.add(new XmlGlobal(name, formula));
		}
	}

	private int getIdNodeContents(Node node) {
		NodeList nodes = node.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("Id".equals(n.getNodeName())) {
					String s = XmlHelper.getContents(n);
					return Integer.parseInt(s);
				}
			}
		}

		return -1;
	}

	private void parseTextInfo(Node node) {
		NodeList nodes = node.getChildNodes();

		String string = null;
		int id = -1;

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if ("String".equals(n.getNodeName())) {
					string = XmlHelper.getContents(n);
				} else if ("PageEntry".equals(n.getNodeName())) {
					id = getIdNodeContents(n);
				}
			}
		}

		if (string != null && id != -1) {
			texts.put(id, string);
		}
	}

}
