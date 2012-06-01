package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;

/**
 * @author: bachi
 */
public class NumericMethod {

	private NumericMethodType type;
	private DefaultConfigurationPane pane;
	private AbstractCodeGenerator codeGenerator;
	
	public NumericMethod(NumericMethodType type, DefaultConfigurationPane pane, AbstractCodeGenerator codeGenerator) {
		this.type = type;
		this.pane = pane;
		this.codeGenerator = codeGenerator;
	}

	public NumericMethodType getType() {
		return type;
	}

	public DefaultConfigurationPane getPane() {
		return pane;
	}

	public AbstractCodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
