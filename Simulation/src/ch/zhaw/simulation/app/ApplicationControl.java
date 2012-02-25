package ch.zhaw.simulation.app;

import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.gui.SimulationFrame;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.math.console.MatrixConsole;
import butti.javalibs.config.Settings;

public class ApplicationControl implements AppActionListener {

	private Settings settings;
	private SimulationFrame mainFrame;

	public ApplicationControl() {
	}

	public void start(Settings settings, String openfile) {
		this.settings = settings;

		this.mainFrame = new SimulationFrame(settings, openfile);

		// TODO: remove
		SimulationControl control = mainFrame.getControl();

		control.addAppActionListener(this);

		mainFrame.setVisible(true);

	}

	public void showAbout() {
		AboutDialog dlg = new AboutDialog(mainFrame);
		dlg.setVisible(true);
	}

	@Override
	public void actionPerformed(AppAction action) {
		switch (action) {
		case SHOW_ABOUT:
			showAbout();
			break;
		case SHOW_MATH_CONSOLE:
			new MatrixConsole().setVisible(true);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

}
