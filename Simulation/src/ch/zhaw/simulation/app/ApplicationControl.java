package ch.zhaw.simulation.app;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
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
		if (x == 2) {
			XYWindow win = new XYWindow(mainWindow);
			XYEditorControl control = new XYEditorControl(this, win, settings);
			win.init(control);
			win.addListener(control);

			this.mainFrame = win;

			win.setVisible(true);

		} else {

			FlowWindow win = new FlowWindow(mainWindow);
			FlowEditorControl control = new FlowEditorControl(this, win, settings);
			win.init(control);
			win.addListener(control);

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
