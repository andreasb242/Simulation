package ch.zhaw.simulation.dialog.snapshot;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class MovieExportDialog extends AbstractExportDialog {
	private static final long serialVersionUID = 1L;

	private JRadioButton rImages = new JRadioButton("Nur Bilder");
	private JRadioButton rMoview = new JRadioButton("Bilder konvertieren in .mpeg");
	private ButtonGroup format = new ButtonGroup();

	private MovieExportable exportable;

	public MovieExportDialog(JFrame parent, Settings settings, MovieExportable exportable, Sysintegration sys, String name) {
		super(parent, settings, sys, name, "Als Film speichern...", "movieexport", "diagram/video");

		this.exportable = exportable;

		initFormat();

		layoutButtons();

		setModal(true);
		pack();
		setLocationRelativeTo(parent);
	}

	private void initFormat() {
		JPanel pFormat = new JPanel();
		pFormat.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pFormat.add(rImages);
		pFormat.add(rMoview);
		format.add(rImages);
		format.add(rMoview);

		String format = settings.getSetting("movieexport.format", "Images");
		if ("Movie".equals(format)) {
			rMoview.setSelected(true);
		} else {
			rImages.setSelected(true);
		}

		gbm.setX(1).setY(5).setWeightY(0).setComp(new JLabel("Format"));
		gbm.setX(2).setY(5).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_START).setComp(pFormat);
	}

	private String getFormat() {
		if (rImages.isSelected()) {
			return "Images";
		} else if (rMoview.isSelected()) {
			return "Movie";
		}

		return "Images";
	}

	@Override
	protected boolean export(String path, String name) {
		String format = getFormat();
		settings.setSetting("movieexport.format", format);

		File file = createFile(path, name, "");

		if (file != null) {
			try {
				file.mkdirs();

				String ffmpeg = settings.getSetting("ffmpegPath", "ffmpeg");
				
				MovieExport dlg = new MovieExport(this, this.exportable, file, rMoview.isSelected(), ffmpeg);
				dlg.setVisible(true);

				if (dlg.getException() != null) {
					throw dlg.getException();
				}

				return true;
			} catch (Exception e) {
				Errorhandler.showError(e, "Bild speichern fehlgeschlagen!");
			}
			return false;
		} else {
			Messagebox.showError(this, "Speichern fehlgeschlagen", "Speichern des Filmes ist fehlgeschlagen, bitte wählen Sie einen anderen Dateinamen");
			return false;
		}
	}

	@Override
	protected void layoutButtons() {
		pButton.setLayout(null);

		int width = (int) btCancel.getPreferredSize().getWidth() + 10;
		width += (int) btSave.getPreferredSize().getWidth() + 10;

		int height = (int) btSave.getPreferredSize().getHeight();

		pButton.setPreferredSize(new Dimension(width, height));

		pButton.add(btCancel);
		pButton.add(btSave);

		int h = (int) btCancel.getPreferredSize().getHeight();
		int w = (int) btCancel.getPreferredSize().getWidth();
		int x = 10;

		btCancel.setBounds(x, (height - h) / 2, w, h);

		x += 10 + w;

		h = (int) btSave.getPreferredSize().getHeight();
		w = (int) btSave.getPreferredSize().getWidth();

		btSave.setBounds(x, (height - h) / 2, w, h);
	}

}
