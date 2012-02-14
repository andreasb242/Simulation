package sun.net.www.protocol.man;

import java.util.Vector;
import java.util.Map.Entry;

import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.help.model.FunctionInformation;


public class DynamicHelp {
	private static DynamicHelp instance = new DynamicHelp();
	private FunctionHelp help = new FunctionHelp();

	private DynamicHelp() {
	}

	private void get(String path, StringBuilder contents) {
		if ("/functionlist.html".equals(path)) {
			getFunctionlist(contents);
		} else {
			contents.append("<h1>ERROR: Dynamic Page not found.</h1>");
		}
	}

	private void getFunctionlist(StringBuilder contents) {
		contents.append("<h1>Funktionsliste</h1>\n");

		contents.append("<h1>Inhalt</h1>\n");

		int i = 0;

		for (Entry<String, Vector<FunctionInformation>> e : help.getData().entrySet()) {
			contents.append("<a href=\"#t" + i + "\">");
			contents.append(e.getKey());
			contents.append("</a><br>");
			i++;
		}

		contents.append("<br />\n");

		i = 0;
		for (Entry<String, Vector<FunctionInformation>> e : help.getData().entrySet()) {
			addHeader(e.getKey(), e.getValue(), i, contents);
			i++;
		}
	}

	private void addHeader(String name, Vector<FunctionInformation> list, int id, StringBuilder contents) {
		contents.append("<h2>");
		contents.append(name);
		contents.append("</h2>\n");
		contents.append("<a name=\"t" + id + "\"></a>");

		for (FunctionInformation info : list) {
			contents.append("<b>" + info.getName() + "</b>(" + info.getParameter() + ")<br>");
			contents.append("<i>" + info.getDescription() + "</i><br /><br />\n");
		}
	}

	public static void request(String path, StringBuilder contents) {
		instance.get(path, contents);
	}
}
