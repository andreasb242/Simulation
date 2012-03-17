package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.zhaw.simulation.gui.codeditor.FormulaEditorPanel;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

import butti.javalibs.gui.GridBagManager;

public class EditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private FormulaEditorPanel editor;
	
	private GridBagManager gbm;

	private JTextField txtName = new JTextField();
	private JTextField txtDescription = new JTextField();

	private DensityData selected;

	private SimulationXYModel model;

	public EditorPanel(SimulationXYModel model, Sysintegration sys, FunctionHelp help) {
		gbm = new GridBagManager(this);
		this.model = model;
		
		Vector<String> vars = new Vector<String>();
		vars.add("x");
		vars.add("y");
		
		editor = new FormulaEditorPanel(sys, help, model, vars);

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Name"));
		gbm.setX(0).setY(1).setWeightY(0).setWeightX(0).setComp(new JLabel("Beschreibung"));

		gbm.setX(1).setY(0).setWeightY(0).setComp(txtName);
		gbm.setX(1).setY(1).setWeightY(0).setComp(txtDescription);

		gbm.setX(0).setWidth(2).setY(2).setComp(editor);
		
		FocusListener listener = new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				saveContents();
			}

		};

		txtName.addFocusListener(listener);
		txtDescription.addFocusListener(listener);
	}

	protected void saveContents() {
		if (this.selected != null) {
			selected.setName(txtName.getText());
			selected.setDescription(txtDescription.getText());
			model.densityChanged(selected);
		}
	}

	public void setSelected(DensityData selected) {
		saveContents();
		this.selected = selected;

		txtName.setText(selected.getName());
		txtDescription.setText(selected.getDescription());
		
		editor.setData(selected);
	}

}
