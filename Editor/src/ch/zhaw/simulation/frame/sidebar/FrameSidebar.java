package ch.zhaw.simulation.frame.sidebar;

import java.awt.Dimension;

import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * The sidebar of our editor
 * 
 * @author Andreas Butti
 */
public class FrameSidebar extends JXTaskPaneContainer {
	private static final long serialVersionUID = 1L;

	public FrameSidebar() {
	}

	@Override
	public Dimension getMinimumSize() {
		int h = super.getMinimumSize().height;
		return new Dimension(0, h);
	}

}
