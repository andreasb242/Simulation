package ch.zhaw.simulation.gui.codeditor;

import java.util.Vector;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;

public class FormulaEditorKit extends StyledEditorKit {

	private static final long serialVersionUID = 2969169649596107757L;
	private FormulaViewFactory xmlViewFactory;

	public FormulaEditorKit() {
		xmlViewFactory = new FormulaViewFactory();
	}

	@Override
	public ViewFactory getViewFactory() {
		return xmlViewFactory;
	}

	@Override
	public String getContentType() {
		return "text/xml";
	}

	public void setConsts(Constant[] constants, Function[] functions, Vector<AbstractNamedSimulationData> parameter, Vector<SimulationGlobalData> globals) {
		xmlViewFactory.setConsts(constants, functions, parameter, globals);
	}
}
