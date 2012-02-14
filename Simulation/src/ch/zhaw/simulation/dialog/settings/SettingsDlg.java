package ch.zhaw.simulation.dialog.settings;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import ch.zhaw.simulation.dialog.aboutdlg.GradientPanel;
import ch.zhaw.simulation.gui.HeaderPanel;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.inexport.ImportReader;

import butti.javalibs.config.Settings;
import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import butti.plugin.PluginDescription;

public class SettingsDlg extends BDialog {
	private static final long serialVersionUID = 1L;
	private SimulationControl control;
	private HeaderPanel header = new HeaderPanel();

	private JCheckBox cbAutoloadLastDocument = new JCheckBox("Letzte geöffnete Datei beim Start laden");

	private JTabbedPane tabs = new JTabbedPane();

	private GridBagManager gbm;

	public SettingsDlg(SimulationControl control) {
		super(control.getParent());
		setTitle("Einstellungen");

		this.control = control;
		add(header, BorderLayout.NORTH);

		GradientPanel panel = new GradientPanel();

		gbm = new GridBagManager(panel);
		gbm.setX(0).setY(1).setComp(tabs);
		add(panel, BorderLayout.CENTER);

		tabs.addTab("Generell", cbAutoloadLastDocument);

		for (PluginDescription<ImportReader> p : control.getImportPlugins().getPlugins()) {
			JPanel settingsPanel = p.getPlugin().getSettingsPanel();
			
			if(settingsPanel!=null) {
				tabs.addTab(p.getName(), settingsPanel);
			}
		}

		initHeader();
		initData();

		pack();
		setLocationRelativeTo(control.getParent());
	}

	private void initData() {
		final Settings settings = control.getSettings();

		cbAutoloadLastDocument.setSelected(settings.isSetting("autoloadLastDocument", false));
		cbAutoloadLastDocument.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.setSetting("autoloadLastDocument", cbAutoloadLastDocument.isSelected());
			}
		});
	}

	private void initHeader() {
		header.setLayout(new BorderLayout());

		ImageIcon icon = IconSVG.getIcon("preferences", 64);

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

		header.add(BorderLayout.WEST, titel);
		header.add(BorderLayout.EAST, lbTitle);
	}

}
