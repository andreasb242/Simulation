package ch.zhaw.simulation.gui.configuration.codeditor;

import java.util.Vector;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationGlobal;


public class FormulaViewFactory extends Object implements ViewFactory {

	private FormulaView view;

	public FormulaViewFactory() {
	}
	
	/**
	 * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
	 */
	public View create(Element element) {
		view = new FormulaView(element);
		return view;
	}

	public void setConsts(Constant[] constants, Function[] functions, Vector<NamedSimulationObject> parameter, Vector<SimulationGlobal> globals) {
		view.setHilighter(constants, functions, parameter, globals);
	}

}
