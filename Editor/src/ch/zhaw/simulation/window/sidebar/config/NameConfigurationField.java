package ch.zhaw.simulation.window.sidebar.config;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

import butti.javalibs.util.ColorConstants;
import butti.javalibs.util.StringUtil;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.InvalidNameException;
import ch.zhaw.simulation.model.NameChecker;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.undo.action.NameChangeUndoAction;

public class NameConfigurationField extends SingleConfigurationField {
	private JTextField txtName = new JTextField();
	private JLabel lbNameError = new JLabel();
	private NameChecker nameChecker = new NameChecker();
	private Color defaultBackground;
	private UndoManager undo;
	private AbstractSimulationModel<?> model;

	public NameConfigurationField(UndoManager undo, AbstractSimulationModel<?> model) {
		this.undo = undo;
		this.model = model;
	}

	@Override
	public void init(GroupLayout layout, SequentialGroup g, ParallelGroup layoutBoth, ParallelGroup leftGroup, ParallelGroup rightGroup) {
		JLabel title = new JLabel("Name");
		addComponent(title);
		addComponent(txtName);
		addComponent(lbNameError);

		lbNameError.setVisible(false);

		this.defaultBackground = txtName.getBackground();

		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(title).addComponent(txtName));
		g.addGroup(layout.createParallelGroup().addComponent(lbNameError));

		leftGroup.addComponent(title);
		rightGroup.addComponent(txtName);
		layoutBoth.addComponent(lbNameError);

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
		String name = txtName.getText();

		try {
			nameChecker.checkName(name);
			txtName.setBackground(defaultBackground);

			if (!StringUtil.equals(getData().getName(), name)) {
				undo.addEdit(new NameChangeUndoAction(getData(), getData().getName(), name, model));
			}

			lbNameError.setVisible(false);

		} catch (InvalidNameException e) {
			lbNameError.setVisible(true);
			lbNameError.setText(e.getMessage());
			txtName.setBackground(ColorConstants.ERROR_COLOR);
		}
	}

	@Override
	public int getOrder() {
		return 100;
	}

	@Override
	public boolean supportsMultibleEditing() {
		return false;
	}

	@Override
	protected boolean canHandleData(AbstractNamedSimulationData data) {
		// TextData is already filtered in
		// ConfigurationSidebarPanel.selectionChanged()

		return true;
	}

	@Override
	protected void loadData() {
		this.txtName.setText(getData().getName());
	}

}
