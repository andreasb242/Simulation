package ch.zhaw.simulation.gui.configuration;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPaneContainer;

import ch.zhaw.simulation.editor.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.elements.HideableSplitComponent;
import ch.zhaw.simulation.editor.elements.container.ContainerView;
import ch.zhaw.simulation.editor.elements.global.GlobalView;
import ch.zhaw.simulation.editor.elements.parameter.ParameterView;
import ch.zhaw.simulation.gui.control.DrawModusListener;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;


public class Configurationpanel extends JPanel implements DrawModusListener, HideableSplitComponent {
	private static final long serialVersionUID = 1L;

	private SelectionModel selectionModel;

	private ContainerConfiguration containerAttributes;
	private ParameterConfiguration parameterAttributes;
	private GlobalConfiguration globalAttributes;
	private FlowParameterConfiguration flowParameterAttributs;

	private JXTaskPaneContainer taskPaneContainer;

	private SimulationConfiguration simConfig;

	public Configurationpanel(SimulationControl control) {
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

		simConfig = new SimulationConfiguration(control);
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
			
			if(s instanceof GlobalView) {
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
