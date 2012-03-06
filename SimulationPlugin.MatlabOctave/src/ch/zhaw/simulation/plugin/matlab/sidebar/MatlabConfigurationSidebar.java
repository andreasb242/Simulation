package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.MatlabParameter;
import ch.zhaw.simulation.plugin.matlab.NumericMethod;
import ch.zhaw.simulation.plugin.matlab.codegen.DormandPrinceCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.EulerCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.RungeKuttaCodeGenerator;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;
import ch.zhaw.simulation.plugin.sidebar.StepConfigurationPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author: bachi
 */
public class MatlabConfigurationSidebar extends DefaultConfigurationSidebar implements ActionListener, MatlabParameter {

	Vector<NumericMethod> numericMethods;
	private NumericMethod selectedNumericMethod;

	private JComboBox cbNumericMethods;

	public MatlabConfigurationSidebar(SimulationConfiguration config) {
		super(config);
	}

	@Override
	protected void initComponents() {
		DefaultConfigurationPane stepConfigurationPane = new StepConfigurationPane(this);
		DormandPrinceConfigurationPane dormandPrinceConfigurationPane = new DormandPrinceConfigurationPane(this);

		numericMethods = new Vector<NumericMethod>();
		numericMethods.add(new NumericMethod("Euler", stepConfigurationPane, new EulerCodeGenerator()));
		numericMethods.add(new NumericMethod("Klassisch Runge-Kutta", stepConfigurationPane, new RungeKuttaCodeGenerator()));
		numericMethods.add(new NumericMethod("Dormand–Prince", dormandPrinceConfigurationPane, new DormandPrinceCodeGenerator()));

		add(new JLabel("Numerisches Verfahren"));
		cbNumericMethods = new JComboBox(numericMethods);
		cbNumericMethods.addActionListener(this);
		add(cbNumericMethods);
		selectedNumericMethod = getSelectedNumericMethod();

		selectedPane = stepConfigurationPane;
		selectedPane.add();
	}

	@Override
	protected void loadDataFromModel() {
		super.loadDataFromModel();

		cbNumericMethods.setSelectedIndex((int) config.getParameter(NUMERIC_METHOD, 0));
	}

	public NumericMethod getSelectedNumericMethod() {
		return (NumericMethod) cbNumericMethods.getSelectedItem();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		selectedPane.remove();
		selectedNumericMethod = getSelectedNumericMethod();
		selectedPane = selectedNumericMethod.getPane();
		selectedPane.add();
	}
}

