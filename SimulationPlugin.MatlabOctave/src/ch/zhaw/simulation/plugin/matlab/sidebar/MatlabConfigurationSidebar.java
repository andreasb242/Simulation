package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.codegen.*;
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

	public MatlabConfigurationSidebar(SimulationConfiguration config) {
		super(config);
	}

	@Override
	protected void initComponents() {
		Vector<NumericMethod> numericMethods;
		
		FixedStepConfigurationPane fixedStepConfigurationPane = new FixedStepConfigurationPane(this);
		AdaptiveStepConfigurationPane adaptiveStepConfigurationPane = new AdaptiveStepConfigurationPane(this);

		numericMethods = new Vector<NumericMethod>();
		numericMethods.add(new NumericMethod("Euler", fixedStepConfigurationPane, new EulerCodeGenerator()));
		numericMethods.add(new NumericMethod("Runge-Kutta 4", fixedStepConfigurationPane, new RungeKuttaCodeGenerator()));
		numericMethods.add(new NumericMethod("Cash–Karp", adaptiveStepConfigurationPane, new CashKarpCodeGenerator()));
		numericMethods.add(new NumericMethod("Fehlberg", adaptiveStepConfigurationPane, new FehlbergCodeGenerator()));
		numericMethods.add(new NumericMethod("Dormand–Prince", adaptiveStepConfigurationPane, new DormandPrinceCodeGenerator()));

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

		// Default is 'fixed Step'
		pane = fixedStepConfigurationPane;
		pane.add();
	}

	public NumericMethod getSelectedNumericMethod() {
		return (NumericMethod) cbNumericMethods.getSelectedItem();
	}
}

