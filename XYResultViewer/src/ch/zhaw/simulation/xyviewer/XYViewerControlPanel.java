package ch.zhaw.simulation.xyviewer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.util.OS;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.icon.IconLoader;

public class XYViewerControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JSlider slider;
	private JLabel lbPosition = new JLabel("a / b");

	private Timer steptimer;

	private GridBagManager gbm;

	private PositionListener internPositionListener = new PositionListener() {

		@Override
		public void positionChanged(int pos) {
			lbPosition.setText(pos + " / " + model.getStepCount());
			slider.setValue(pos);
		}
	};

	private PositionModel model;

	private JButton playPause;

	private JSpinner spinFps;

	private Settings settings;

	public XYViewerControlPanel(PositionModel model, Settings settings) {
		gbm = new GridBagManager(this);
		this.model = model;
		this.settings = settings;

		int fps = (int) settings.getSetting("xyviewer.fps", 20);
		this.spinFps = new JSpinner(new SpinnerNumberModel(fps, 1, 60, 1));

		this.spinFps.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				XYViewerControlPanel.this.settings.setSetting("xyviewer.fps", getFps());
			}
		});

		model.addListener(internPositionListener);

		this.slider = new JSlider(0, model.getStepCount());
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				XYViewerControlPanel.this.model.firePosition(slider.getValue());
			}
		});

		gbm.setX(0).setY(0).setWeightY(0).setWidth(20).setComp(slider);

		this.playPause = new JButton(IconLoader.getIcon("player/play", 32));
		initButton(playPause);

		playPause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startPause();
			}
		});

		gbm.setX(0).setY(1).setWeightX(0).setWeightY(0).setInsets(new Insets(5, 5, 5, 25)).setComp(playPause);

		JButton prev = new JButton(IconLoader.getIcon("player/previous", 24));
		initButton(prev);

		prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				XYViewerControlPanel.this.model.positionPrev();
			}
		});


		JButton next = new JButton(IconLoader.getIcon("player/next", 24));
		initButton(next);

		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				XYViewerControlPanel.this.model.positionNext();
			}
		});


		JButton stop = new JButton(IconLoader.getIcon("player/stop", 24));
		initButton(stop);

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});


		gbm.setX(1).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(prev);
		gbm.setX(2).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(stop);
		gbm.setX(3).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(next);

		gbm.setX(4).setY(1).setWeightX(0).setWeightY(0).setInsets(new Insets(5, 25, 5, 5)).setFill(GridBagConstraints.NONE)
				.setAnchor(GridBagConstraints.CENTER).setComp(this.spinFps);
		gbm.setX(5).setY(1).setWeightX(0).setWeightY(0).setInsets(new Insets(5, 5, 5, 25)).setFill(GridBagConstraints.NONE)
				.setAnchor(GridBagConstraints.CENTER).setComp(new JLabel("fps"));
		
		
		if (OS.isMacOSX()) {
			setBackground(new Color(0xdde3eb));
			setOpaque(true);
		}

	}

	private void initButton(JButton button) {
		button.setBackground(Color.BLACK);
	}

	protected void stop() {
		pause();
		model.positionStart();
	}

	protected void startPause() {
		if (this.steptimer == null) {
			start();
		} else {
			pause();
		}

	}

	public int getFps() {
		return (Integer) XYViewerControlPanel.this.spinFps.getValue();
	}

	protected void start() {
		this.playPause.setIcon(IconLoader.getIcon("player/pause", 32));

		if (this.model.isLast()) {
			this.model.positionStart();
		}

		int fps = getFps();
		int period = 1000 / fps;

		this.steptimer = new Timer();
		this.steptimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!model.positionNext()) {
					pause();
				}
			}
		}, period, period);
	}

	protected void pause() {
		this.playPause.setIcon(IconLoader.getIcon("player/play", 32));

		if (this.steptimer != null) {
			this.steptimer.cancel();
			this.steptimer = null;
		}
	}

	public void dispose() {
		model.removeListener(internPositionListener);
	}

}
