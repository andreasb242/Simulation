
import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.icon.IconSVG;

public class XYFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public XYFrame(SimulationApplication app, Settings settings, String openfile) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(IconSVG.getIcon("simulation", 128).getImage());

	}

}
