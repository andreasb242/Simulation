package ch.zhaw.simulation.dialog.overview;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.elements.global.GlobalImage;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class TypeSearchRenderer implements ListCellRenderer {
	private ImageIcon containerImage;
	private ImageIcon parameterImage;
	private ImageIcon flowParameterImage;
	private ImageIcon globalImage;
	private ListCellRenderer delegate;

	public TypeSearchRenderer(GuiConfig config, ListCellRenderer delegate) {
		this.delegate = delegate;

		globalImage = new ImageIcon(GuiImage.drawToImage(new GlobalImage(16, config)));
		containerImage = new ImageIcon(GuiImage.drawToImage(new ContainerImage(16, 16, config)));
		parameterImage = new ImageIcon(GuiImage.drawToImage(new ParameterImage(16, config)));
		flowParameterImage = new ImageIcon(GuiImage.drawToImage(new FlowArrowImage(16, config)));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel comp = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value.equals(SimulationGlobalData.class)) {
			comp.setText("Global");
			comp.setIcon(globalImage);
		} else if (value.equals(SimulationParameterData.class)) {
			comp.setText("Parameter");
			comp.setIcon(parameterImage);
		} else if (value.equals(SimulationContainerData.class)) {
			comp.setText("Container");
			comp.setIcon(containerImage);
		} else if (value.equals(FlowValveData.class)) {
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
