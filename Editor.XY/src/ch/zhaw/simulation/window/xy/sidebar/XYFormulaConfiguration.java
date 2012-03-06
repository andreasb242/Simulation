package ch.zhaw.simulation.window.xy.sidebar;

import javax.swing.JLabel;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;

public abstract class XYFormulaConfiguration extends NameFormulaConfiguration {
	private static final long serialVersionUID = 1L;

	private JLabel lbxDot = new JLabel("ẋ");
	private JLabel lbyDot = new JLabel("ẏ");
	
	public XYFormulaConfiguration(AbstractSimulationModel<?> model, SelectionModel selectionModel) {
		super(model, selectionModel);
		
		gbm.setX(0).setY(50).setWidth(3).setWeightY(0).setComp(lbxDot);
		gbm.setX(0).setY(51).setWidth(3).setWeightY(0).setComp(lbyDot);
	}
	

}
