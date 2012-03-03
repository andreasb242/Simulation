package ch.zhaw.simulation.window.flow.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
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
		this.model = control.getSimulationConfiguration();
		model.addPluginChangeListener(this);

		add(new JLabel("Simulation"));

		SimulationManager manager = control.getApp().getManager();
		cbSimulationtype = new JComboBox(manager.getPlugins());

		cbSimulationtype.setSelectedItem(model.getPlugin());

		if (cbSimulationtype.getSelectedIndex() < 0 && manager.getPlugins().size() > 0) {
			cbSimulationtype.setSelectedIndex(0);
		}
		actionPerformed(null);

		add(cbSimulationtype);

		btStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				control.menuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.START_SIMULATION));
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
