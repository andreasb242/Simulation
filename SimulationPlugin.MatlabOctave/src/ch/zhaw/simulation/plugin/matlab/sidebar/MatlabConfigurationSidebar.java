package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.NumericMethod;
import ch.zhaw.simulation.plugin.matlab.codegen.DormandPrinceCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.EulerCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.codegen.RungeKuttaCodeGenerator;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author: bachi
 */
public class MatlabConfigurationSidebar extends DefaultConfigurationSidebar implements ActionListener {

	Vector<NumericMethod> numericMethods;
	private JComboBox cbNnumericMethods;
	private NumericMethod selectedNumericMethod;

	public MatlabConfigurationSidebar(SimulationConfiguration config) {
		super(config);
	}

	@Override
	protected void initComponents() {
		StandartConfigurationPane standartConfigurationPane = new StandartConfigurationPane();

		numericMethods = new Vector<NumericMethod>();
		numericMethods.add(new NumericMethod("Euler", standartConfigurationPane, new EulerCodeGenerator()));
		numericMethods.add(new NumericMethod("Klassisch Runge-Kutta", standartConfigurationPane, new RungeKuttaCodeGenerator()));
		numericMethods.add(new NumericMethod("Dormand–Prince", new DormandPrinceConfigurationPane(), new DormandPrinceCodeGenerator()));

		add(new JLabel("Numerisches Verfahren"));
		cbNnumericMethods = new JComboBox(numericMethods);
		cbNnumericMethods.addActionListener(this);
		add(cbNnumericMethods);
		selectedNumericMethod = getSelectedNumericMethod(); 
		add(selectedNumericMethod.getPane());
	}

	public NumericMethod getSelectedNumericMethod() {
		return (NumericMethod) cbNnumericMethods.getSelectedItem();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		remove(selectedNumericMethod.getPane());
		selectedNumericMethod = getSelectedNumericMethod();
		add(selectedNumericMethod.getPane());
	}
}

