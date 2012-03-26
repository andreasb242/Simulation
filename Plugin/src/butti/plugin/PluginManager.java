package butti.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import butti.plugin.definition.AbstractPlugin;

/**
 * Plugin Manager
 * 
 * @author Andreas Butti
 * 
 * @param <E>
 *            Interface das die Plugins implementieren müssen
 */
public class PluginManager<E extends AbstractPlugin> {
	/**
	 * Alle Fehler
	 */
	private Vector<Exception> pluginLoadErrors = new Vector<Exception>();

	/**
	 * Alle geladenen Plugins
	 */
	private Vector<PluginDescription<E>> pluginDescriptions = new Vector<PluginDescription<E>>();

	/**
	 * Lädt alle Plugins
	 * 
	 * @param folder
	 *            Wo nach .jar Files gesucht werden soll
	 */
	public void loadPlugins(String folder) {
		loadPlugins(new File(folder));
	}

	/**
	 * Lädt alle Plugins
	 * 
	 * @param folder
	 *            Wo nach .jar Files gesucht werden soll
	 */
	public void loadPlugins(File folder) {
		String[] files = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar") || name.endsWith(".xml");
			}

		});

		for (String f : files) {
			try {
				if (!loadPlugin(folder + "/" + f)) {
					throw new Exception("Plugin initialisation of Plugin \"" + folder + "/" + f + "\" failed!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				pluginLoadErrors.add(e);
			}
		}
	}

	/**
	 * Lädt ein Plugin
	 * 
	 * @param path
	 *            Der Pfad zum .jar File
	 * @return true wenn erfolgreich
	 */
	@SuppressWarnings("unchecked")
	// ggf. wird eine Exception geworfen, die abgefangen ist...
	private boolean loadPlugin(String path) throws Exception {
		ClassLoader cl;
		InputStream xmlInput;

		if (path.endsWith(".jar")) {
			File file = new File(path);
			URL jarfile = new URL("jar", "", "file:" + file.getAbsolutePath() + "!/");
			cl = URLClassLoader.newInstance(new URL[] { jarfile });

			xmlInput = cl.getResourceAsStream("plugin.xml");
		} else {
			xmlInput = new FileInputStream(path);
			cl = getClass().getClassLoader();
		}

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlInput);

		Node root = document.getChildNodes().item(0);

		NodeList nodes = root.getChildNodes();

		String clazz = "";

		PluginDescription<E> pluginDescription = new PluginDescription<E>();

		pluginDescription.setFilename(getFileName(path));

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if ("class".equals(n.getNodeName())) {
				clazz = n.getChildNodes().item(0).getNodeValue();
			} else if ("name".equals(n.getNodeName())) {
				pluginDescription.setName(n.getChildNodes().item(0).getNodeValue());
			} else if ("description".equals(n.getNodeName())) {
				pluginDescription.setDescription(n.getChildNodes().item(0).getNodeValue());
			} else if ("author".equals(n.getNodeName())) {
				pluginDescription.setAuthor(n.getChildNodes().item(0).getNodeValue());
			}
		}

		if (!pluginDescription.isValid() || clazz.equals("")) {
			return false;
		}

		Class<?> loadedClass = cl.loadClass(clazz);

		E obj;
		try {
			obj = (E) loadedClass.newInstance();
		} catch (Exception e) {
			throw new Exception("Failed to instance «" + clazz + "»", e);
		}

		pluginDescription.setPlugin(obj);
		pluginDescriptions.add(pluginDescription);

		return true;
	}

	public static String getFileName(String path) {
		String fileName = null;

		// Für Windows...
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}

		int pos = path.lastIndexOf('/');
		int pos2 = path.lastIndexOf(".");

		if (pos2 > -1) {
			fileName = path.substring(pos + 1, pos2);
		} else {
			fileName = path.substring(pos + 1);
		}

		return fileName;
	}

	/**
	 * Gibt alle Fehler die beim laden der Plugins aufgetreten sind zurück
	 */
	public Vector<Exception> getPluginLoadErrors() {
		return pluginLoadErrors;
	}

	/**
	 * Gibt alle geladenen Plugins zurück
	 */
	public Vector<PluginDescription<E>> getPluginDescriptions() {
		return pluginDescriptions;
	}

	/**
	 * Entfernt ein Plugin aus der Liste, nachdem es entladen wurde
	 * 
	 * @param p
	 *            Das Plugin das entladen werden soll
	 * @return true wenn erfolgreich
	 */
	public boolean unloadPlugin(PluginDescription<E> p) {
		if (p.getPlugin() != null) {
			p.getPlugin().unload();
		}

		return pluginDescriptions.remove(p);
	}
}
