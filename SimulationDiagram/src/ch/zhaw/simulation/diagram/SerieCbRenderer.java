package ch.zhaw.simulation.diagram;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.plugin.data.SimulationSerie.SerieSource;

public class SerieCbRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	private final Icon ICON_TIME;
	private final ImageIcon ICON_GLOBAL;
	private final ImageIcon ICON_CONTAINER;
	private final ImageIcon ICON_PARAMETER;
	private final ImageIcon ICON_ARROW;
	private final ImageIcon ICON_DENSITY;

	public SerieCbRenderer() {
		ICON_TIME = IconLoader.getIcon("type/time", 22);
		ICON_GLOBAL = IconLoader.getIcon("type/global", 22);
		ICON_CONTAINER = IconLoader.getIcon("type/container", 22);
		ICON_PARAMETER = IconLoader.getIcon("type/parameter", 22);
		ICON_ARROW = IconLoader.getIcon("type/arrow", 22);
		ICON_DENSITY = IconLoader.getIcon("type/density", 22);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof SimulationSerie) {
			SerieSource type = ((SimulationSerie) value).getSource();
			switch (type) {
			case GLOBAL:
				setIcon(ICON_GLOBAL);
				break;
			case CONTAINER:
				setIcon(ICON_CONTAINER);
				break;
			case PARAMETER:
				setIcon(ICON_PARAMETER);
				break;
			case FLOW:
				setIcon(ICON_ARROW);
				break;
			case DENSITY_CONTAINER:
				setIcon(ICON_DENSITY);
				break;

			default:
				setIcon(null);
			}

		} else if (value instanceof String) {
			setIcon(ICON_TIME);
		}

		return this;
	}
}
