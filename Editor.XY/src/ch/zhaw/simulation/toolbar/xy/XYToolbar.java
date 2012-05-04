package ch.zhaw.simulation.toolbar.xy;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.xy.SubModelSelectionListener;
import ch.zhaw.simulation.editor.xy.element.meso.MesoImage;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarButton;
import ch.zhaw.simulation.toolbar.AbstractToolbar;

public class XYToolbar extends AbstractToolbar implements SubModelSelectionListener {

	private MesoImage mesoimg;
	private ToolbarButton mesoToolbarAction;

	public XYToolbar(Sysintegration sys, boolean mainToolbar) {
		super(sys, mainToolbar);
	}

	@Override
	protected void initCustomToolitems() {
		mesoimg = new MesoImage(24, config);
		ImageIcon mesoIcon = addShadow(GuiImage.drawToImage(mesoimg));

		mesoToolbarAction = toolbar.add(new ToolbarAction("Meso Kompartment (m)", mesoIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.XY_ADD_MESO));
			}
		});

		toolbar.add(new ToolbarAction("Submodel wählen", new DownIcon()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.XY_MESO_POPUP, e.getSource()));
			}
		});

		addGlobalIcon();
		addTextIcon();
	}

	@Override
	public void subModelSelected(SubModel submodel) {
		if (submodel != null) {
			mesoimg.setColor(submodel.getColor());
		} else {
			mesoimg.setColor(Color.WHITE);
		}
		ImageIcon mesoIcon = addShadow(GuiImage.drawToImage(mesoimg));
		mesoToolbarAction.setIcon(mesoIcon);
	}
}
