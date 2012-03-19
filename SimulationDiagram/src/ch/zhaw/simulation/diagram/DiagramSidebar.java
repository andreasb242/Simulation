package ch.zhaw.simulation.diagram;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.VerticalLayout;

import ch.zhaw.simulation.diagram.colorcheckbox.ColorCheckbox;

public class DiagramSidebar extends JScrollPane {
	private static final long serialVersionUID = 1L;

	private JPanel pConents;

	public DiagramSidebar() {
		super(new JPanel(), VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		this.pConents = (JPanel) getViewport().getView();

		this.pConents.setLayout(new VerticalLayout());

		String[] names = new String[] { "a", "b", "c", "d", "Auto", "Velo", "Melone", "Lorem Ipsum" };
		Color[] colors = calcColors(names.length);

		for (int i = 0; i < names.length; i++) {
			addEntry(names[i], colors[i]);
		}
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

	public void addEntry(String name, Color c) {
		ColorCheckbox cc = new ColorCheckbox(name);
		cc.setColor(c);
		this.pConents.add(cc);
	}
}
