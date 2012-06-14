package ch.zhaw.simulation.dialog.settings;

import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.filechoosertextfield.FilechooserTextfield;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class GeneralTab extends JPanel implements SettingsPanel {
	private static final long serialVersionUID = 1L;

	private JCheckBox cbAutoloadLastDocument;
	private JCheckBox cbShowAdvancedDiagramSettings;
	private FilechooserTextfield execFfmpegPath;

	private Settings settings;

	public GeneralTab(Window parent, Settings settings, Sysintegration sys) {
		this.settings = settings;
		setLayout(new GridLayout(0, 1, 0, 5));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		cbAutoloadLastDocument = new JCheckBox("Letzte geöffnete Datei beim Start laden");
		add(cbAutoloadLastDocument);

		cbShowAdvancedDiagramSettings = new JCheckBox(
				"<html>Eweiterter Diagramm Einstellungen Dialog verwenden<br><i>(komplizierter, nicht empfohlen)</i></html>");
		add(cbShowAdvancedDiagramSettings);

		execFfmpegPath = new FilechooserTextfield(parent, sys, null, false, false, true);

		add(new JLabel("<html>ffmpeg Executable <i>wird verwendet um Videos zu exportieren</i></html>"));
		add(execFfmpegPath);

		initData();
	}

	private void initData() {
		cbAutoloadLastDocument.setSelected(settings.isSetting("autoloadLastDocument", false));
		cbShowAdvancedDiagramSettings.setSelected(settings.isSetting("extendedDiagramSettings", false));
		String ffmpeg = settings.getSetting("ffmpegPath", "ffmpeg");
		execFfmpegPath.setPath(ffmpeg);
	}

	@Override
	public JPanel getContentsPanel() {
		return this;
	}

	@Override
	public void saveSettings() {
		settings.setSetting("autoloadLastDocument", cbAutoloadLastDocument.isSelected());
		settings.setSetting("extendedDiagramSettings", cbShowAdvancedDiagramSettings.isSelected());
		settings.setSetting("ffmpegPath", execFfmpegPath.getPath());
	}

}
