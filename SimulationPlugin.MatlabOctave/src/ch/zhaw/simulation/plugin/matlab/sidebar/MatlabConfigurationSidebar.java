package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.codegen.DormandPrinceCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.EulerCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.RungeKuttaCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.addon.AdaptiveStepCodeAddon;
import ch.zhaw.simulation.plugin.matlab.codegen.addon.FixedStepCodeAddon;
import ch.zhaw.simulation.plugin.sidebar.AdaptiveStepConfigurationPane;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;
import ch.zhaw.simulation.plugin.sidebar.FixedStepConfigurationPane;

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
	private JComboBox cbTimeType;

	public MatlabConfigurationSidebar(SimulationConfiguration config) {
		super(config);
	}

	@Override
	protected void initComponents() {
		Vector<NumericMethod> numericMethods;
		Vector<TimeStep> timeSteps;
		
		FixedStepConfigurationPane fixedStepConfigurationPane = new FixedStepConfigurationPane(this);
		AdaptiveStepConfigurationPane adaptiveStepConfigurationPane = new AdaptiveStepConfigurationPane(this);

		numericMethods = new Vector<NumericMethod>();
		numericMethods.add(new NumericMethod("Euler", new EulerCodeGenerator()));
		numericMethods.add(new NumericMethod("Runge-Kutta 4", new RungeKuttaCodeGenerator()));
		numericMethods.add(new NumericMethod("Cash–Karp", new DormandPrinceCodeGenerator()));
		numericMethods.add(new NumericMethod("Fehlberg", new DormandPrinceCodeGenerator()));
		numericMethods.add(new NumericMethod("Dormand–Prince", new DormandPrinceCodeGenerator()));

		add(new JLabel("Numerisches Verfahren"));
		cbNumericMethods = new JComboBox(numericMethods);
		add(cbNumericMethods);

		timeSteps = new Vector<TimeStep>();
		timeSteps.add(new TimeStep("fix", fixedStepConfigurationPane, new FixedStepCodeAddon()));
		timeSteps.add(new TimeStep("adaptiv", adaptiveStepConfigurationPane, new AdaptiveStepCodeAddon()));

		add(new JLabel("Zeitschritt"));
		cbTimeType = new JComboBox(timeSteps);
		cbTimeType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimeStep timeStep = getSelectedTimeStep();
				pane.remove();
				pane = timeStep.getPane();
				getSelectedNumericMethod().getCodeGenerator().setTimeStepCodeAddon(timeStep.getCodeAddon());
				pane.add();
			}
		});
		add(cbTimeType);

		// Default is 'fixed Step'
		pane = fixedStepConfigurationPane;
		pane.add();
	}

	public NumericMethod getSelectedNumericMethod() {
		return (NumericMethod) cbNumericMethods.getSelectedItem();
	}

	public TimeStep getSelectedTimeStep() {
		return (TimeStep) cbTimeType.getSelectedItem();
	}
}

