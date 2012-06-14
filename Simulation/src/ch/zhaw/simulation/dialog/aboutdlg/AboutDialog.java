package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.util.gui.GradientPanel;

/**
 * Der Aboutdialog
 * 
 * @author Andreas Butti
 */
public class AboutDialog extends BDialog {
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabs;

	public AboutDialog(JFrame parent) {
		super(parent);
		setTitle("Info über (AB)² Simulation");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		AboutHeader header = new AboutHeader();
		add(header, BorderLayout.NORTH);

		GradientPanel panel = new GradientPanel();
		panel.setLayout(new BorderLayout(0, 0));
		add(panel, BorderLayout.CENTER);

		this.tabs = new JTabbedPane();
		panel.add(tabs);

		addTab("(AB)² Simulation", new InformationPanel());
		addTab("Libraries", new LibraryPanel());
		addTab("System", new SystemPanel());

		pack();
		setLocationRelativeTo(parent);
	}

	private void addTab(String name, JPanel p) {
		JPanel contents = new JPanel();
		contents.setOpaque(false);
		contents.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		contents.add(p);
		tabs.addTab(name, contents);
	}
}
