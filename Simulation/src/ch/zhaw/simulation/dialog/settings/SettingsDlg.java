package ch.zhaw.simulation.dialog.settings;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import butti.javalibs.config.Settings;
import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.app.ApplicationControl;
import ch.zhaw.simulation.filechoosertextfield.FilechooserTextfield;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.inexport.ImportPlugin;
import ch.zhaw.simulation.plugin.SimulationPlugin;
import ch.zhaw.simulation.util.gui.GradientPanel;
import ch.zhaw.simulation.util.gui.HeaderPanel;

public class SettingsDlg extends BDialog {
	private static final long serialVersionUID = 1L;
	private HeaderPanel header = new HeaderPanel();

	private JCheckBox cbAutoloadLastDocument = new JCheckBox("Letzte geöffnete Datei beim Start laden");
	private JCheckBox cbShowAdvancedDiagramSettings = new JCheckBox(
			"<html>Eweiterter Diagramm Einstellungen Dialog verwenden<br><i>(komplizierter, nicht empfohlen)</i></html>");

	private FilechooserTextfield execFfmpegPath;

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
		/////////////////////////////////////////////

		
		add(header, BorderLayout.NORTH);

		GradientPanel panel = new GradientPanel();

		gbm = new GridBagManager(panel);
		gbm.setX(0).setY(1).setInsets(new Insets(0, 0, 0, 0)).setComp(tabs);
		add(panel, BorderLayout.CENTER);

		JPanel pGeneral = new JPanel();
		pGeneral.setLayout(new GridLayout(0, 1, 0, 5));
		pGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		addTab("Allgemein", pGeneral);

		pGeneral.add(cbAutoloadLastDocument);
		pGeneral.add(cbShowAdvancedDiagramSettings);

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

		execFfmpegPath = new FilechooserTextfield(this, app.getSysintegration(), null, false, false, true);

		initHeader();
		initData();

		pGeneral.add(new JLabel("ffmpeg Executable"));
		pGeneral.add(execFfmpegPath);

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
		settings.setSetting("autoloadLastDocument", cbAutoloadLastDocument.isSelected());
		settings.setSetting("extendedDiagramSettings", cbShowAdvancedDiagramSettings.isSelected());
		settings.setSetting("ffmpegPath", execFfmpegPath.getPath());

		for (SettingsPanel s : settingsPanels) {
			s.saveSettings();
		}

		// END TRANSACTION
		settings.finishTransaction();
		/////////////////////////////////////////////
	}

	private void addTab(String name, JPanel panel) {
		JPanel pContents = new JPanel();
		pContents.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pContents.setLayout(new BorderLayout());
		tabs.addTab(name, pContents);

		pContents.add(panel, BorderLayout.NORTH);
	}

	private void initData() {
		cbAutoloadLastDocument.setSelected(settings.isSetting("autoloadLastDocument", false));
		cbShowAdvancedDiagramSettings.setSelected(settings.isSetting("extendedDiagramSettings", false));
		String ffmpeg = settings.getSetting("ffmpegPath", "ffmpeg");
		execFfmpegPath.setPath(ffmpeg);
	}

	private void initHeader() {
		header.setLayout(new BorderLayout());

		ImageIcon icon = IconLoader.getIcon("preferences", 64);

		JPanel titel = new JPanel(new GridLayout(0, 1));
		titel.setOpaque(false);
		titel.setBorder(new EmptyBorder(12, 0, 12, 0));

		TitleLabel titleLabel = new TitleLabel("Einstellungen von Simulation");
		Font police = titleLabel.getFont().deriveFont(Font.BOLD);
		titleLabel.setFont(police);
		titleLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
		titel.add(titleLabel);

		JLabel lbTitle;
		titel.add(lbTitle = new JLabel("<html>Hier können Sie generelle Einstellungen<br>des Programmes festlegen</html>"));
		police = lbTitle.getFont().deriveFont(Font.PLAIN);
		lbTitle.setFont(police);
		lbTitle.setBorder(new EmptyBorder(0, 24, 0, 0));

		lbTitle = new JLabel(icon);
		lbTitle.setBorder(new EmptyBorder(0, 0, 0, 12));

		header.add(titel, BorderLayout.WEST);
		header.add(lbTitle, BorderLayout.EAST);
	}

}
