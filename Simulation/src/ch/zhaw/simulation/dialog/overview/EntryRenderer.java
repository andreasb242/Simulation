package ch.zhaw.simulation.dialog.overview;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.global.GlobalImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationContainer;
import ch.zhaw.simulation.model.flow.SimulationGlobal;
import ch.zhaw.simulation.model.flow.SimulationParameter;
import ch.zhaw.simulation.model.flow.connection.FlowValve;

import butti.javalibs.controls.AbstractListCellRenderer;


public class EntryRenderer extends AbstractListCellRenderer<NamedSimulationObject> {

	private BufferedImage containerImage;
	private BufferedImage parameterImage;
	private BufferedImage flowParameterImage;
	private BufferedImage globalImage;

	public EntryRenderer(GuiConfig config) {
		super(35);
		globalImage = new GlobalImage(32, config).getImage(false);
		containerImage = new ContainerImage(32, 40, config).getImage(false);
		parameterImage = new ParameterImage(32, config).getImage(false);
		flowParameterImage = new FlowArrowImage(32, config).getImage(false);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void calculateMinWidth(NamedSimulationObject data) {
		setMinWidth(45, data.getName(), true);
		setMinWidth(45, data.getFormula(), false);
	}

	private BufferedImage getImage(NamedSimulationObject data) {
		if (data instanceof FlowValve) {
			return flowParameterImage;
		} else if (data instanceof SimulationContainer) {
			return containerImage;
		} else if (data instanceof SimulationParameter) {
			return parameterImage;
		} else if (data instanceof SimulationGlobal) {
			return globalImage;
		}
		return null;
	}

	@Override
	protected int calcHeight(NamedSimulationObject data) {
		String[] lines = data.getFormula().split("\n");
		return HEIGHT + 15 * lines.length;
	}

	@Override
	protected void paintData(Graphics2D g, NamedSimulationObject data) {
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
