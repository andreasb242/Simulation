package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.VerticalLayout;

import ch.zhaw.simulation.diagram.colorcheckbox.ColorCheckbox;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramSidebar extends JScrollPane implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel component;
	private DiagramPlot plot;
	private SimulationCollection collection;

	public DiagramSidebar(DiagramConfigModel model) {
		// TODO Auto-generated constructor stub
	}
	
	public DiagramSidebar(SimulationCollection collection, DiagramPlot plot) {
		super(new JPanel(), VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		this.collection = collection;
		this.plot = plot;
		this.component = (JPanel) getViewport().getView();

		this.component.setLayout(new VerticalLayout());

		int size = collection.size();
		Color[] colors = calcColors(size);

		for (int i = 0; i < size; i++) {
			SimulationSerie serie = collection.getSerie(i);
			serie.setColor(colors[i]);
			addEntry(serie.getName(), colors[i]);
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
		cc.addActionListener(this);
		this.component.add(cc);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JCheckBox) {
			SimulationCollection newCollection = new SimulationCollection(collection.getStartTime(), collection.getEndTime());
			int size = component.getComponentCount();
			for (int i = 0; i < size; i++) {
				Component c = component.getComponent(i);
				if (c instanceof ColorCheckbox) {
					ColorCheckbox cc = (ColorCheckbox) c;
					if (cc.isSelected()) {
						newCollection.addSeries(collection.getSerie(i));
					}
				}
			}
			plot.updateSimulationCollection(newCollection);
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
