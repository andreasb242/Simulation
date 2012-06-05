package ch.zhaw.simulation.diagram;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.plugin.data.SimulationSerie.SerieSource;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class SerieCbRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	private TypeIcons icons;
	private Icon timeIcon;
	private String timeText;

	public static final Object TIME_ENTRY = new Object();

	public SerieCbRenderer(Sysintegration sys) {
		this.icons = new TypeIcons(sys);
		this.timeIcon = this.icons.ICON_TIME;
		this.timeText = "Zeit";
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof SimulationSerie) {
			SerieSource type = ((SimulationSerie) value).getSource();
			setIcon(this.icons.iconFor(type));

		} else if (value == TIME_ENTRY) {
			setText(timeText);
			setIcon(timeIcon);
		}

		return this;
	}

	public Icon getTimeIcon() {
		return timeIcon;
	}

	public void setTimeIcon(Icon timeIcon) {
		this.timeIcon = timeIcon;
	}

	public String getTimeText() {
		return timeText;
	}

	public void setTimeText(String timeText) {
		this.timeText = timeText;
	}

}
