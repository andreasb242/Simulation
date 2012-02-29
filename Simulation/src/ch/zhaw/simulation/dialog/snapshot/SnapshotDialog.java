package ch.zhaw.simulation.dialog.snapshot;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import butti.filechooser.TxtDirChooser;
import butti.javalibs.config.FileSettings;
import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmark;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;
import ch.zhaw.simulation.sysintegration.bookmarks.ComboSeparatorsRenderer;

public class SnapshotDialog extends BDialog implements ClipboardOwner {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private JTextField txtName = new JTextField(20);
	private JComboBox cbSavePath;

	private TxtDirChooser dirChooser;

	private JCheckBox cbSelection = new JCheckBox("Nur Selektion");

	private JRadioButton rPng = new JRadioButton("PNG");
	private JRadioButton rSvg = new JRadioButton("SVG");
	private ButtonGroup format = new ButtonGroup();

	private Settings settings;

	public SnapshotDialog(JFrame parent, Settings settings, Sysintegration sys, final JComponent c, final Rectangle size, String name) {
		super(parent);
		setTitle("Als Bild speichern...");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.settings = settings;

		dirChooser = new TxtDirChooser(this, false);

		gbm = new GridBagManager(this);

		final Bookmarks model = sys.getBookmarks();
		cbSavePath = new JComboBox(model);

		txtName.setText(name);

		ImageIcon img = IconSVG.getIcon("photos", 64);
		gbm.setX(0).setY(0).setHeight(5).setWeightX(0).setWeightY(0).setComp(new JLabel(img));

		gbm.setX(1).setY(0).setWeightY(0).setComp(new JLabel("Name"));
		gbm.setX(2).setY(0).setWeightY(0).setComp(txtName);

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
					if ("<custom>".equals(((Bookmark) model.getSelectedItem()).getPath())) {
						dirChooser.setVisible(true);
					} else {
						dirChooser.setVisible(false);
					}
				}
			}
		});

		gbm.setX(1).setY(1).setWeightY(0).setComp(new JLabel("Im Ordner speichern"));
		gbm.setX(2).setY(1).setWeightY(0).setComp(cbSavePath);

		gbm.setX(2).setY(2).setWeightY(0).setComp(dirChooser);

		gbm.setX(2).setY(3).setComp(cbSelection);

		JPanel pFormat = new JPanel();
		pFormat.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pFormat.add(rPng);
		pFormat.add(rSvg);
		format.add(rPng);
		format.add(rSvg);

		rPng.setSelected(true);

		gbm.setX(1).setY(5).setWeightY(0).setComp(new JLabel("Format"));
		gbm.setX(2).setY(5).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_START).setComp(pFormat);

		JButton btSave = ButtonFactory.createButton("Speichern", true);
		JButton btCopy = ButtonFactory.createButton("In die Zwischenablage kopieren", false);
		JButton btCancel = ButtonFactory.createButton("Abbrechen", false);

		btCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel pButton = new JPanel();
		pButton.setLayout(null);

		int width = (int) btCopy.getPreferredSize().getWidth() + 10;
		width += (int) btCancel.getPreferredSize().getWidth() + 10;
		width += (int) btSave.getPreferredSize().getWidth() + 10;

		int height = (int) btSave.getPreferredSize().getHeight();

		pButton.setPreferredSize(new Dimension(width, height));

		pButton.add(btCopy);
		pButton.add(btCancel);
		pButton.add(btSave);

		int h = (int) btCancel.getPreferredSize().getHeight();
		int w = (int) btCancel.getPreferredSize().getWidth();
		int x = 10;

		btCancel.setBounds(x, (height - h) / 2, w, h);

		x += 10 + w;

		h = (int) btCopy.getPreferredSize().getHeight();
		w = (int) btCopy.getPreferredSize().getWidth();

		btCopy.setBounds(x, (height - h) / 2, w, h);

		x += 10 + w;

		h = (int) btSave.getPreferredSize().getHeight();
		w = (int) btSave.getPreferredSize().getWidth();

		btSave.setBounds(x, (height - h) / 2, w, h);

		btSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Bookmark b = (Bookmark) model.getSelectedItem();
				SnapshotDialog.this.settings.setSetting("snapshot.path", b.getPath());
				SnapshotDialog.this.settings.setSetting("snapshot.custompath", dirChooser.getPath());

				if ("<custom>".equals(b.getPath())) {
					saveSnapshot(dirChooser.getPath(), c, size, txtName.getText());
				} else {
					if ("".equals(txtName.getText())) {
						Messagebox.showWarning(SnapshotDialog.this, "Ungültige Eingabe", "Bitte geben Sie einen Dateiname ein");
					} else {
						saveSnapshot(b.getPath(), c, size, txtName.getText());
					}
				}
			}
		});

		btCopy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyImageToClipboard(c, size);
			}
		});

		gbm.setX(0).setWidth(3).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END).setY(6).setWeightY(0).setComp(pButton);

		setModal(true);
		pack();

		dirChooser.setPath(settings.getSetting("snapshot.custompath", new File(".").getAbsolutePath()));

		cbSavePath.setSelectedItem(settings.getSetting("snapshot.path"));
		if (cbSavePath.getSelectedIndex() < 0) {
			cbSavePath.setSelectedIndex(0);
		}

		if (!"<custom>".equals(((Bookmark) model.getSelectedItem()).getPath())) {
			dirChooser.setVisible(false);
		}

		setLocationRelativeTo(parent);
	}

	protected void copyImageToClipboard(JComponent comp, Rectangle size) {
		BufferedImage img = createImage(comp, size);
		TransferableImage trans = new TransferableImage(img);
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(trans, this);
	}

	private BufferedImage createImage(JComponent comp, Rectangle size) {
		BufferedImage img = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		comp.paint(g);
		g.dispose();
		return img;
	}

	protected void saveSnapshot(String path, JComponent comp, Rectangle size, String name) {
		BufferedImage img = createImage(comp, size);

		for (int i = 0; i < 1000; i++) {
			String file = path + File.separator + name + i + ".png";
			File f = new File(file);
			if (!f.exists()) {
				try {
					ImageIO.write(img, "PNG", f);
					System.out.println("write image: " + f.getAbsolutePath());
					dispose();
				} catch (Exception e) {
					Errorhandler.showError(e, "Bild speichern fehlgeschlagen!");
				}
				return;
			}
		}

		Messagebox.showError(this, "Speichern fehlgeschlagen", "Speichern des Bildes ist fehlgeschlagen, bitte wählen Si einen anderen Dateinamen");
	}

	public static void main(String[] args) {
		SnapshotDialog dlg = new SnapshotDialog(null, new FileSettings("test.ini"), SysintegrationFactory.createSysintegration(), null, null, "test");
		dlg.setVisible(true);

		System.exit(0);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
