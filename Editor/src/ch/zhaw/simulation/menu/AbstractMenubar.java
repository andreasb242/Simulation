package ch.zhaw.simulation.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.menu.actions.MenuAction;
import ch.zhaw.simulation.menu.actions.MenuActionType;
import ch.zhaw.simulation.sidebar.SidebarListener;
import ch.zhaw.simulation.sysintegration.SysMenuShortcuts;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.UndoListener;

/**
 * The menubar
 * 
 * @author Andreas Butti
 */
public class AbstractMenubar extends MenuActionHandler implements UndoListener, SidebarListener {
	/**
	 * The menubar
	 */
	private JMenuBar mb = new JMenuBar();

	/**
	 * Menu file
	 */
	private JMenu mFile;

	/**
	 * Menu view
	 */
	private JMenu mView;

	/**
	 * Menu edit
	 */
	private JMenu mEdit;

	/**
	 * Menu simulation
	 */
	private JMenu mSimulation;

	/**
	 * Menu help
	 */
	private JMenu mHelp;

	/**
	 * Redo menuitem
	 */
	private JMenuItem mRedo;

	/**
	 * Undo menuitem
	 */
	private JMenuItem mUndo;

	/**
	 * Layout menu
	 */
	private JMenu mLayout = new JMenu("Layout");

	/**
	 * Recent menu
	 */
	private JMenu recentMenu;

	/**
	 * The shortcuts for a specific command
	 */
	private SysMenuShortcuts sysmenu;

	/**
	 * The undo redo handle, to read the undo redo state
	 */
	private UndoHandler um;

	/**
	 * true if this is the main menu with all menupoints or false if this is a
	 * submenu with only a part of
	 */
	private boolean mainMenu;

	private ClipboardInterface clipboard;

	private JCheckBoxMenuItem sidebar;

	public AbstractMenubar(Sysintegration sysintegration, UndoHandler um, ClipboardInterface clipboard) {
		this.sysmenu = sysintegration.getMenu();
		this.um = um;
		this.clipboard = clipboard;

		if (um == null) {
			throw new NullPointerException("um == null");
		}

		if (this.sysmenu == null) {
			throw new NullPointerException("this.sysmenu == null");
		}

		if (clipboard == null) {
			throw new NullPointerException("clipboard == null");
		}

	}

	/**
	 * Initialize the menus
	 * 
	 * @param recentMenu
	 *            The recent menu
	 * @param mainMenu
	 *            true if this is the main menu, or false if this is a sub
	 *            editor without load save etc.
	 */
	public void initMenusToolbar(JMenu recentMenu, boolean mainMenu) {
		this.recentMenu = recentMenu;
		this.mainMenu = mainMenu;

		initFileMenu();
		initEditMenu();
		initViewMenu();

		initSimulationMenu();

		initHelpMenu();
	}

	private void initSimulationMenu() {
		mSimulation = new JMenu("Simulation");
		mSimulation.setMnemonic('S');

		addMenuItem(mSimulation, "Zeitsimulation", "start-simulation", MenuActionType.START_SIMULATION, sysmenu.getSimulationSimulation());
		addMenuItem(mSimulation, "Forml Übersicht", "overview", MenuActionType.FORMULA_OVERVIEW, sysmenu.getFormulaOverview());
		mSimulation.addSeparator();

		addMenuItem(mSimulation, "Mathekonsole", "math", MenuActionType.SHOW_MATH_CONSOLE, sysmenu.getSimulationMathconsole());
		mb.add(mSimulation);
	}

	private void initViewMenu() {
		mView = new JMenu("Ansicht");
		mView.setMnemonic('A');

		JMenu laf = new JMenu("Look & Feel");
		laf.setIcon(IconSVG.getIcon("style"));

		addMenuItem(laf, "System LAF", "system", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("sys");
			}
		}, null);

		addMenuItem(laf, "Nimbus", "Java-Logo", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("nimbus");
			}
		}, null);

		addMenuItem(laf, "Java LAF", "Java-Logo", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("java");
			}
		}, null);

		mView.add(laf);

		this.sidebar = new JCheckBoxMenuItem("Seitenleiste");
		sidebar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuAction(MenuActionType.SHOW_SIDEBAR, sidebar.isSelected()));
			}
		});

		sidebar.setAccelerator(sysmenu.getViewSidebar());

		mView.add(sidebar);

		mb.add(mView);
	}

	@Override
	public void showSidebar(boolean show) {
		if (sidebar.isSelected() != show) {
			sidebar.setSelected(show);
		}
	}

	private void setLookAndFeel(String lookAndFeel) {
		fireMenuActionPerformed(new MenuAction(MenuActionType.LOOK_AND_FEEL_CHANGED, lookAndFeel));
	}

	private void initHelpMenu() {
		mHelp = new JMenu("Hilfe");
		mHelp.setMnemonic('H');

		addMenuItem(mHelp, "Hilfe", "help", MenuActionType.HELP, sysmenu.getHelpHelp());

		addMenuItem(mHelp, "Über", "help-about", MenuActionType.ABOUT, sysmenu.getHelpAbout());

		mb.add(mHelp);
	}

	@Override
	public void undoRedoUpdated() {
		mUndo.setEnabled(um.canUndo());
		mRedo.setEnabled(um.canRedo());

		if (um.canUndo()) {
			mUndo.setText(um.getUndoPresentationName());
		} else {
			mUndo.setText("Rückgängig");
		}

		if (um.canRedo()) {
			mRedo.setText(um.getRedoPresentationName());
		} else {
			mRedo.setText("Widerherstellen");
		}
	}

	private void initEditMenu() {
		mEdit = new JMenu("Bearbeiten");
		mEdit.setMnemonic('B');

		mUndo = new JMenuItem("Rückgängig", IconSVG.getIcon("edit-redo"));
		mUndo.setAccelerator(sysmenu.getEditUndo());
		mUndo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuAction(MenuActionType.UNDO));
			}
		});
		mEdit.add(mUndo);

		mRedo = new JMenuItem("Widerherstellen", IconSVG.getIcon("edit-undo"));
		mRedo.setAccelerator(sysmenu.getEditRedo());
		mRedo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuAction(MenuActionType.REDO));
			}
		});
		mEdit.add(mRedo);

		undoRedoUpdated();

		final JMenuItem mCut = addMenuItem(mEdit, "Ausschneiden", "edit-cut", MenuActionType.CUT, sysmenu.getEditCut());
		final JMenuItem mCopy = addMenuItem(mEdit, "Kopieren", "editcopy", MenuActionType.COPY, sysmenu.getEditCopy());
		final JMenuItem mPaste = addMenuItem(mEdit, "Einfügen", "editpaste", MenuActionType.PASTE, sysmenu.getEditPaste());

		mCopy.setEnabled(false);
		mPaste.setEnabled(false);
		mCut.setEnabled(false);

		clipboard.addListener(new ClipboardListener() {

			@Override
			public void pasteEnabled(boolean enabled) {
				mPaste.setEnabled(enabled);
			}

			@Override
			public void cutCopyEnabled(boolean enabled) {
				mCut.setEnabled(enabled);
				mCopy.setEnabled(enabled);
			}
		});

		mEdit.addSeparator();

		addMenuItem(mEdit, "Alles markieren", "edit-select-all", MenuActionType.SELECT_ALL, sysmenu.getEditSelectAll());
		addMenuItem(mEdit, "Löschen", "edit-delete", MenuActionType.DELETE_SELECTION, sysmenu.getEditDelete());

		mLayout.setIcon(IconSVG.getIcon("alignCenterVertical"));
		mEdit.add(mLayout);

		addMenuItem(mEdit, "Einstellungen", "preferences", MenuActionType.SETTINGS, sysmenu.getEditSettings());

		mb.add(mEdit);
	}

	public JMenu getMLayout() {
		return mLayout;
	}

	private void initFileMenu() {
		mFile = new JMenu("Datei");
		mFile.setMnemonic('D');

		addMenuItem(mFile, "Neu", "file-new", MenuActionType.NEW_FILE, sysmenu.getFileNew());
		mFile.addSeparator();

		addMenuItem(mFile, "Öffnen", "open", MenuActionType.OPEN_FILE, sysmenu.getFileOpen());
		mFile.add(recentMenu);
		mFile.addSeparator();

		addMenuItem(mFile, "Speichern", "save", MenuActionType.SAVE, sysmenu.getFileSave());
		addMenuItem(mFile, "Speichern unter", "save-as", MenuActionType.SAVE_AS, sysmenu.getFileSaveAs());
		mFile.addSeparator();

		addMenuItem(mFile, "Speichern als Bild", "photos", MenuActionType.SNAPSHOT, sysmenu.getFileTakeSnapshot());
		mFile.addSeparator();

		addMenuItem(mFile, "Beenden", "exit", MenuActionType.EXIT, sysmenu.getFileExitApplication());
		mb.add(mFile);
	}

	protected JMenuItem addMenuItem(JMenu menu, String name, String icon, final MenuActionType action, KeyStroke keyStroke) {
		return addMenuItem(menu, name, icon, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuAction(action));
			}
		}, keyStroke);
	}

	protected JMenuItem addMenuItem(JMenu menu, String name, String icon, final ActionListener listener, KeyStroke keyStroke) {

		JMenuItem it = new JMenuItem(name, IconSVG.getIcon(icon));
		it.addActionListener(listener);

		if (keyStroke != null) {
			it.setAccelerator(keyStroke);
		}

		menu.add(it);

		return it;
	}

	public JMenuBar getMenubar() {
		return mb;
	}
}
