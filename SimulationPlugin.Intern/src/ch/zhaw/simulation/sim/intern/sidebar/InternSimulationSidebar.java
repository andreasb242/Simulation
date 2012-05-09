package ch.zhaw.simulation.sim.intern.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;
import ch.zhaw.simulation.sim.intern.InternSimulationParameter;

public class InternSimulationSidebar extends DefaultConfigurationSidebar implements InternSimulationParameter {
	private static final long serialVersionUID = 1L;

	private JComboBox cbType;

	public InternSimulationSidebar(SimulationConfiguration config, SimulationType type) {
		super(config, type);
	}

	@Override
	protected void initComponents() {
		cbType = new JComboBox(new String[] { "Euler", "Runge-Kutta" });

		add(new JLabel("Type"));
		add(cbType);
		cbType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				config.setParameter(TYPE, cbType.getSelectedIndex());
			}
		});

		super.initComponents();
	}

	@Override
	public void updateSidebar(SimulationType type) {
		if (type == SimulationType.FLOW_SIMULATION) {
			setVisible(true);
		} else {
			setVisible(false);
		}
	}

	@Override
	protected void loadDataFromModel() {
		super.loadDataFromModel();

		cbType.setSelectedIndex((int) config.getParameter(TYPE, 0));
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		super.propertyChanged(property, newValue);

		if (TYPE.equals(property)) {
			cbType.setSelectedIndex((int) newValue);
		}
	}
}
