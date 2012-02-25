package ch.zhaw.simulation.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import butti.plugin.PluginDescription;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.flow.elements.HideableSplitComponent;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.global.GlobalView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.gui.control.DrawModusListener;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionListener;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;
import ch.zhaw.simulation.model.flow.simulation.PluginChangeListener;
import ch.zhaw.simulation.sim.SimulationManager;
import ch.zhaw.simulation.sim.SimulationPlugin;

public class Configurationpanel extends JPanel implements DrawModusListener, HideableSplitComponent {
	private static final long serialVersionUID = 1L;

	private SelectionModel selectionModel;

	// TODO: improve, use only one object for all settings or so
	private ContainerConfiguration containerAttributes;
	private ParameterConfiguration parameterAttributes;
	private GlobalConfiguration globalAttributes;
	private FlowParameterConfiguration flowParameterAttributs;

	private JXTaskPaneContainer taskPaneContainer;

	private SimulationConfigurationPanel simConfig;

	private JXTaskPane selectedSimulationSettings = null;

	public Configurationpanel(final SimulationControl control) {
		setLayout(new BorderLayout());

		taskPaneContainer = new JXTaskPaneContainer();

		containerAttributes = new ContainerConfiguration(control);
		parameterAttributes = new ParameterConfiguration(control);
		globalAttributes = new GlobalConfiguration(control);
		flowParameterAttributs = new FlowParameterConfiguration(control);

		// add this taskPane to the taskPaneContainer
		taskPaneContainer.add(containerAttributes);
		taskPaneContainer.add(parameterAttributes);
		taskPaneContainer.add(globalAttributes);
		taskPaneContainer.add(flowParameterAttributs);

		simConfig = new SimulationConfigurationPanel(control);
		taskPaneContainer.add(simConfig);

		add(taskPaneContainer, BorderLayout.CENTER);

		selectionModel = control.getSelectionModel();
		selectionModel.addSelectionListener(new SelectionListener() {

			@Override
			public void selectionChanged() {
				readValues();
			}

			@Override
			public void selectionMoved(int dX, int dY) {
			}
		});
		control.addDrawModusListener(this);

		control.getModel().getSimulationConfiguration().addPluginChangeListener(new PluginChangeListener() {

			@Override
			public void pluginChanged(String plugin) {
				if (selectedSimulationSettings != null) {
					taskPaneContainer.remove(selectedSimulationSettings);
				}

				SimulationManager manager = control.getManager();
				PluginDescription<SimulationPlugin> p = manager.getPluginByName(plugin);

				if (p != null) {
					JXTaskPane sidebar = p.getPlugin().getConfigurationSettingsSidebar();
					if (sidebar != null) {
						taskPaneContainer.add(sidebar);
						selectedSimulationSettings = sidebar;
					}
				}
			}
		});
	}

	private void readValues() {
		SelectableElement[] selected = selectionModel.getSelected();
		containerAttributes.noneSelected();
		parameterAttributes.noneSelected();
		globalAttributes.noneSelected();
		flowParameterAttributs.noneSelected();

		if (selected.length == 1) {
			SelectableElement s = selected[0];

			if (s instanceof ContainerView) {
				containerAttributes.setSelected(((ContainerView) s).getData());
				return;
			}

			if (s instanceof ParameterView) {
				parameterAttributes.setSelected(((ParameterView) s).getData());
				return;
			}

			if (s instanceof GlobalView) {
				globalAttributes.setSelected(((GlobalView) s).getData());
				return;
			}

			if (s instanceof FlowConnectorParameter) {
				flowParameterAttributs.setSelected(((FlowConnectorParameter) s).getData());
				return;
			}
		}

	}

	@Override
	public void drawModusEnabled() {
		simConfig.setVisible(false);
		setVisible(false);
	}

	@Override
	public void drawModusFinished() {
		simConfig.setVisible(true);
		setVisible(true);
	}

	@Override
	public Dimension getMinimumSize() {
		int h = super.getMinimumSize().height;
		return new Dimension(0, h);
	}

	@Override
	public int getRealMinWidht() {
		return super.getMinimumSize().width;
	}
}
