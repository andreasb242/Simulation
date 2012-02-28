package ch.zhaw.simulation.toolbar.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.toolbar.AbstractToolbar;

public class FlowToolbar extends AbstractToolbar {

	public FlowToolbar(Sysintegration sys, boolean mainToolbar) {
		super(sys, mainToolbar);
	}

	public static Icon drawArrowIcon(GuiConfig config) {
		BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);

		Color color = config.getConnectorLineColor();

		Graphics2D g = (Graphics2D) img.getGraphics();

		DrawHelper.antialisingOn(g);

		g.setColor(color);

		int x1 = 0;
		int y1 = 12;
		int x2 = 24;
		int y2 = 12;

		g.drawLine(x1, y1, x2, y2);
		ParameterConnectorUi.drawArrow(g, x1, y1, x2, y2);

		return addShadow(img);
	}

	@Override
	protected void initCustomToolitems() {
		ImageIcon parameterIcon = addShadow(new ParameterImage(24, config).getImage(false));

		toolbar.add(new ToolbarAction("Parameter (p)", parameterIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_PARAMETER));
			}
		});

		ImageIcon containerIcon = addShadow(new ContainerImage(18, 24, config).getImage(false));

		toolbar.add(new ToolbarAction("Container (c)", containerIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_CONTAINER));
			}
		});

		addGlobalIcon();

		toolbar.add(new ToolbarAction("Verbindung", drawArrowIcon(config)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_CONNECTOR));
			}
		});

		ImageIcon flowArrowIcon = addShadow(new FlowArrowImage(24, config).getImage(false));

		toolbar.add(new ToolbarAction("Fluss", flowArrowIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_FLOW));
			}
		});

		addTextIcon();
	}

}
