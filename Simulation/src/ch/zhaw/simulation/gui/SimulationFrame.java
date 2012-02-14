package ch.zhaw.simulation.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import ch.zhaw.simulation.gui.configuration.Configurationpanel;
import ch.zhaw.simulation.gui.control.SidebarListener;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;

import butti.javalibs.config.Settings;
import butti.javalibs.config.WindowPositionSaver;

public class SimulationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private SimulationControl control;
	private DocumentView view;
	private Configurationpanel cpanel;

	public SimulationFrame(Settings settings, String openfile) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(IconSVG.getIcon("simulation", 128).getImage());

		control = new SimulationControl(this, settings);
		view = control.getView();

		cpanel = new Configurationpanel(control);

		control.setDocumentTitle(null);

		setLayout(new BorderLayout());

		JScrollPane documentScrolpanel = new JScrollPane(view);

		add(documentScrolpanel, BorderLayout.CENTER);
		add(cpanel, BorderLayout.EAST);

		documentScrolpanel.setBorder(BorderFactory.createEmptyBorder());

		initUi();

		setSize(800, 600);
		setLocationRelativeTo(null);

		new WindowPositionSaver(this);

		addWindowListener();

		if (openfile != null) {
			control.open(openfile);
		} else {
			control.openLastFile();
		}

		cpanel.setVisible(control.isSidebarVisible());

		control.addSidebarListener(new SidebarListener() {

			@Override
			public void showSidebar(boolean show) {
				cpanel.setVisible(show);
			}
		});

		setBackground(Color.WHITE);
	}

	private void addWindowListener() {
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (!control.exit()) {
					setVisible(true);
				}
			}

		});
	}

	public void initUi() {
		add(control.getToolbar(), BorderLayout.NORTH);
		add(control.getStatusBar(), BorderLayout.SOUTH);
		setJMenuBar(control.getMenuBar());
	}
}
