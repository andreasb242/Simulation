package ch.zhaw.simulation.dialog.settings;

import javax.swing.JPanel;

public interface SettingsPanel {
	public JPanel getContentsPanel();
	public void saveSettings();
}
