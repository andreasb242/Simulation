package ch.zhaw.simulation.dialog.aboutdlg;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.action.OpenBrowserAction;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.errorhandler.Errorhandler;

/**
 * Der Aboutdialog
 * 
 * @author Andreas Butti
 */
public class AboutDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;
	private int yCoord = 12;

	public AboutDialog(JFrame parent) {
		super(parent);
		setTitle("Info über Simulation");

		JPanel headerPanel = new JPanel(new BorderLayout());
		AboutHeader header = new AboutHeader();
		headerPanel.add(BorderLayout.NORTH, header);

		add(headerPanel, BorderLayout.NORTH);

		GradientPanel panel = new GradientPanel();
		add(panel, BorderLayout.CENTER);

		gbm = new GridBagManager(panel);

		JLabel lbSave = new TitleLabel("Speicher");

		gbm.setX(1).setY(6).setWeightY(0).setWidth(2).setComp(lbSave);

		String usedMem = formatMemoryUsage(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

		String allocMem = formatMemoryUsage(Runtime.getRuntime().totalMemory());

		String totalMem = formatMemoryUsage(Runtime.getRuntime().maxMemory());

		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(7).setWeightY(0).setComp(new JLabel("Benutzt:"));
		gbm.setX(2).setY(7).setWeightY(0).setComp(new JLabel(usedMem));

		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(8).setWeightY(0).setComp(new JLabel("Reserviert:"));
		gbm.setX(2).setY(8).setWeightY(0).setComp(new JLabel(allocMem));

		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(9).setWeightY(0).setComp(new JLabel("Max. verfügbar:"));
		gbm.setX(2).setY(9).setWeightY(0).setComp(new JLabel(totalMem));

		JLabel lbLib = new TitleLabel("Verwendete Libraries");

		gbm.setX(1).setY(11).setWeightY(0).setInsets(new Insets(30, 5, 5, 5)).setWidth(2).setComp(lbLib);

		addLib("SwingX (Sun)", "http://swinglabs.org/");
		addLib("SvgSalamander", "https://svgsalamander.dev.java.net/");
		addLib("Jep Java - Math Expression Parser", "http://sourceforge.net/projects/jep/");
		addLib("EPS Library", "http://www.abeel.be/epsgraphics/");
		addLib("JOpenChart", "http://jopenchart.sourceforge.net/");

		pack();
		setLocationRelativeTo(parent);
	}

	private void addLib(String name, String url) {
		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(yCoord).setWeightY(0).setComp(new JLabel(name));

		final OpenBrowserAction helpAction = new OpenBrowserAction();
		try {
			helpAction.setURI(new URI(url));
		} catch (Exception e) {
			Errorhandler.showError(e, "Konnte URL nicht öffnen");
		}

		JLabel lbUrl = new JLabel(url);
		lbUrl.setForeground(Color.BLUE);

		lbUrl.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				helpAction.actionPerformed(null);
			}

		});

		gbm.setX(2).setY(yCoord++).setWeightY(0).setComp(lbUrl);
	}

	/**
	 * Formatiert den long in Speichergrössen (MB, KB, B)
	 * 
	 * @param m
	 *            Speicher
	 * @return Formatierte Ausgabe
	 */
	public static String formatMemoryUsage(long m) {
		if (m > 1024 * 1024) {
			return (m / 1024 / 1024) + "MB";
		}
		if (m > 1024) {
			return (m / 1024) + "KB";
		}
		return m + "B";
	}
}
