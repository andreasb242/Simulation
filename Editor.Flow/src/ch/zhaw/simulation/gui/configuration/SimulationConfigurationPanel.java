package ch.zhaw.simulation.gui.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.model.flow.simulation.PluginChangeListener;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
import ch.zhaw.simulation.sim.SimulationManager;

public class SimulationConfigurationPanel extends JXTaskPane implements ActionListener, PluginChangeListener {
	private static final long serialVersionUID = 1L;

	private JComboBox cbSimulationtype;
	private SimulationConfiguration model;

	private JButton btStart = new JButton("Simulieren");

	public SimulationConfigurationPanel(final FlowEditorControl control) {
		setTitle("Simulation");
		this.model = control.getModel().getSimulationConfiguration();
		model.addPluginChangeListener(this);

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
		model.setPlugin(cbSimulationtype.getSelectedItem().toString());
	}

	@Override
	public void pluginChanged(String plugin) {
		if (plugin == null) {
			return;
		}

		ComboBoxModel model = cbSimulationtype.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (plugin.equalsIgnoreCase(model.getElementAt(i).toString())) {
				cbSimulationtype.setSelectedIndex(i);
				break;
			}
		}
	}
}
