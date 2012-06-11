package ch.zhaw.simulation.editor.xy.density;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import butti.javalibs.gui.GridBagManager;
import butti.javalibs.util.ColorConstants;
import ch.zhaw.simulation.gui.codeditor.FormulaEditorPanel;
import ch.zhaw.simulation.model.InvalidNameException;
import ch.zhaw.simulation.model.NameChecker;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class EditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private FormulaEditorPanel editor;

	private GridBagManager gbm;

	private NameChecker nameChecker = new NameChecker();
	private JTextField txtName = new JTextField();
	private JLabel lbNameError = new JLabel("!");
	private Color defaultBackground;

	private JTextField txtDescription = new JTextField();

	private DensityData selected;

	private SimulationXYModel model;

	public EditorPanel(SimulationXYModel model, Sysintegration sys) {
		gbm = new GridBagManager(this);
		this.model = model;

		lbNameError.setVisible(false);

		Vector<String> vars = new Vector<String>();
		vars.add("x");
		vars.add("y");

		editor = new FormulaEditorPanel(sys, model, vars, false);

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Name"));
		gbm.setX(0).setY(2).setWeightY(0).setWeightX(0).setComp(new JLabel("Beschreibung"));

		gbm.setX(1).setY(0).setWeightY(0).setComp(txtName);

		gbm.setX(1).setY(1).setWeightY(0).setComp(lbNameError);

		gbm.setX(1).setY(2).setWeightY(0).setComp(txtDescription);

		gbm.setX(0).setWidth(2).setY(5).setInsets(new Insets(0, 0, 0, 0)).setComp(editor);

		FocusListener listener = new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				saveContents();
			}

		};

		txtDescription.addFocusListener(listener);

		txtName.addFocusListener(listener);
		this.defaultBackground = txtName.getBackground();

		txtName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				nameChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				nameChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				nameChanged();
			}
		});

	}

	protected void nameChanged() {
		// if we set the name first we get an event that the text has changed,
		// but we don't need to save the text, because it was the original name
		if (this.selected == null) {
			return;
		}

		String name = txtName.getText();

		try {
			nameChecker.checkName(name);

			txtName.setBackground(defaultBackground);
			selected.setName(txtName.getText());
			model.densityChanged(selected);
			lbNameError.setVisible(false);
		} catch (InvalidNameException e) {
			txtName.setBackground(ColorConstants.ERROR_COLOR);
			lbNameError.setText(e.getMessage());
			lbNameError.setVisible(true);
		}
	}

	protected void saveContents() {
		if (this.selected != null) {
			selected.setDescription(txtDescription.getText());
			model.densityChanged(selected);
			editor.saveContents();
		}
	}

	public void unselecet() {
		this.selected = null;
		editor.unselect();
	}

	public void setSelected(DensityData selected) {
		saveContents();

		txtName.setText(selected.getName());
		txtDescription.setText(selected.getDescription());

		this.selected = selected;
		editor.setData(selected);
	}

	public void dispose() {
		this.editor.dispose();
	}
}
