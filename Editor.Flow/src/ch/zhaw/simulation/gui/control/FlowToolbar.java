package ch.zhaw.simulation.gui.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.undo.UndoManager;

import butti.javalibs.util.DrawHelper;

import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.global.GlobalImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarButton;
import ch.zhaw.simulation.toolbar.AbstractToolbar;
import ch.zhaw.simulation.undo.UndoListener;

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

//	public void initToolbar() {
//	
//
//		toolbar.add(new ToolbarAction("Speichern", "save") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				control.save();
//			}
//		});
//
//		addSeparator();
//
//		addUndoRedoButtons();
//
//		addCopyPasteButtons();
//
//		addSeparator();
//
//		addLayoutToolbarItems();
//	}
//
//	private void addLayoutToolbarItems() {
//		JMenu menu = control.mb.getMLayout();
//
//		addTb(menu, new ToolbarAction("Unten ausrichten", "alignBottom") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				layoutControl.layoutBottom();
//			}
//		});
//
//		addTb(menu, new ToolbarAction("Oben ausrichten", "alingTop") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				layoutControl.layoutTop();
//			}
//		});
//
//		addTb(menu, new ToolbarAction("Links ausrichten", "alingLeft") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				layoutControl.layoutLeft();
//			}
//		});
//
//		addTb(menu, new ToolbarAction("Rechts ausrichten", "alingRight") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				layoutControl.layoutRight();
//			}
//		});
//
//		addTb(menu, new ToolbarAction("Horizontal zentrieren", "alignCenterHorizontal") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				layoutControl.layoutCenterHorizontal();
//			}
//		});
//
//		addTb(menu, new ToolbarAction("Vertikal zentrieren", "alignCenterVertical") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				layoutControl.layoutCenterVertical();
//			}
//		});
//	}
//
//	private void addCopyPasteButtons() {
//
//		final ToolbarButton cut = toolbar.add(new ToolbarAction("Ausschneiden", IconSVG.getIconShadow("edit-cut", 24)) {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				control.getClipboard().cut();
//			}
//		});
//
//		final ToolbarButton copy = toolbar.add(new ToolbarAction("Kopieren", IconSVG.getIconShadow("editcopy", 24)) {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				control.getClipboard().copy();
//			}
//		});
//
//		final ToolbarButton paste = toolbar.add(new ToolbarAction("Einfügen", IconSVG.getIcon("editpaste", 24)) {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				control.getClipboard().paste();
//			}
//		});
//
//		cut.setEnabled(false);
//		copy.setEnabled(false);
//		paste.setEnabled(false);
//
//		control.getClipboard().addListener(new ClipboardListener() {
//
//			@Override
//			public void pasteEnabled(boolean enabled) {
//				paste.setEnabled(enabled);
//			}
//
//			@Override
//			public void cutCopyEnabled(boolean enabled) {
//				cut.setEnabled(enabled);
//				copy.setEnabled(enabled);
//			}
//		});
//	}
//
//	private void addUndoRedoButtons() {
//		final ToolbarButton undo = toolbar.add(new ToolbarAction("Rückgängig", "edit-undo") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				control.undo();
//			}
//		});
//
//		final ToolbarButton redo = toolbar.add(new ToolbarAction("Widerherstellen", "edit-redo") {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				control.redo();
//			}
//		});
//
//		UndoListener listener = new UndoListener() {
//
//			@Override
//			public void undoRedoUpdated() {
//				UndoManager um = control.getUndoManager();
//
//				undo.setEnabled(um.canUndo());
//				redo.setEnabled(um.canRedo());
//
//				if (um.canUndo()) {
//					undo.setText(um.getUndoPresentationName());
//				} else {
//					undo.setText("Rückgängig");
//				}
//
//				if (um.canRedo()) {
//					redo.setText(um.getRedoPresentationName());
//				} else {
//					redo.setText("Widerherstellen");
//				}
//			}
//		};
//
//		control.getUndoManager().addUndoListener(listener);
//		listener.undoRedoUpdated();
//
//		addSeparator();
//	}

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

		BufferedImage image = new GlobalImage(24, config).getImage(false);
		int height = image.getHeight();
		int width = image.getWidth();
		image = image.getSubimage(0, 0, width, height);

		Graphics g = image.getGraphics();

		DrawHelper.antialisingOn(g);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Sans", Font.BOLD, 16));
		g.drawString("G", 5, 19);

		g.dispose();

		ImageIcon globalIcon = addShadow(image);

		toolbar.add(new ToolbarAction("Global (g)", globalIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_GLOBAL));
			}
		});

		toolbar.add(new ToolbarAction("Verbindung", drawArrowIcon(config)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_CONNECTOR));

				// TODO !!!!
				
				//				control.cancelAllActions();
//				control.getSelectionModel().clearSelection();
//				control.getView().addConnectArrow();
			}
		});

		ImageIcon flowArrowIcon = addShadow(new FlowArrowImage(24, config).getImage(false));

		toolbar.add(new ToolbarAction("Fluss", flowArrowIcon) {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO !!!!
//				control.cancelAllActions();
//				control.getSelectionModel().clearSelection();
//				control.getView().addFlowArrow();

				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_FLOW));

			}
		});

		toolbar.add(new ToolbarAction("Text", "text") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.FLOW_ADD_TEXT));
			}
		});

		addSeparator();		
	}

}
