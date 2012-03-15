package ch.zhaw.simulation.toolbar.xy;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.xy.element.AtomImage;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.toolbar.AbstractToolbar;

public class XYToolbar extends AbstractToolbar {

	public XYToolbar(Sysintegration sys, boolean mainToolbar) {
		super(sys, mainToolbar);
	}

	@Override
	protected void initCustomToolitems() {
		AtomImage atomimg = new AtomImage(24, config);
		ImageIcon atomIcon = addShadow(GuiImage.drawToImage(atomimg));

		toolbar.add(new ToolbarAction("Atom (a)", atomIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.XY_ADD_ATOM));
			}
		});

		addGlobalIcon();
		addTextIcon();
	}
}
