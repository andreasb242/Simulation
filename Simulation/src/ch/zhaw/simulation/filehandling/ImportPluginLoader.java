package ch.zhaw.simulation.filehandling;

import java.io.File;
import java.util.Vector;

import butti.javalibs.config.Config;
import butti.javalibs.config.Settings;
import butti.javalibs.config.SettingsPrefix;
import butti.javalibs.errorhandler.Errorhandler;
import butti.plugin.PluginDescription;
import butti.plugin.PluginManager;
import ch.zhaw.simulation.inexport.ImportPlugin;
import ch.zhaw.simulation.sysintegration.SimFileFilter;

/**
 * Loads and Configures import Plugsins for other fileformats
 * 
 * @author Andreas Butti
 */
public class ImportPluginLoader {
	private Settings settings;
	private PluginManager<ImportPlugin> importPlugins = new PluginManager<ImportPlugin>();
	private Vector<PluginDescription<ImportPlugin>> pluginDescriptions;
	private SimFileFilter simulationFileOpen;

	public ImportPluginLoader(Settings settings) {
		this.settings = settings;
	}

	private void loadPlugins() {
		if (this.pluginDescriptions != null) {
			return;
		}

		String importPluginFolder = Config.get("importPluginFolder");
		if (importPluginFolder != null) {
			importPlugins.loadPlugins(importPluginFolder);

			for (Exception e : importPlugins.getPluginLoadErrors()) {
				Errorhandler.showError(e, "Plugin laden fehlgeschlagen");
			}

			for (PluginDescription<ImportPlugin> pluginDescription : importPlugins.getPluginDescriptions()) {
				ImportPlugin plugin = pluginDescription.getPlugin();
				SettingsPrefix sp = new SettingsPrefix(settings, "importplugin." + pluginDescription.getName());
				plugin.init(sp);
			}

		} else {
			System.err.println("No importPluginFolder defined in config/config.properties");
		}

		this.pluginDescriptions = importPlugins.getPluginDescriptions();

		simulationFileOpen = new SimFileFilter() {
			@Override
			public boolean accept(File f) {
				for (PluginDescription<ImportPlugin> pluginDescription : pluginDescriptions) {
					ImportPlugin plugin = pluginDescription.getPlugin();
					String[] exts = plugin.getFileExtension();
					if (exts != null) {
						for (String ext : exts) {
							if (f.getName().endsWith(ext)) {
								return true;
							}
						}
					}
				}

				return (f.isDirectory() || f.getName().endsWith(".simz"));
			}

			@Override
			public String getDescription() {
				return "Simulationsdateien";
			}

			@Override
			public String getExtension() {
				return null;
			}
		};
	}

	public SimFileFilter getSimulationFileOpen() {
		loadPlugins();

		return simulationFileOpen;
	}

	public Vector<PluginDescription<ImportPlugin>> getPluginDescriptions() {
		loadPlugins();

		return pluginDescriptions;
	}
}
