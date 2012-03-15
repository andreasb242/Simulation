package ch.zhaw.simulation.dialog.overview;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.elements.global.GlobalImage;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

import butti.javalibs.controls.AbstractListCellRenderer;

public class EntryRenderer extends AbstractListCellRenderer<AbstractNamedSimulationData> {

	private BufferedImage containerImage;
	private BufferedImage parameterImage;
	private BufferedImage flowParameterImage;
	private BufferedImage globalImage;

	public EntryRenderer(GuiConfig config) {
		super(35);

		globalImage = GuiImage.drawToImage(new GlobalImage(32, config));
		containerImage = GuiImage.drawToImage(new ContainerImage(32, 40, config));
		parameterImage = GuiImage.drawToImage(new ParameterImage(32, config));
		flowParameterImage = GuiImage.drawToImage(new FlowArrowImage(32, config));
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void calculateMinWidth(AbstractNamedSimulationData data) {
		setMinWidth(45, data.getName(), true);
		setMinWidth(45, data.getFormula(), false);
	}

	private BufferedImage getImage(AbstractNamedSimulationData data) {
		if (data instanceof FlowValveData) {
			return flowParameterImage;
		} else if (data instanceof SimulationContainerData) {
			return containerImage;
		} else if (data instanceof SimulationParameterData) {
			return parameterImage;
		} else if (data instanceof SimulationGlobalData) {
			return globalImage;
		}
		return null;
	}

	@Override
	protected int calcHeight(AbstractNamedSimulationData data) {
		String[] lines = data.getFormula().split("\n");
		return HEIGHT + 15 * lines.length;
	}

	@Override
	protected void paintData(Graphics2D g, AbstractNamedSimulationData data) {
		setTitleFont();
		drawStringFilter(data.getName(), 40, 20, 0);

		BufferedImage img = getImage(data);
		if (img != null) {
			g.drawImage(img, 5, 5, this);
		}

		setDefaultFont();

		String[] lines = data.getFormula().split("\n");

		int y = 0;

		for (String s : lines) {
			drawStringFilter(s, 40, 40 + y, 0);
			y += 15;
		}
	}
}
