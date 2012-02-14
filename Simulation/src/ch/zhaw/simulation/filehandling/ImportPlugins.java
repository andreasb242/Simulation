package ch.zhaw.simulation.filehandling;


import java.io.File;
import java.util.Vector;


import butti.javalibs.config.Config;
import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.plugin.PluginDescription;
import butti.plugin.PluginManager;
import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.inexport.ImportReader;
import ch.zhaw.simulation.inexport.madonna.MadonnaReader;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.sysintegration.SimFileFilter;

public class ImportPlugins {

	private Settings settings;
	private PluginManager<ImportReader> importPlugins = new PluginManager<ImportReader>();
	private Vector<PluginDescription<ImportReader>> plugins;
	private SimFileFilter simulationFileOpen;

	public ImportPlugins(Settings settings) {
		this.settings = settings;
	}

	private void loadPlugins() {
		if (this.plugins != null) {
			return;
		}

		String importPluginFolder = Config.get("importPluginFolder");
		if (importPluginFolder != null) {
			importPlugins.loadPlugins(importPluginFolder);

			for (Exception e : importPlugins.getPluginLoadErrors()) {
				Errorhandler.showError(e, "Plugin laden fehlgeschlagen");
			}

			for (PluginDescription<ImportReader> plugin : importPlugins.getPlugins()) {
				ImportReader handler = plugin.getPlugin();
				handler.init(settings);
			}

		} else {
			System.err.println("No importPluginFolder defined in config/config.properties");
		}

		this.plugins = importPlugins.getPlugins();

		simulationFileOpen = new SimFileFilter() {
			@Override
			public boolean accept(File f) {
				for (PluginDescription<ImportReader> def : plugins) {
					ImportReader p = def.getPlugin();
					String[] exts = p.getFileExtension();
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

	public Vector<PluginDescription<ImportReader>> getPlugins() {
		loadPlugins();

		return plugins;
	}
}
