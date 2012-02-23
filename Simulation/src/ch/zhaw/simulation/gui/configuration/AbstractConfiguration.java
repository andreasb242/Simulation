package ch.zhaw.simulation.gui.configuration;


import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationDocument;

import butti.javalibs.gui.GridBagManager;

abstract class AbstractConfiguration<E extends NamedSimulationObject> extends
		JXTaskPane {
	private static final long serialVersionUID = 1L;

	private JTextField txtName = new JTextField();

	private NameChecker nameChecker = new NameChecker();

	private E data;

	private SimulationDocument model;

	protected GridBagManager gbm;

	public AbstractConfiguration(SimulationDocument model, boolean name) {
		this.model = model;
		setTitle("Eigenschaften");
		setSpecial(true);
		gbm = new GridBagManager(this);

		if (name) {
			JLabel lbName = new JLabel("Name");
			lbName.setFont(lbName.getFont().deriveFont(Font.BOLD));

			gbm.setX(0).setWidth(2).setY(10).setWeightX(0).setWeightY(0).setComp(lbName);
			gbm.setX(0).setWidth(2).setY(11).setWidth(2).setWeightY(0).setComp(txtName);

			txtName.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					dataChanged();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					dataChanged();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					dataChanged();
				}
			});
		}

		setVisible(false);
	}

	protected void addAdditionalDataListener(JTextComponent txt) {
		txt.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if(data == null) {
					return;
				}
				additionalDataChanged(data);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if(data == null) {
					return;
				}
				additionalDataChanged(data);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if(data == null) {
					return;
				}
				additionalDataChanged(data);
			}
		});
	}

	protected void additionalDataChanged(E data) {
		model.fireObjectChanged(data, false);
	}

	private void dataChanged() {
		if (data == null) {
			return;
		}

		if (nameChecker.checkName(txtName.getText())) {
			data.setName(txtName.getText());
			txtName.setForeground(Color.BLACK);
			model.fireObjectChanged(data, false);
		} else {
			txtName.setForeground(Color.RED);
		}
	}
	
	public E getData() {
		return data;
	}

	public void setSelected(E data) {
		this.data = data;

		txtName.setText(data.getName());

		setVisible(true);
	}

	public void noneSelected() {
		data = null;
		setVisible(false);
	}
}