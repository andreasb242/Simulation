package ch.zhaw.simulation.diagram;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.plugin.data.SimulationSerie.SerieSource;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class SerieCbRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	private TypeIcons icons;

	public SerieCbRenderer(Sysintegration sys) {
		this.icons = new TypeIcons(sys);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof SimulationSerie) {
			SerieSource type = ((SimulationSerie) value).getSource();
			setIcon(this.icons.iconFor(type));

		} else if (value instanceof String) {
			setIcon(this.icons.ICON_TIME);
		}

		return this;
	}
}
