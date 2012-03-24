package ch.zhaw.simulation.plugin.matlab.gui;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

/**
 * @author: bachi
 */
public class BusyDialog extends JDialog{

	private JXBusyLabel busyLabel;


	public BusyDialog(JFrame parent) {
		super(parent, true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		busyLabel = new JXBusyLabel(new Dimension(50, 50));
		busyLabel.setHorizontalAlignment(JXLabel.CENTER);
		busyLabel.setBusy(true);
		add(busyLabel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}
}
