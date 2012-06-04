package ch.zhaw.simulation.xyviewer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.icon.IconLoader;

public class PositionControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JSlider slider;
	private JLabel lbPosition = new JLabel("a / b");

	private GridBagManager gbm;

	private PositionListener internPositionListener = new PositionListener() {

		@Override
		public void positionChanged(int pos) {
			lbPosition.setText(pos + " / " + model.getStepCount());
			slider.setValue(pos);
		}
	};

	private PositionModel model;

	public PositionControlPanel(PositionModel model) {
		gbm = new GridBagManager(this, true);
		this.model = model;

		model.addListener(internPositionListener);

		this.slider = new JSlider(0, model.getStepCount());
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				PositionControlPanel.this.model.firePosition(slider.getValue());
			}
		});

		gbm.setX(0).setY(0).setWeightY(0).setWidth(10).setComp(slider);

		JButton playPause = new JButton(IconLoader.getIcon("player/play", 32));
		playPause.setBackground(Color.BLACK);
		gbm.setX(0).setY(1).setWeightX(0).setWeightY(0).setInsets(new Insets(5, 5, 5, 25)).setComp(playPause);

		JButton prev = new JButton(IconLoader.getIcon("player/previous", 24));
		prev.setBackground(Color.BLACK);
		JButton next = new JButton(IconLoader.getIcon("player/next", 24));
		next.setBackground(Color.BLACK);
		JButton stop = new JButton(IconLoader.getIcon("player/stop", 24));
		stop.setBackground(Color.BLACK);

		gbm.setX(1).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(prev);
		gbm.setX(2).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(stop);
		gbm.setX(3).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(next);

		gbm.setX(9).setY(1).setWeightX(0).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END).setComp(lbPosition);
	}

	public void dispose() {
		model.removeListener(internPositionListener);
	}
}
