package ch.zhaw.simulation.window.sidebar.config;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JLabel;

import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.window.sidebar.config.SidebarActionListener.SidebarAction;

public class FormulaConfigurationField extends SingleConfigurationField {
	public FormulaConfigurationField() {
	}

	@Override
	public void init(GroupLayout layout, SequentialGroup g, ParallelGroup layoutBoth, ParallelGroup leftGroup, ParallelGroup rightGroup) {
		JLabel lbValue = new JLabel("Wert");
		leftGroup.addComponent(lbValue);
		addComponent(lbValue);

		JButton btEdit = new JButton("bearbeiten", IconLoader.getIcon("text-editor", 24));
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lbValue).addComponent(btEdit));
		addComponent(btEdit);

		rightGroup.addComponent(btEdit);

		btEdit.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				fireActionPerformed(SidebarAction.SHOW_FORMULA_EDITOR, getData());
			}

		});

	}

	@Override
	public int getOrder() {
		return 200;
	}

	@Override
	protected boolean canHandleData(AbstractNamedSimulationData data) {
		if (data instanceof SimulationContainerData) {
			return true;
		}
		if (data instanceof SimulationParameterData) {
			return true;
		}
		if (data instanceof FlowValveData) {
			return true;
		}
		if (data instanceof SimulationGlobalData) {
			return true;
		}

		return false;
	}

	@Override
	protected void loadData() {
		// nothing to do, formula is loaded if you open the formula editor
	}
}
