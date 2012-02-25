package ch.zhaw.simulation.app;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.gui.FlowFrame;
import ch.zhaw.simulation.math.console.MatrixConsole;

public class ApplicationControl implements SimulationApplication {

	private FlowFrame mainFrame;

	public ApplicationControl() {
	}

	public void start(Settings settings, String openfile) {
		this.mainFrame = new FlowFrame(this, settings, openfile);
		mainFrame.setVisible(true);
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
