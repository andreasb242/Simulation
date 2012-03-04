package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;

/**
 * @author: bachi
 */
public class NumericMethod {
	
	private String name;
	private JPanel pane;
	private AbstractCodeGenerator codeGenerator;
	
	public NumericMethod(String name, JPanel pane, AbstractCodeGenerator codeGenerator) {
		this.name = name;
		this.pane = pane;
		this.codeGenerator = codeGenerator;
	}
	
	public String getName() {
		return name;
	}

	public JPanel getPane() {
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
