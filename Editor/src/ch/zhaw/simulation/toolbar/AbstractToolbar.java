package ch.zhaw.simulation.toolbar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.elements.global.GlobalImage;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionHandler;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.UndoListener;

public abstract class AbstractToolbar extends MenuToolbarActionHandler implements ClipboardListener, UndoListener {
	protected GuiConfig config;
	protected Toolbar toolbar;

	/**
	 * If this is the toolbar of the main window or of an additional window
	 */
	protected boolean mainToolbar;

	private JButton clipboardCut;
	private JButton clipboarCopy;
	private JButton clipboarPaste;
	private UndoHandler undo;
	private JButton btUndo;
	private JButton btRedo;

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

		toolbar = sys.createToolbar(24);
	}

	public static ImageIcon addShadow(BufferedImage icon) {
		BufferedImage img = new BufferedImage(icon.getWidth(), icon.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Image shadowImage = IconLoader.getIcon("iconShadow", icon.getWidth()).getImage();

		img.getGraphics().drawImage(shadowImage, 0, icon.getHeight() - shadowImage.getHeight(null), null);
		img.getGraphics().drawImage(icon, 0, 0, null);

		return new ImageIcon(img);
	}

	protected abstract void initCustomToolitems();

	public void initToolbar(UndoHandler undo) {
		this.undo = undo;
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

	protected void addLayoutToolbarItems() {
		toolbar.add(new ToolbarAction("Unten ausrichten", "alignBottom") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.LAYOUT_BOTTOM));
			}
		});

		toolbar.add(new ToolbarAction("Oben ausrichten", "alignTop") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.LAYOUT_TOP));
			}
		});

		toolbar.add(new ToolbarAction("Links ausrichten", "alignLeft") {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.LAYOUT_LEFT));
			}
		});

		toolbar.add(new ToolbarAction("Rechts ausrichten", "alignRight") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.LAYOUT_RIGHT));
			}
		});

		toolbar.add(new ToolbarAction("Horizontal zentrieren", "alignCenterHorizontal") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.LAYOUT_CENTER_HORIZONTAL));
			}
		});

		toolbar.add(new ToolbarAction("Vertikal zentrieren", "alignCenterVertical") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.LAYOUT_CENTER_VERTICAL));
			}
		});
	}

	protected void addCopyPasteButtons() {
		this.clipboardCut = toolbar.add(new ToolbarAction("Ausschneiden", IconLoader.getIconShadow("edit-cut", 24)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.CUT));
			}
		});

		this.clipboarCopy = toolbar.add(new ToolbarAction("Kopieren", IconLoader.getIconShadow("editcopy", 24)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.COPY));
			}
		});

		this.clipboarPaste = toolbar.add(new ToolbarAction("Einfügen", IconLoader.getIcon("editpaste", 24)) {
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
		this.btUndo = toolbar.add(new ToolbarAction("Rückgängig", "edit-undo") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.UNDO));
			}
		});

		this.btRedo = toolbar.add(new ToolbarAction("Widerherstellen", "edit-redo") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.REDO));
			}
		});

		// refresh buttons
		undoRedoUpdated();

		addSeparator();
	}

	@Override
	public void undoRedoUpdated() {
		btUndo.setEnabled(this.undo.canUndo());
		btRedo.setEnabled(this.undo.canRedo());

		if (this.undo.canUndo()) {
			btUndo.setToolTipText(this.undo.getUndoPresentationName());
		} else {
			btUndo.setToolTipText("Rückgängig");
		}

		if (this.undo.canRedo()) {
			btRedo.setToolTipText(this.undo.getRedoPresentationName());
		} else {
			btRedo.setToolTipText("Widerherstellen");
		}
	}

	/**
	 * Creates the global toolbar item
	 */
	public void addGlobalIcon() {
		BufferedImage image = GuiImage.drawToImage(new GlobalImage(24, config));

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
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.EDITOR_ADD_GLOBAL));
			}
		});
	}

	/**
	 * Creates the text toolbar item
	 */
	protected void addTextIcon() {
		toolbar.add(new ToolbarAction("Text (t)", "text") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.EDITOR_ADD_TEXT));
			}
		});

	}

	protected void addSeparator() {
		toolbar.addSeparator();
	}

	public JComponent getToolbar() {
		return toolbar.getComponent();
	}

}
