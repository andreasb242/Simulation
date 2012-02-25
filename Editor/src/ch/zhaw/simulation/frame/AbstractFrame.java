package ch.zhaw.simulation.frame;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.icon.IconSVG;

public class AbstractFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private FrameSidebar sidebar = new FrameSidebar();

	public AbstractFrame(AbstractEditorControl editor) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(IconSVG.getIcon("simulation", 128).getImage());

		add(BorderLayout.EAST, sidebar);
	}

	public FrameSidebar getSidebar() {
		return sidebar;
	}
}
