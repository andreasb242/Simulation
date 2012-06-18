package ch.zhaw.simulation.sysintegration;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class SysMenuShortcuts {
	protected int getCommandModifier() {
		return KeyEvent.CTRL_MASK;
	}

	public KeyStroke getSimulationSimulation() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
	}

	public KeyStroke getSimulationMathconsole() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_M, getCommandModifier());
	}

	public KeyStroke getHelpHelp() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
	}

	public KeyStroke getHelpAbout() {
		return null;
	}

	public KeyStroke getEditUndo() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_Z, getCommandModifier());
	}

	public KeyStroke getEditRedo() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_Y, getCommandModifier());
	}

	public KeyStroke getFileNew() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_N, getCommandModifier());
	}

	public KeyStroke getEditCopy() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_C, getCommandModifier());
	}

	public KeyStroke getEditPaste() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_V, getCommandModifier());
	}

	public KeyStroke getEditCut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_X, getCommandModifier());
	}

	public KeyStroke getEditSearch() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F, getCommandModifier());
	}

	public KeyStroke getFileOpen() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_O, getCommandModifier());
	}

	public KeyStroke getFileSave() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_S, getCommandModifier());
	}

	public KeyStroke getFileSaveAs() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_S, getCommandModifier() | KeyEvent.SHIFT_MASK);
	}

	public KeyStroke getFileTakeSnapshot() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_T, getCommandModifier());
	}

	public KeyStroke getFileExitApplication() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK);
	}

	public KeyStroke getFileClose() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_W, getCommandModifier());
	}

	public KeyStroke getEditSelectAll() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_A, getCommandModifier());
	}

	public KeyStroke getEditDelete() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
	}

	public KeyStroke getLayoutBottom() {
		return null;
	}

	public KeyStroke getLayoutTop() {
		return null;
	}

	public KeyStroke getLayoutLeft() {
		return null;
	}

	public KeyStroke getLayoutRight() {
		return null;
	}

	public KeyStroke getLayoutCenterVertical() {
		return null;
	}

	public KeyStroke getLayoutCenterHorizontal() {
		return null;
	}

	public KeyStroke getEditSettings() {
		return null;
	}

	public KeyStroke getViewSidebar() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
	}

	public KeyStroke getFormulaOverview() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_I, getCommandModifier());
	}

	public KeyStroke getMathOverview() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_T, getCommandModifier());
	}
}
