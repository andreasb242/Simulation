package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;

/**
 * @author: bachi
 */
public class NumericMethod {
	
	private String name;
	private DefaultConfigurationPane pane;
	private AbstractCodeGenerator codeGenerator;
	
	public NumericMethod(String name, DefaultConfigurationPane pane, AbstractCodeGenerator codeGenerator) {
		this.name = name;
		this.pane = pane;
		this.codeGenerator = codeGenerator;
	}
	
	public String getName() {
		return name;
	}

	public DefaultConfigurationPane getPane() {
		return pane;
	}

	public AbstractCodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	@Override
	public String toString() {
		return name;
	}
}
