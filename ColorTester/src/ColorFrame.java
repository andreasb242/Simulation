import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Color[] colors;
	private JPanel panel;
	private JSpinner spinner;

	public ColorFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		this.spinner = new JSpinner(new SpinnerNumberModel(5, 1, 255, 1));
		add(BorderLayout.EAST, spinner);

		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				recalcColors();
			}
		});

		this.panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());

				int x = 0;
				int y = (getHeight() - 20) / 2;
				for (Color c : colors) {
					g.setColor(c);
					g.fillRect(x, y, 20, 20);
					x += 25;
				}
			}
		};

		add(BorderLayout.CENTER, panel);
		recalcColors();

		setSize(500, 100);
		setLocationRelativeTo(null);
	}

	private void recalcColors() {
		this.colors = calcColors((Integer) spinner.getValue());
		this.panel.repaint();
	}

	public Color[] calcColors(int count) {
		Color[] colors = new Color[count];

		float step = 360.0f / count;
		float angle = 0;

		for (int i = 0; i < count; i++) {
			colors[i] = Color.getHSBColor(angle / 360.0f, 1.0f, 1.0f);
			angle += step;
		}

		return colors;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ColorFrame f = new ColorFrame();
		f.setVisible(true);
	}
}
