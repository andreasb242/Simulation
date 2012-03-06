package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.XYModel;

import butti.javalibs.gui.GridBagManager;

public class EditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private JTextField txtName = new JTextField();
	private JTextField txtDescription = new JTextField();

	private DensityData selected;

	public EditorPanel(final XYModel model) {
		gbm = new GridBagManager(this, true);

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Name"));
		gbm.setX(0).setY(1).setWeightY(0).setWeightX(0).setComp(new JLabel("Beschreibung"));

		gbm.setX(1).setY(0).setWeightY(0).setComp(txtName);
		gbm.setX(1).setY(1).setWeightY(0).setComp(txtDescription);

		FocusListener listener = new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if (selected != null) {
					selected.setName(txtName.getText());
					selected.setDescription(txtDescription.getText());
					model.densityChanged(selected);
				}
			}

		};

		txtName.addFocusListener(listener);
		txtDescription.addFocusListener(listener);
	}

	public void setSelected(DensityData selected) {
		this.selected = selected;

		txtName.setText(selected.getName());
		txtDescription.setText(selected.getDescription());
	}

}
