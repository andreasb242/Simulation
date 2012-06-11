package ch.zhaw.simulation.dialog.snapshot;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class SnapshotDialog extends AbstractExportDialog {
	private static final long serialVersionUID = 1L;

	private JCheckBox cbSelection = new JCheckBox("Nur Selektion");

	private JRadioButton rPng = new JRadioButton("PNG");
	private JRadioButton rSVG = new JRadioButton("SVG");
	private JRadioButton rPS = new JRadioButton("PS");
	private JRadioButton rEMF = new JRadioButton("EMF");
	private ButtonGroup format = new ButtonGroup();

	private ImageExportable export;

	private JButton btCopy;

	public SnapshotDialog(JFrame parent, Settings settings, Sysintegration sys, ImageExportable exportable, String name) {
		super(parent, settings, sys, name, "Als Bild speichern...", "snapshot", "photos");

		this.export = exportable;

		initSelection();
		initFormat();

		initBtCopy();

		layoutButtons();

		setModal(true);
		pack();
		setLocationRelativeTo(parent);
	}

	private void initBtCopy() {
		this.btCopy = ButtonFactory.createButton("In die Zwischenablage kopieren", false);

		btCopy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyImageToClipboard();
				dispose();
			}
		});
	}

	private void initFormat() {
		JPanel pFormat = new JPanel();
		pFormat.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pFormat.add(rPng);
		pFormat.add(rSVG);
		pFormat.add(rPS);
		pFormat.add(rEMF);
		format.add(rPng);
		format.add(rSVG);
		format.add(rPS);
		format.add(rEMF);

		String format = settings.getSetting("snapshot.format", "PNG");
		if ("SVG".equals(format)) {
			rSVG.setSelected(true);
		} else if ("PS".equals(format)) {
			rPS.setSelected(true);
		} else if ("EMF".equals(format)) {
			rEMF.setSelected(true);
		} else {
			rPng.setSelected(true);
		}

		gbm.setX(1).setY(5).setWeightY(0).setComp(new JLabel("Format"));
		gbm.setX(2).setY(5).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_START).setComp(pFormat);
	}

	private void initSelection() {
		if (!this.export.supportsSelection()) {
			cbSelection.setVisible(false);
		}

		gbm.setX(2).setY(3).setComp(cbSelection);
	}

	protected void copyImageToClipboard() {
		boolean onlySelection = cbSelection.isSelected();
		export.exportToClipboard(onlySelection);
	}

	private String getFormat() {
		if (rPng.isSelected()) {
			return "PNG";
		} else if (rSVG.isSelected()) {
			return "SVG";
		} else if (rPS.isSelected()) {
			return "PS";
		} else if (rEMF.isSelected()) {
			return "EMF";
		}

		return "PNG";
	}

	@Override
	protected boolean export(String path, String name) {
		String format = getFormat();
		settings.setSetting("snapshot.format", format);

		File file = createFile(path, name, "." + format.toLowerCase());

		if (file != null) {
			try {
				boolean onlySelection = cbSelection.isSelected();
				export.export(onlySelection, format, file);
				return true;
			} catch (Exception e) {
				Errorhandler.showError(e, "Bild speichern fehlgeschlagen!");
			}
			return false;
		} else {
			Messagebox.showError(this, "Speichern fehlgeschlagen", "Speichern des Bildes ist fehlgeschlagen, bitte wählen Sie einen anderen Dateinamen");
			return false;
		}
	}

	@Override
	protected void layoutButtons() {
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
	}

}
