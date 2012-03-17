package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;

/**
 * @author: bachi
 */
public class NumericMethod {
	
	private String name;
	private AbstractCodeGenerator codeGenerator;
	
	public NumericMethod(String name, AbstractCodeGenerator codeGenerator) {
		this.name = name;
		this.codeGenerator = codeGenerator;
	}
	
	public String getName() {
		return name;
	}

	public AbstractCodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	@Override
	public String toString() {
		return name;
	}
}
