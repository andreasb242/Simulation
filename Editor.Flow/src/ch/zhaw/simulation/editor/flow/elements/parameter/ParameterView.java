package ch.zhaw.simulation.editor.flow.elements.parameter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;

public class ParameterView extends GuiDataTextElement<SimulationParameterData> {
	private static final long serialVersionUID = 1L;
	private ParameterImage image;

	public ParameterView(int size, FlowEditorControl control, SimulationParameterData data) {
		super(data, control);

		image = new ParameterImage(size, control.getSysintegration().getGuiConfig());

		textY = 30;
		questionmarkY = 45;
	}
	

	@Override
	public void paint(Graphics g1) {
		// TODO DEBUG: DELETE METHOD
		
		Graphics2D g = DrawHelper.antialisingOn(g1);
		if (name == null) {
			recalcFontMetrics(g);
		}

		image.drawBackground(g, isSelected());

		g.setColor(Color.BLACK);

		g.drawString(name, textX, textY);

		if (!getData().getStaus().equals(AbstractNamedSimulationData.Status.SYNTAX_OK)) {
			g.setColor(Color.RED);

			int x = (getWidth() - g.getFontMetrics().stringWidth("?")) / 2;
			g.drawString("?", x, questionmarkY);
		}
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}

}
