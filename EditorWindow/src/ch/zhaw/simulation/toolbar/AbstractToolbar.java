package ch.zhaw.simulation.toolbar;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;

import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionHandler;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarButton;

public abstract class AbstractToolbar extends MenuToolbarActionHandler implements ClipboardListener {
	protected GuiConfig config;
	protected Toolbar toolbar;

	/**
	 * If this is the toolbar of the main window or of an additional window
	 */
	private boolean mainToolbar;
	private ToolbarButton clipboardCut;
	private ToolbarButton clipboarCopy;
	private ToolbarButton clipboarPaste;

	/**
	 * 
	 * @param sys
	 * @param mainToolbar
	 *            If this is the toolbar of the main window or of an additional
	 *            window
	 */
	public AbstractToolbar(Sysintegration sys, boolean mainToolbar) {
		this.mainToolbar = mainToolbar;
		this.config = sys.getGuiConfig();

		toolbar = sys.createToolbar();
	}

	public static ImageIcon addShadow(BufferedImage icon) {
		BufferedImage img = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Image shadowImage = IconSVG.getIcon("iconShadow", icon.getWidth()).getImage();

		img.getGraphics().drawImage(shadowImage, 0, icon.getHeight() - shadowImage.getHeight(null), null);
		img.getGraphics().drawImage(icon, 0, 0, null);

		return new ImageIcon(img);
	}

	protected abstract void initCustomToolitems();

	public void initToolbar() {
		initCustomToolitems();

		if (mainToolbar) {
			addSeparator();

			toolbar.add(new ToolbarAction("Speichern", "save") {
				@Override
				public void actionPerformed(ActionEvent e) {
					fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.SAVE));
				}
			});
		}

		addSeparator();

		addUndoRedoButtons();

		addCopyPasteButtons();

		addSeparator();

		addLayoutToolbarItems();
	}

	private void addLayoutToolbarItems() {
		// JMenu menu = control.mb.getMLayout();
		//
		// addTb(menu, new ToolbarAction("Unten ausrichten", "alignBottom") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// layoutControl.layoutBottom();
		// }
		// });
		//
		// addTb(menu, new ToolbarAction("Oben ausrichten", "alingTop") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// layoutControl.layoutTop();
		// }
		// });
		//
		// addTb(menu, new ToolbarAction("Links ausrichten", "alingLeft") {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// layoutControl.layoutLeft();
		// }
		// });
		//
		// addTb(menu, new ToolbarAction("Rechts ausrichten", "alingRight") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// layoutControl.layoutRight();
		// }
		// });
		//
		// addTb(menu, new ToolbarAction("Horizontal zentrieren",
		// "alignCenterHorizontal") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// layoutControl.layoutCenterHorizontal();
		// }
		// });
		//
		// addTb(menu, new ToolbarAction("Vertikal zentrieren",
		// "alignCenterVertical") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// layoutControl.layoutCenterVertical();
		// }
		// });
	}

	private void addCopyPasteButtons() {

		this.clipboardCut = toolbar.add(new ToolbarAction("Ausschneiden", IconSVG.getIconShadow("edit-cut", 24)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.CUT));
			}
		});

		this.clipboarCopy = toolbar.add(new ToolbarAction("Kopieren", IconSVG.getIconShadow("editcopy", 24)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.COPY));
			}
		});

		this.clipboarPaste = toolbar.add(new ToolbarAction("Einfügen", IconSVG.getIcon("editpaste", 24)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.PASTE));
			}
		});

		clipboardCut.setEnabled(false);
		clipboarCopy.setEnabled(false);
		clipboarPaste.setEnabled(false);

	}

	@Override
	public void pasteEnabled(boolean enabled) {
		clipboarPaste.setEnabled(enabled);
	}

	@Override
	public void cutCopyEnabled(boolean enabled) {
		clipboardCut.setEnabled(enabled);
		clipboarCopy.setEnabled(enabled);
	}

	private void addUndoRedoButtons() {
		// final ToolbarButton undo = toolbar.add(new
		// ToolbarAction("Rückgängig", "edit-undo") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// control.undo();
		// }
		// });
		//
		// final ToolbarButton redo = toolbar.add(new
		// ToolbarAction("Widerherstellen", "edit-redo") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// control.redo();
		// }
		// });
		//
		// UndoListener listener = new UndoListener() {
		//
		// @Override
		// public void undoRedoUpdated() {
		// UndoManager um = control.getUndoManager();
		//
		// undo.setEnabled(um.canUndo());
		// redo.setEnabled(um.canRedo());
		//
		// if (um.canUndo()) {
		// undo.setText(um.getUndoPresentationName());
		// } else {
		// undo.setText("Rückgängig");
		// }
		//
		// if (um.canRedo()) {
		// redo.setText(um.getRedoPresentationName());
		// } else {
		// redo.setText("Widerherstellen");
		// }
		// }
		// };
		//
		// control.getUndoManager().addUndoListener(listener);
		// listener.undoRedoUpdated();

		addSeparator();
	}

	protected void addSeparator() {
		toolbar.addSeparator();
	}

	private ToolbarAction addTb(JMenu menu, ToolbarAction a) {
		toolbar.add(a);

		menu.add(a.getMenuItem());

		return a;
	}

	public JComponent getToolbar() {
		return toolbar.getComponent();
	}

}
