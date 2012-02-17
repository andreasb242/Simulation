package ch.zhaw.simulation.sim;

import java.awt.Window;
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

	private PluginManager<SimulationPlugin> plugins = new PluginManager<SimulationPlugin>();

	private PluginDescription<SimulationPlugin> selectedPlugin;

	public SimulationManager(Settings settings, SimulationConfiguration config, Window parent) {
		String path = Config.get("simulationPluginFolder");
		if (path != null) {
			plugins.loadPlugins(path);

			for (Exception e : plugins.getPluginLoadErrors()) {
				Errorhandler.showError(e, "Plugin laden fehlgeschlagen");
			}

			for (PluginDescription<SimulationPlugin> p : plugins.getPlugins()) {
				SimulationPlugin handler = p.getPlugin();
				SettingsPrefix sp = new SettingsPrefix(settings, "simplugin." + p.getName());
				handler.init(sp, config, parent);
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

				for (PluginDescription<SimulationPlugin> p : plugins.getPlugins()) {
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

	public Vector<PluginDescription<SimulationPlugin>> getPlugins() {
		return plugins.getPlugins();
	}

	public PluginDescription<SimulationPlugin> getPluginByName(String name) {
		if (name == null) {
			throw new NullPointerException("name == null");

		}
		for (PluginDescription<SimulationPlugin> p : plugins.getPlugins()) {
			if (name.equals(p.getName())) {
				return p;
			}
		}

		return null;
	}
}
