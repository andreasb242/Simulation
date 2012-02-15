package ch.zhaw.simulation.gui.configuration;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;


import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.model.simulation.SimulationModel;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;
import ch.zhaw.simulation.sim.SimulationManager;


public class SimulationConfiguration extends JXTaskPane implements ActionListener, SimulationParameterListener, FocusListener {
	private static final long serialVersionUID = 1L;

	private JComboBox cbSimulationtype;
	private SimulationModel model;

	private JButton btStart = new JButton("Simulieren");

	private boolean dontAcceptEvents = false;

	public SimulationConfiguration(final SimulationControl control) {
		setTitle("Simulation");
		this.model = control.getModel().getSimModel();
		model.addSimulationParameterListener(this);

		add(new JLabel("Simulation"));

		SimulationManager manager = control.getManager();
		cbSimulationtype = new JComboBox(manager.getPlugins());

		cbSimulationtype.setSelectedItem(model.getPlugin());

		add(cbSimulationtype);

		btStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.startSimulation();
			}
		});
		add(btStart);
		btStart.setIcon(IconSVG.getIcon("start-simulation"));

		cbSimulationtype.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (cbSimulationtype.getSelectedItem() == null) {
			return;
		}
		dontAcceptEvents = true;
		model.setPlugin(cbSimulationtype.getSelectedItem().toString());
		dontAcceptEvents = false;
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
//		dontAcceptEvents = true;
//		double start = 1;
//		try {
//			start = startTime.getDoubleValue();
//
//			if (start < 0) {
//				start = 0;
//				startTime.setValue(0);
//			}
//			model.setStartTime(start);
//		} catch (Exception ex) {
//			startTime.setValue(model.getStartTime());
//		}
//
//		double end = 1;
//		try {
//			end = endTime.getDoubleValue();
//
//			if (end < 0) {
//				end = 1;
//				endTime.setValue(1);
//			}
//			model.setEndtime(end);
//		} catch (Exception ex) {
//			endTime.setValue(model.getEndTime());
//		}
//
//		double dt = 1;
//		try {
//			dt = dtTime.getDoubleValue();
//
//			if (dt <= 0) {
//				dt = 1;
//				dtTime.setValue(1);
//			}
//			model.setDt(dt);
//		} catch (Exception ex) {
//			dtTime.setValue(model.getDt());
//		}
//
//		dontAcceptEvents = false;
	}

	@Override
	public void pluginChanged(String plugin) {
		if(plugin == null) {
			return;
		}
		
		ComboBoxModel model = cbSimulationtype.getModel();
		for(int i = 0; i < model.getSize(); i++) {
			if(plugin.equalsIgnoreCase(model.getElementAt(i).toString())) {
				cbSimulationtype.setSelectedIndex(i);
				break;
			}
		}
	}

	@Override
	public void propertyChanged(String property, Object newValue) {
		// TODO Auto-generated method stub
		
	}
}
