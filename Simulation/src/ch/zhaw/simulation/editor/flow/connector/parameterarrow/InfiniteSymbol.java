package ch.zhaw.simulation.editor.flow.connector.parameterarrow;


import java.awt.Graphics;

import javax.swing.ImageIcon;

import ch.zhaw.simulation.editor.flow.elements.GuiDataElement;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.model.flow.InfiniteData;


public class InfiniteSymbol extends GuiDataElement<InfiniteData> {
	private static final long serialVersionUID = 1L;
	private ImageIcon icon;
	private ParameterImage pi;
	
	public InfiniteSymbol(InfiniteData data, SimulationControl control) {
		super(data, control);
		setSize(50, 50);
		
		pi = new ParameterImage(50, control.getConfig());
		icon = IconSVG.getIcon("Infinite", 50);
	}

	@Override
	public void dispose() {
		icon = null;
		pi = null;
		super.dispose();
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(pi.getImage(isSelected()), 0, 0, this);
		g.drawImage(icon.getImage(), 0, 0, this);
	}
}
