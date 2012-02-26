package ch.zhaw.simulation.app;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.filehandling.ImportPlugins;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.math.console.MatrixConsole;
import ch.zhaw.simulation.window.flow.FlowWindow;
import ch.zhaw.simulation.window.xy.XYWindow;

public class ApplicationControl implements SimulationApplication {

	/**
	 * The main frame
	 */
	private JFrame mainFrame;

	/**
	 * The settings
	 */
	private Settings settings;

	public ApplicationControl() {
	}

	public void start(Settings settings, String openfile) {
		this.settings = settings;

		if (settings == null) {
			throw new NullPointerException("settings == null");
		}

		boolean mainWindow = true;

		int x = 2;
		if (x == 1) {
			XYWindow win = new XYWindow(mainWindow);

			XYEditorControl control = new XYEditorControl(win, settings);
			win.init(control, control.getClipboard());

			this.mainFrame = win;

			win.setVisible(true);

		} else {

			FlowWindow win = new FlowWindow(mainWindow);
			FlowEditorControl control = new FlowEditorControl(this, win, settings);
			win.init(control, control.getClipboard());
			win.addListener(control);
			// control.getUndoManager().addUndoListener(l)
			// getUndoManager().addUndoListener(mb);

			// this.mainFrame = new FlowFrame(this, settings, openfile);

			this.mainFrame = win;
			mainFrame.setVisible(true);
		}
	}

	public Settings getSettings() {
		return settings;
	}

	public void showAboutDialog() {
		AboutDialog dlg = new AboutDialog(mainFrame);
		dlg.setVisible(true);
	}

	@Override
	public void showMathConsole() {
		new MatrixConsole().setVisible(true);
	}
}
