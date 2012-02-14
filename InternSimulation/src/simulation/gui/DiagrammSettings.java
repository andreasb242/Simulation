package simulation.gui;

import gui.diagramm.DiagrammControl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jdesktop.swingx.VerticalLayout;

import simulation.data.SimulationSerie;

public class DiagrammSettings extends JPanel {
	private static final long serialVersionUID = 1L;
	private Insets insets = new Insets(10, 2, 10, 10);
	private DiagrammControl control;

	public DiagrammSettings(DiagrammControl control) {
		setLayout(new VerticalLayout());

		this.control = control;
		SimulationSerie[] data = control.getData();

		for (int i = 0; i < data.length; i++) {
			addCb(data[i], i);
		}
	}

	@Override
	public Insets getInsets() {
		return insets;
	}

	private void addCb(SimulationSerie s, final int id) {
		LegendCheckbox cb = new LegendCheckbox(s, id, s.getColor());
		add(cb);
	}

	class LegendCheckbox extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;

		private int id;

		private Color color;

		private JCheckBox cb;

		public LegendCheckbox(SimulationSerie series, int id, Color color) {
			cb = new JCheckBox(series.getName());
			this.id = id;
			this.color = color;

			setLayout(null);

			cb.setSelected(true);
			cb.addActionListener(this);
			add(cb);
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			super.setBounds(x, y, width, height);
			cb.setBounds(20, 0, width - 20, height);
		}

		public Dimension getPreferredSize() {
			Dimension s = cb.getPreferredSize();
			return new Dimension(s.width + 20, s.height);
		}
		
		@Override
		public Dimension getMinimumSize() {
			Dimension s = cb.getMinimumSize();
			return new Dimension(s.width + 20, s.height);
		}
		
		@Override
		public Dimension getMaximumSize() {
			Dimension s = cb.getMaximumSize();
			return new Dimension(s.width + 20, s.height);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.WHITE);
			g.fillRect(2, (getHeight() - 16) / 2, 16, 16);
			g.setColor(color);
			g.drawRect(2, (getHeight() - 16) / 2, 16, 16);
			g.drawRect(3, (getHeight() - 14) / 2, 14, 14);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			control.setVisibel(id, cb.isSelected());
		}
	}
}
