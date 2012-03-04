package ch.zhaw.simulation.plugin;

import java.util.Vector;

import butti.javalibs.config.Config;
import butti.javalibs.config.Settings;
import butti.javalibs.config.SettingsPrefix;
import butti.javalibs.errorhandler.Errorhandler;
import butti.plugin.PluginDescription;
import butti.plugin.PluginManager;
import ch.zhaw.simulation.model.simulation.PluginChangeListener;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;

public class SimulationManager {

	private PluginManager<SimulationPlugin> pluginManager = new PluginManager<SimulationPlugin>();

	private PluginDescription<SimulationPlugin> selectedPlugin;

	public SimulationManager(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		String path = Config.get("simulationPluginFolder");
		if (path != null) {
			pluginManager.loadPlugins(path);

			for (Exception e : pluginManager.getPluginLoadErrors()) {
				Errorhandler.showError(e, "Plugin laden fehlgeschlagen");
			}

			for (PluginDescription<SimulationPlugin> desc : pluginManager.getPluginDescriptions()) {
				SimulationPlugin plugin = desc.getPlugin();
				SettingsPrefix sp = new SettingsPrefix(settings, "simplugin." + desc.getName());
				plugin.init(sp, config, provider);
			}

		} else {
			System.err.println("No simulationPluginFolder defined in config/config.properties");
		}

		config.addPluginChangeListener(new PluginChangeListener() {

			@Override
			public void pluginChanged(String plugin) {
				if (selectedPlugin != null && selectedPlugin.getName().equals(plugin)) {
					return;
				}

				if (selectedPlugin != null) {
					selectedPlugin.getPlugin().unload();
				}
				selectedPlugin = null;

				for (PluginDescription<SimulationPlugin> p : pluginManager.getPluginDescriptions()) {
					if (p.getName().equals(plugin)) {
						SimulationPlugin handler = p.getPlugin();
						try {
							handler.load();
						} catch (Exception e) {
							Errorhandler.showError(e, "Simulationsplugin konnte nicht aktiviert werden");
						}
						selectedPlugin = p;
						break;
					}
				}
			}
		});
	}

	public PluginDescription<SimulationPlugin> getSelectedPlugin() {
		return selectedPlugin;
	}
	
	public Vector<PluginDescription<SimulationPlugin>> getPluginManager() {
		return pluginManager.getPluginDescriptions();
	}

	public PluginDescription<SimulationPlugin> getPluginByName(String name) {
		if (name == null) {
			throw new NullPointerException("name == null");

		}
		for (PluginDescription<SimulationPlugin> p : pluginManager.getPluginDescriptions()) {
			if (name.equals(p.getName())) {
				return p;
			}
		}

		return null;
	}
}
