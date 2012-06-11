package ch.zhaw.simulation.dialog.snapshot;

import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import butti.javalibs.config.Config;
import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.GridBagManager;

public class MovieExport extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private JProgressBar pg = new JProgressBar();
	private JLabel lbState = new JLabel(" ");

	private SwingWorker<Void, Integer> worker;

	private Exception exception;
	private Process process;

	public MovieExport(Window parent, final MovieExportable export, final File path, final boolean movie, final String ffmpeg) {
		super(parent);
		setTitle("Exportieren...");
		setModal(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		gbm = new GridBagManager(this);

		gbm.setX(0).setY(0).setWeightY(0).setWidth(5).setComp(new TitleLabel("Exportieren der Bilder."));

		gbm.setX(0).setY(1).setWeightY(0).setWidth(5).setComp(pg);
		gbm.setX(0).setY(2).setWeightY(0).setWidth(5).setComp(lbState);

		JButton btCancel = ButtonFactory.createButton("Abbrechen", true);

		gbm.setX(4).setY(4).setFill(GridBagConstraints.NONE).setWeightY(0).setWeightX(0).setAnchor(GridBagConstraints.LINE_END).setComp(btCancel);

		btCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lbState.setText("Abgebrochen...");
				worker.cancel(false);

				synchronized (MovieExport.this) {
					if (process != null) {
						process.destroy();
					}
				}

				dispose();
			}
		});

		pack();
		setLocationRelativeTo(parent);

		final int count = export.getCount();
		pg.setMaximum(count);

		worker = new SwingWorker<Void, Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				try {
					for (int i = 0; i < count && !isCancelled(); i++) {
						File f = new File(path.getAbsolutePath() + File.separator + path.getName() + "_" + i + ".png");
						export.export(i, f);

						int progress;
						if (movie) {
							progress = (int) (((double) i) / (count + 1) * 100.0);
						} else {
							progress = (int) (((double) i) / count * 100.0);
						}
						setProgress(progress);
					}

					if (movie && !isCancelled()) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								lbState.setText("Konvertieren in Film, keine Statusanzeige verfügbar.");
							}
						});

						int fps = export.getFps();
						String bitrate = Config.get("ffmpegBitrate");
						if (bitrate == null) {
							bitrate = "1000k";
						}

						String output = path.getAbsolutePath() + File.separator + path.getName() + ".mp4";

						// ffmpeg -r 10 -b 1800 -i %03d.jpg test1800.mp4
						String[] cmd = new String[] { ffmpeg, "-r", Integer.toString(fps), "-b", bitrate, "-i",
								path.getAbsolutePath() + File.separator + path.getName() + "_%d.png", output };

						StringBuilder command = new StringBuilder();
						
						for(String s : cmd) {
							command.append(s + " ");
						}
						System.out.println(command.toString());
						
						synchronized (MovieExport.this) {
							process = Runtime.getRuntime().exec(cmd);
						}

						int result = process.waitFor();
						if (result != 0) {
							throw new Exception("ffmpeg failed, return code: " + result+"\ncommand="+command.toString());
						}
					}
				} catch (Exception e) {
					exception = e;
				}
				return null;
			}

		};

		worker.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					pg.setValue(worker.getProgress());
				} else if ("state".equals(evt.getPropertyName())) {
					System.out.println("state");
					if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
						dispose();
					}
				}
			}
		});

		worker.execute();
	}

	public Exception getException() {
		return exception;
	}
}
