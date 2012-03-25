package ch.zhaw.simulation.plugin;

import java.util.Vector;

import butti.javalibs.config.Config;
import butti.javalibs.config.Settings;
import butti.javalibs.config.SettingsPrefix;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.plugin.PluginDescription;
import butti.plugin.PluginManager;
import ch.zhaw.simulation.model.simulation.PluginChangeListener;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;

public class SimulationManager {

	private PluginManager<SimulationPlugin> pluginManager = new PluginManager<SimulationPlugin>();

	private PluginDescription<SimulationPlugin> selectedPluginDescription;

	public SimulationManager(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		String path = Config.get("simulationPluginFolder");

		boolean error = false;

		if (path != null) {
			pluginManager.loadPlugins(path);

			for (Exception e : pluginManager.getPluginLoadErrors()) {
				Errorhandler.logError(e);
				error = true;
			}
			
			Vector<PluginDescription<SimulationPlugin>> errorPlugins = new Vector<PluginDescription<SimulationPlugin>>();
			
			for (PluginDescription<SimulationPlugin> pluginDescription : pluginManager.getPluginDescriptions()) {
				try {
					SimulationPlugin plugin = pluginDescription.getPlugin();
					SettingsPrefix sp = new SettingsPrefix(settings, "simplugin." + pluginDescription.getName());
					plugin.init(sp, config, provider);
				} catch (Exception e) {
					errorPlugins.add(pluginDescription);

					Errorhandler.logError(e);
					error = true;
				}
			}
			
			for(PluginDescription<SimulationPlugin> p : errorPlugins) {
				if(pluginManager.unloadPlugin(p)) {
					System.err.println("Unload plugin because of problem: " + p.getClass());
				} else {
					System.err.println("Unload plugin failed: " + p.getClass());
				}
			}

			if (error) {
				Messagebox.showError(null, "Plugin", "<html><b>Nicht alle Plugins konnte geladen werden.</b> " +
						"Details entnehmen Sie dem Errorlog.<br>" +
						"Höchstwarscheinlich handelt es sich bei diesem Fehler um einen Programmierfehler, konntaktieren Sie den Entwickler.</html>");
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

				for (PluginDescription<SimulationPlugin> pluginDescription : pluginManager.getPluginDescriptions()) {
					if (pluginDescription.getName().equals(pluginName)) {
						SimulationPlugin plugin = pluginDescription.getPlugin();
						try {
							plugin.load();
						} catch (Exception e) {
							Errorhandler.showError(e, "Simulationsplugin konnte nicht aktiviert werden");
						}
						selectedPluginDescription = pluginDescription;
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
