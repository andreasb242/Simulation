package ch.zhaw.simulation.window.sidebar;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NameChecker;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;

public abstract class NameFormulaConfiguration extends JXTaskPane implements SelectionListener {
	private static final long serialVersionUID = 1L;

	private JTextField txtName = new JTextField();

	private JButton btEdit;

	private NameChecker nameChecker = new NameChecker();

	private NamedSimulationObject data;

	private AbstractSimulationModel<?> model;

	protected GridBagManager gbm;

	private SelectionModel selectionModel;

	public NameFormulaConfiguration(AbstractSimulationModel<?> model, SelectionModel selectionModel) {
		this.model = model;
		this.selectionModel = selectionModel;
		setTitle("Eigenschaften");
		setSpecial(true);
		gbm = new GridBagManager(this);

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

		JLabel lbStartvalue = new JLabel("Wert");
		lbStartvalue.setFont(lbStartvalue.getFont().deriveFont(Font.BOLD));

		gbm.setX(0).setY(40).setWidth(3).setWeightY(0).setComp(lbStartvalue);

		btEdit = new JButton("Formel bearbeiten", IconSVG.getIcon("text-editor", 24));
		gbm.setX(0).setY(41).setWidth(3).setWeightY(0).setComp(btEdit);

		btEdit.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				showFormulaEditor(getData());
			}

		});

		setVisible(false);
	}
	
	/**
	 * Shows the formula editor to
	 * @param data The data to edit
	 */
	public abstract void showFormulaEditor(NamedSimulationObject data);

	protected void addAdditionalDataListener(JTextComponent txt) {
		txt.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (data == null) {
					return;
				}
				additionalDataChanged(data);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (data == null) {
					return;
				}
				additionalDataChanged(data);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (data == null) {
					return;
				}
				additionalDataChanged(data);
			}
		});
	}

	protected void additionalDataChanged(NamedSimulationObject data) {
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

	public NamedSimulationObject getData() {
		return data;
	}

	public void setSelected(NamedSimulationObject data) {
		this.data = data;

		txtName.setText(data.getName());

		setVisible(true);
	}

	public void noneSelected() {
		data = null;
		setVisible(false);
	}

	@Override
	public void selectionChanged() {
		SelectableElement[] selected = selectionModel.getSelected();

		if (selected.length == 1) {
			SelectableElement s = selected[0];

			if (s instanceof GuiDataElement) {
				SimulationObject data = ((GuiDataElement<?>) s).getData();
				if (data instanceof NamedSimulationObject && !(data instanceof TextData)) {
					setSelected((NamedSimulationObject) data);
				}
				return;
			}
		}

		noneSelected();
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}
}