package ch.zhaw.simulation.widgets.zoom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.UnsupportedLookAndFeelException;

import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.gui.ImageButton;


public class DiagrammZoom extends JComponent {
	private static final long serialVersionUID = 1L;

	private JSlider slider;

	private ImageButton btPlus = new ImageButton(new ToolbarAction("Zoom in", getIcon("plus.png")) {

		@Override
		protected void actionPerformed(ActionEvent e) {

		}
	});

	private ImageButton btMinus = new ImageButton(new ToolbarAction("Zoom out", getIcon("minus.png")) {

		@Override
		protected void actionPerformed(ActionEvent e) {

		}
	});

	public DiagrammZoom() {
		setLayout(new BorderLayout());

		slider = new JSlider();

		slider.setPaintLabels(false);
		slider.setPaintTicks(false);
		slider.setPaintTrack(false);

		add(btMinus, BorderLayout.WEST);
		add(slider, BorderLayout.CENTER);
		add(btPlus, BorderLayout.EAST);

		slider.setUI(new DiagrammSliderUi(slider));

		slider.setPaintTicks(false);
		slider.setPaintTrack(true);
		slider.setPaintLabels(false);
	}

	static ImageIcon getIcon(String name) {
		try {
			return new ImageIcon(ImageIO.read(DiagrammSliderUi.class.getResource(name)));
		} catch (IOException e) {
			System.out.println("Icon " + name + " not found");
			return null;
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new DiagrammZoom());
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}
