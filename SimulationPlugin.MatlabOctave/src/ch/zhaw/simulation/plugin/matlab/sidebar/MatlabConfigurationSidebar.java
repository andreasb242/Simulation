package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.codegen.*;
import ch.zhaw.simulation.plugin.sidebar.AdaptiveStepConfigurationPane;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;
import ch.zhaw.simulation.plugin.sidebar.FixedStepConfigurationPane;
import ch.zhaw.simulation.plugin.sidebar.XYConfigurationPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author: bachi
 */
public class MatlabConfigurationSidebar extends DefaultConfigurationSidebar {
	private static final long serialVersionUID = 1L;

	private JComboBox cbNumericMethods;

	public MatlabConfigurationSidebar(SimulationConfiguration config, SimulationType type) {
		super(config, type);
	}

	@Override
	protected void initComponents() {
		Vector<NumericMethod> numericMethods;

		FixedStepConfigurationPane fixedStepConfigurationPane = new FixedStepConfigurationPane(this);
		AdaptiveStepConfigurationPane adaptiveStepConfigurationPane = new AdaptiveStepConfigurationPane(this);
		XYConfigurationPane xyConfigurationPane = new XYConfigurationPane(this);

		numericMethods = new Vector<NumericMethod>();
		if (type == SimulationType.FLOW_SIMULATION) {
			numericMethods.add(new NumericMethod(NumericMethodType.EULER, fixedStepConfigurationPane, new EulerCodeGenerator()));
			numericMethods.add(new NumericMethod(NumericMethodType.RK4, fixedStepConfigurationPane, new RungeKuttaCodeGenerator()));
			numericMethods.add(new NumericMethod(NumericMethodType.FEHLBERG, adaptiveStepConfigurationPane, new FehlbergCodeGenerator()));
			numericMethods.add(new NumericMethod(NumericMethodType.CASH_KARP, adaptiveStepConfigurationPane, new CashKarpCodeGenerator()));
			numericMethods.add(new NumericMethod(NumericMethodType.DORMAND_PRINCE, adaptiveStepConfigurationPane, new DormandPrinceCodeGenerator()));
			pane = fixedStepConfigurationPane;
		} else if (type == SimulationType.XY_MODEL) {
			numericMethods.add(new NumericMethod(NumericMethodType.RK4, xyConfigurationPane, new XYCodeGenerator()));
			pane = xyConfigurationPane;
		}

		add(new JLabel("Numerisches Verfahren"));
		cbNumericMethods = new JComboBox(numericMethods);
		cbNumericMethods.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pane.remove();
				pane = getSelectedNumericMethod().getPane();
				pane.add();
			}
		});
		add(cbNumericMethods);

		pane.add();
	}

	public NumericMethod getSelectedNumericMethod() {
		return (NumericMethod) cbNumericMethods.getSelectedItem();
	}
}
