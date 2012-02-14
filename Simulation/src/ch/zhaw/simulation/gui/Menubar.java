package ch.zhaw.simulation.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.undo.UndoManager;

import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.dialog.overview.OverviewWindow;
import ch.zhaw.simulation.gui.control.SidebarListener;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.math.console.MatrixConsole;
import ch.zhaw.simulation.sysintegration.SysMenuShortcuts;
import ch.zhaw.simulation.undo.UndoListener;

import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.util.RestartUtil;


public class Menubar implements UndoListener {
	private JMenuBar mb = new JMenuBar();
	private JMenu mFile;
	private JMenu mView;
	private JMenu mEdit;

	private JMenu mSimulation;

	private JMenu mHelp;

	private JMenuItem mRedo;
	private JMenuItem mUndo;

	private JMenu mLayout = new JMenu("Layout");

	private SimulationControl control;

	private JMenu recentMenu;

	private SysMenuShortcuts sysmenu;

	private OverviewWindow overviewWindow;

	public Menubar(SimulationControl control) {
		this.control = control;
		this.sysmenu = control.getSysintegration().getMenu();
	}

	public void initMenusToolbar(JMenu recentMenu) {
		this.recentMenu = recentMenu;

		initFileMenu();
		initEditMenu();
		initViewMenu();

		initSimulationMenu();

		initHelpMenu();

		control.getUndoManager().addUndoListener(this);
	}

	private void initSimulationMenu() {
		mSimulation = new JMenu("Simulation");
		mSimulation.setMnemonic('S');

		addMenuItem(mSimulation, "Zeitsimulation", "start-simulation", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.startSimulation();
			}
		}, sysmenu.getSimulationSimulation());

		addMenuItem(mSimulation, "Forml Übersicht", "overview", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (overviewWindow == null) {
					overviewWindow = new OverviewWindow(control.getParent(), control);
				}
				overviewWindow.showWindow();
			}
		}, sysmenu.getFormulaOverview());

		mSimulation.addSeparator();

		addMenuItem(mSimulation, "Mathekonsole", "math", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new MatrixConsole().setVisible(true);
			}
		}, sysmenu.getSimulationMathconsole());

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

		final JCheckBoxMenuItem sidebar = new JCheckBoxMenuItem("Seitenleiste");
		sidebar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.setSidebarVisible(sidebar.isSelected());
			}
		});
		sidebar.setSelected(control.isSidebarVisible());

		control.addSidebarListener(new SidebarListener() {

			@Override
			public void showSidebar(boolean show) {
				if (sidebar.isSelected() != show) {
					sidebar.setSelected(show);
				}
			}
		});

		sidebar.setAccelerator(sysmenu.getViewSidebar());

		mView.add(sidebar);

		mb.add(mView);
	}

	private void setLookAndFeel(String lookAndFeel) {
		control.getSettings().setSetting("ui.look-and-feel", lookAndFeel);

		if (control.exit()) {
			if (!RestartUtil.restartApplication("startup.Startup")) {
				Messagebox msg = new Messagebox(null, "Neu Starten", "<html>Das Programm konnte nicht neu gstartet werden.<br>"
						+ "Es wird jetzt beendet, bitte starten Sie es manuell neu.</html>", Messagebox.ERROR);
				msg.addButton("OK", 0);
				msg.display();
			}
		}
	}

	private void initHelpMenu() {
		mHelp = new JMenu("Hilfe");
		mHelp.setMnemonic('H');

		addMenuItem(mHelp, "Hilfe", "help", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.help();
			}
		}, sysmenu.getHelpHelp());

		addMenuItem(mHelp, "Über", "help-about", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.about();
			}
		}, sysmenu.getHelpAbout());

		mb.add(mHelp);
	}

	@Override
	public void undoRedoUpdated() {
		UndoManager um = control.getUndoManager();

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
				control.undo();
			}
		});
		mEdit.add(mUndo);

		mRedo = new JMenuItem("Widerherstellen", IconSVG.getIcon("edit-undo"));
		mRedo.setAccelerator(sysmenu.getEditRedo());
		mRedo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.redo();
			}
		});
		mEdit.add(mRedo);

		undoRedoUpdated();

		final JMenuItem mCut = addMenuItem(mEdit, "Ausschneiden", "edit-cut", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.getClipboard().cut();
			}
		}, sysmenu.getEditCut());

		final JMenuItem mCopy = addMenuItem(mEdit, "Kopieren", "editcopy", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.getClipboard().copy();
			}
		}, sysmenu.getEditCopy());

		final JMenuItem mPaste = addMenuItem(mEdit, "Einfügen", "editpaste", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.getClipboard().paste();
			}
		}, sysmenu.getEditPaste());

		mCopy.setEnabled(false);
		mPaste.setEnabled(false);
		mCut.setEnabled(false);

		control.getClipboard().addListener(new ClipboardListener() {

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

		addMenuItem(mEdit, "Alles markieren", "edit-select-all", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.selectAll();
			}
		}, sysmenu.getEditSelectAll());

		addMenuItem(mEdit, "Löschen", "edit-delete", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.deleteSelected();
			}
		}, sysmenu.getEditDelete());

		mLayout.setIcon(IconSVG.getIcon("alignCenterVertical"));
		mEdit.add(mLayout);

		addMenuItem(mEdit, "Einstellungen", "preferences", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.settings();
			}
		}, sysmenu.getEditSettings());

		mb.add(mEdit);
	}

	public JMenu getMLayout() {
		return mLayout;
	}

	private void initFileMenu() {
		mFile = new JMenu("Datei");
		mFile.setMnemonic('D');

		addMenuItem(mFile, "Neu", "file-new", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.newFile();
			}
		}, sysmenu.getFileNew());

		mFile.addSeparator();

		addMenuItem(mFile, "Öffnen", "open", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ggf. native filechooser, daher invokeLater
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						control.open();
					}
				});
			}
		}, sysmenu.getFileOpen());

		mFile.add(recentMenu);

		mFile.addSeparator();

		addMenuItem(mFile, "Speichern", "save", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ggf. native filechooser, daher invokeLater
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						control.save();
					}
				});
			}
		}, sysmenu.getFileSave());

		addMenuItem(mFile, "Speichern unter", "save-as", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ggf. native filechooser, daher invokeLater
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						control.saveAs();
					}
				});
			}
		}, sysmenu.getFileSaveAs());

		mFile.addSeparator();
		addMenuItem(mFile, "Speichern als Bild", "photos", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ggf. native filechooser, daher invokeLater
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						control.takeSnapshot();
					}
				});
			}
		}, sysmenu.getFileTakeSnapshot());

		mFile.addSeparator();

		addMenuItem(mFile, "Beenden", "exit", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.exit();
			}
		}, sysmenu.getFileExitApplication());

		mb.add(mFile);
	}

	private JMenuItem addMenuItem(JMenu menu, String name, String icon, final ActionListener listener, KeyStroke keyStroke) {

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
