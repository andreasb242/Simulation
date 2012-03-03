package ch.zhaw.simulation.editor.flow.connector.parameterarrow;


import java.awt.Graphics;

import javax.swing.ImageIcon;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.model.flow.element.InfiniteData;


public class InfiniteSymbol extends AbstractDataView<InfiniteData> {
	private static final long serialVersionUID = 1L;
	private ImageIcon icon;
	private ParameterImage pi;
	
	public InfiniteSymbol(InfiniteData data, FlowEditorControl control) {
		super(data, control);
		setSize(50, 50);
		
		pi = new ParameterImage(50, control.getSysintegration().getGuiConfig());
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
