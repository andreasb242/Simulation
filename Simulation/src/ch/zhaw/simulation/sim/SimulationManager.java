package ch.zhaw.simulation.sim;

import java.awt.Window;
import java.util.Vector;

import butti.javalibs.config.Config;
import butti.javalibs.config.Settings;
import butti.javalibs.config.SettingsPrefix;
import butti.javalibs.errorhandler.Errorhandler;
import butti.plugin.PluginDescription;
import butti.plugin.PluginManager;

public class SimulationManager {

	private PluginManager<SimulationPlugin> plugins = new PluginManager<SimulationPlugin>();

	public SimulationManager(Settings settings, Window parent) {
		String path = Config.get("simulationPluginFolder");
		if (path != null) {
			plugins.loadPlugins(path);

			for (Exception e : plugins.getPluginLoadErrors()) {
				Errorhandler.showError(e, "Plugin laden fehlgeschlagen");
			}

			for (PluginDescription<SimulationPlugin> p : plugins.getPlugins()) {
				SimulationPlugin handler = p.getPlugin();
				SettingsPrefix sp = new SettingsPrefix(settings, "simplugin." + p.getName());
				handler.init(sp, parent);
			}

		} else {
			System.err.println("No simulationPluginFolder defined in config/config.properties");
		}
	}

	public Vector<PluginDescription<SimulationPlugin>> getPlugins() {
		return plugins.getPlugins();
	}
	
}
