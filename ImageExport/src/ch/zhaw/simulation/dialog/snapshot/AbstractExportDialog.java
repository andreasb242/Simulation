package ch.zhaw.simulation.dialog.snapshot;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.filechoosertextfield.FilechooserTextfield;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmark;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;
import ch.zhaw.simulation.sysintegration.bookmarks.ComboSeparatorsRenderer;

public abstract class AbstractExportDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	protected GridBagManager gbm;

	protected JTextField txtName = new JTextField(20);
	protected JComboBox cbSavePath;

	protected FilechooserTextfield dirChooser;

	protected Settings settings;

	protected String settingsPrefix;

	protected Bookmarks bookmarkModel;

	protected JButton btSave;

	protected JPanel pButton = new JPanel();

	protected JButton btCancel;

	public AbstractExportDialog(Window parent, Settings settings, Sysintegration sys, String name, String title, String settingsPrefix, String iconName) {
		super(parent);

		this.settingsPrefix = settingsPrefix;
		this.settings = settings;

		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		gbm = new GridBagManager(this);

		initDirchooser(sys);
		initSavepath(sys);
		initTxtName(name);
		initBtSave();
		initBtCancel();

		ImageIcon img = IconLoader.getIcon(iconName, 64);
		gbm.setX(0).setY(0).setHeight(5).setWeightX(0).setWeightY(0).setComp(new JLabel(img));

		// Button position
		gbm.setX(0).setWidth(3).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END).setY(6).setWeightY(0).setComp(pButton);

		loadDefaults();
	}

	protected void initBtCancel() {
		this.btCancel = ButtonFactory.createButton("Abbrechen", false);

		btCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	protected abstract void layoutButtons();

	protected void initBtSave() {
		this.btSave = ButtonFactory.createButton("Speichern", true);

		btSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Bookmark b = (Bookmark) bookmarkModel.getSelectedItem();
				AbstractExportDialog.this.settings.setSetting(settingsPrefix + ".path", b.getPath());
				AbstractExportDialog.this.settings.setSetting(settingsPrefix + ".custompath", dirChooser.getPath());

				if ("<custom>".equals(b.getPath())) {
					if (export(dirChooser.getPath(), txtName.getText())) {
						dispose();
					}
				} else {
					if ("".equals(txtName.getText())) {
						Messagebox.showWarning(AbstractExportDialog.this, "Ungültige Eingabe", "Bitte geben Sie einen Dateiname ein");
					} else {
						if (export(b.getPath(), txtName.getText())) {
							dispose();
						}
					}
				}
			}
		});

	}

	protected void loadDefaults() {
		dirChooser.setPath(settings.getSetting(this.settingsPrefix + ".custompath", new File(".").getAbsolutePath()));

		String lastPath = settings.getSetting(this.settingsPrefix + ".path");
		for (int i = 0; i < cbSavePath.getItemCount(); i++) {
			Bookmark b = (Bookmark) cbSavePath.getItemAt(i);
			if (b.getPath().equals(lastPath)) {
				cbSavePath.setSelectedIndex(i);
				break;
			}
		}

		if (cbSavePath.getSelectedIndex() < 0) {
			cbSavePath.setSelectedIndex(0);
		}

		if (!"<custom>".equals(((Bookmark) bookmarkModel.getSelectedItem()).getPath())) {
			dirChooser.setVisible(false);
		}
	}

	protected void initTxtName(String name) {
		int pos = name.lastIndexOf('.');
		if (pos > 0) {
			// remove .xxx
			name = name.substring(0, pos);
		}
		
		txtName.setText(name);

		gbm.setX(1).setY(0).setWeightY(0).setComp(new JLabel("Name"));
		gbm.setX(2).setY(0).setWeightY(0).setComp(txtName);
	}

	protected void initSavepath(Sysintegration sys) {
		this.bookmarkModel = sys.getBookmarks();
		cbSavePath = new JComboBox(bookmarkModel);
		cbSavePath.setRenderer(new ComboSeparatorsRenderer(cbSavePath.getRenderer()) {

			@Override
			protected boolean addSeparatorAfter(JList list, Object value, int index) {
				if (value instanceof Bookmark) {
					return ((Bookmark) value).isSeparator();
				}
				return false;
			}

			@Override
			public void initComponent(Component comp, Object value) {
				if (comp instanceof JLabel) {
					if (value instanceof Bookmark) {
						((JLabel) comp).setIcon(((Bookmark) value).getIcon());
					} else {
						((JLabel) comp).setIcon(null);
					}
				}
			}

		});

		cbSavePath.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("<custom>".equals(((Bookmark) bookmarkModel.getSelectedItem()).getPath())) {
						dirChooser.setVisible(true);
						pack();
					} else {
						dirChooser.setVisible(false);
					}
				}
			}
		});

		gbm.setX(1).setY(1).setWeightY(0).setComp(new JLabel("Im Ordner speichern"));
		gbm.setX(2).setY(1).setWeightY(0).setComp(cbSavePath);
	}

	protected void initDirchooser(Sysintegration sys) {
		dirChooser = new FilechooserTextfield(this, sys, null, true, false, false);
		gbm.setX(2).setY(2).setWeightY(0).setComp(dirChooser);
	}

	protected abstract boolean export(String path, String name);

	protected File createFile(String path, String name, String ext) {
		for (int i = 0; i < 1000; i++) {
			String file = path + File.separator + name + i + ext;
			File f = new File(file);
			if (!f.exists()) {
				return f;
			}
		}
		return null;
	}

}
