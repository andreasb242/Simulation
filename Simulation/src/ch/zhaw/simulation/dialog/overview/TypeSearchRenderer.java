package ch.zhaw.simulation.dialog.overview;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ch.zhaw.simulation.editor.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.elements.global.GlobalImage;
import ch.zhaw.simulation.editor.elements.parameter.ParameterImage;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;

public class TypeSearchRenderer implements ListCellRenderer {
	private ImageIcon containerImage;
	private ImageIcon parameterImage;
	private ImageIcon flowParameterImage;
	private ImageIcon globalImage;
	private ListCellRenderer delegate;

	public TypeSearchRenderer(GuiConfig config, ListCellRenderer delegate) {
		this.delegate = delegate;
		globalImage = new ImageIcon(new GlobalImage(16, config).getImage(false));
		containerImage = new ImageIcon(new ContainerImage(16, 16, config).getImage(false));
		parameterImage = new ImageIcon(new ParameterImage(16, config).getImage(false));
		flowParameterImage = new ImageIcon(new FlowArrowImage(16, config).getImage(false));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel comp = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value.equals(SimulationGlobal.class)) {
			comp.setText("Global");
			comp.setIcon(globalImage);
		} else if (value.equals(SimulationParameter.class)) {
			comp.setText("Parameter");
			comp.setIcon(parameterImage);
		} else if (value.equals(SimulationContainer.class)) {
			comp.setText("Container");
			comp.setIcon(containerImage);
		} else if (value.equals(FlowParameterPoint.class)) {
			comp.setText("Fluss");
			comp.setIcon(flowParameterImage);
		} else {
			if (index != -1) {
				comp.setText("Kein Filter");
			}
			comp.setIcon(null);
		}

		return comp;
	}
}
