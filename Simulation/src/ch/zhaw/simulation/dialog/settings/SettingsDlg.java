package ch.zhaw.simulation.dialog.settings;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.app.ApplicationControl;
import ch.zhaw.simulation.inexport.ImportPlugin;
import ch.zhaw.simulation.plugin.SimulationPlugin;
import ch.zhaw.simulation.util.gui.GradientPanel;

public class SettingsDlg extends BDialog {
	private static final long serialVersionUID = 1L;
	private SettingsHeader header = new SettingsHeader();

	private JTabbedPane tabs = new JTabbedPane();

	private GridBagManager gbm;
	private Settings settings;

	private Vector<SettingsPanel> settingsPanels = new Vector<SettingsPanel>();

	public SettingsDlg(ApplicationControl app) {
		super(app.getMainFrame());
		setTitle("Einstellungen");

		this.settings = app.getSettings();

		// START TRANSACTION
		settings.startTransaction();
		// ///////////////////////////////////////////

		add(header, BorderLayout.NORTH);

		GradientPanel panel = new GradientPanel();

		gbm = new GridBagManager(panel);
		gbm.setX(0).setY(1).setInsets(new Insets(0, 0, 0, 0)).setComp(tabs);
		add(panel, BorderLayout.CENTER);

		GeneralTab generalTab = new GeneralTab(this, this.settings, app.getSysintegration());
		settingsPanels.add(generalTab);

		addTab("Allgemein", generalTab);

		for (PluginDescription<ImportPlugin> pluginDescription : app.getImportPluginLoader().getPluginDescriptions()) {
			SettingsPanel settingsPanel = pluginDescription.getPlugin().getSettingsPanel();

			if (settingsPanel != null) {
				settingsPanels.add(settingsPanel);
				addTab(pluginDescription.getName(), settingsPanel.getContentsPanel());
			}
		}

		for (PluginDescription<SimulationPlugin> pluginDescription : app.getManager().getPluginDescriptions()) {
			SettingsPanel settingsPanel = pluginDescription.getPlugin().getSettingsPanel();

			if (settingsPanel != null) {
				settingsPanels.add(settingsPanel);
				addTab(pluginDescription.getName(), settingsPanel.getContentsPanel());
			}
		}

		pack();
		int w = getWidth();
		if (w < 500) {
			w = 500;
		}
		setSize(w, getHeight());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveSettings();
			}
		});

		setLocationRelativeTo(app.getMainFrame());
	}

	protected void saveSettings() {
		for (SettingsPanel s : settingsPanels) {
			s.saveSettings();
		}

		// END TRANSACTION
		settings.finishTransaction();
		// ///////////////////////////////////////////
	}

	private void addTab(String name, JPanel panel) {
		JPanel pContents = new JPanel();
		pContents.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pContents.setLayout(new BorderLayout());
		tabs.addTab(name, pContents);

		pContents.add(panel, BorderLayout.NORTH);
	}
}
