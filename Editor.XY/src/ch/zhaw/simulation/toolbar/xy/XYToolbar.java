package ch.zhaw.simulation.toolbar.xy;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.xy.element.MesoImage;
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
		MesoImage mesoimg = new MesoImage(24, config);
		ImageIcon mesoIcon = addShadow(GuiImage.drawToImage(mesoimg));

		toolbar.add(new ToolbarAction("Meso Kompartment (m)", mesoIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.XY_ADD_MESO));
			}
		});

		addGlobalIcon();
		addTextIcon();
	}
}
