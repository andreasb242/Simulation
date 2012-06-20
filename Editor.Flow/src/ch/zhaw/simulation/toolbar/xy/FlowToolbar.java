package ch.zhaw.simulation.toolbar.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.action.TargetableAction;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.density.DensityContainerImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.editor.util.ArrowDraw;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.toolbar.AbstractToolbar;

public class FlowToolbar extends AbstractToolbar {
	private static ArrowDraw arrowDraw = new ArrowDraw(20);
	private TargetableAction btGrid;

	public FlowToolbar(Sysintegration sys, boolean mainToolbar) {
		super(sys, mainToolbar);
	}

	public static Icon drawArrowIcon(GuiConfig config) {
		BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);

		Color color = config.getConnectorLineColor(false);

		Graphics2D g = (Graphics2D) img.getGraphics();

		DrawHelper.antialisingOn(g);

		g.setColor(color);

		int x1 = 0;
		int y1 = 12;
		int x2 = 24;
		int y2 = 12;

		g.drawLine(x1, y1, x2, y2);
		arrowDraw.drawArrow(g, x1, y1, x2, y2);

		return addShadow(img);
	}

	@Override
	protected void addLayoutToolbarItems() {
		this.btGrid = new TargetableAction("Raster zum ausrichten", IconLoader.getIcon("grid", toolbar.getDefaultIconSize())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemStateChanged(ItemEvent evt) {
				boolean gridVisible = evt.getStateChange() == ItemEvent.SELECTED;
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_SHOW_GRID, gridVisible));
			}
		};

		btGrid.setStateAction(true);

		toolbar.addToogleAction(btGrid);

		addSeparator();

		super.addLayoutToolbarItems();
	}

	public void setGridVisible(boolean visible) {
		this.btGrid.setSelected(visible);
	}

	@Override
	protected void initCustomToolitems() {
		ImageIcon parameterIcon = addShadow(GuiImage.drawToImage(new ParameterImage(24, config)));

		toolbar.add(new ToolbarAction("Parameter (p)", parameterIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_PARAMETER));
			}
		});

		ImageIcon containerIcon = addShadow(GuiImage.drawToImage(new ContainerImage(18, 24, config)));

		toolbar.add(new ToolbarAction("Container (c)", containerIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_CONTAINER));
			}
		});

		if (!this.mainToolbar) {
			ImageIcon densityIcon = addShadow(GuiImage.drawToImage(new DensityContainerImage(18, 24, config)));

			toolbar.add(new ToolbarAction("Dichte (d)", densityIcon) {
				@Override
				public void actionPerformed(ActionEvent e) {
					fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_DENSITY));
				}
			});
		}

		addGlobalIcon();

		toolbar.add(new ToolbarAction("Verbindung", drawArrowIcon(config)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_CONNECTOR));
			}
		});

		ImageIcon flowArrowIcon = addShadow(GuiImage.drawToImage(new FlowArrowImage(24, config)));

		toolbar.add(new ToolbarAction("Fluss", flowArrowIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_FLOW));
			}
		});

		addTextIcon();
	}

}
