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

	private PluginDescription<SimulationPlugin> selectedPluginDescription;

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
			public void pluginChanged(String pluginName) {
				if (selectedPluginDescription != null && selectedPluginDescription.getName().equals(pluginName)) {
					return;
				}

				if (selectedPluginDescription != null) {
					selectedPluginDescription.getPlugin().unload();
				}
				selectedPluginDescription = null;

				for (PluginDescription<SimulationPlugin> p : pluginManager.getPluginDescriptions()) {
					if (p.getName().equals(pluginName)) {
						SimulationPlugin plugin = p.getPlugin();
						try {
							plugin.load();
						} catch (Exception e) {
							Errorhandler.showError(e, "Simulationsplugin konnte nicht aktiviert werden");
						}
						selectedPluginDescription = p;
						break;
					}
				}
			}
		});
	}

	public PluginDescription<SimulationPlugin> getSelectedPluginDescription() {
		return selectedPluginDescription;
	}
	
	public Vector<PluginDescription<SimulationPlugin>> getPluginDescriptions() {
		return pluginManager.getPluginDescriptions();
	}

	public PluginDescription<SimulationPlugin> getPluginDescriptionByName(String name) {
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
