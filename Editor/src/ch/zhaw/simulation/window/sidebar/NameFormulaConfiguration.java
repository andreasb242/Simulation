package ch.zhaw.simulation.window.sidebar;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.InvalidNameException;
import ch.zhaw.simulation.model.NameChecker;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;

public abstract class NameFormulaConfiguration extends JXTaskPane implements SelectionListener, SidebarPosition {
	private static final long serialVersionUID = 1L;

	private JTextField txtName = new JTextField();
	private JLabel lbNameError = new JLabel();

	protected TitleLabel lbValue;
	protected JButton btEdit;

	private NameChecker nameChecker = new NameChecker();

	protected AbstractNamedSimulationData data;

	protected AbstractSimulationModel<?> model;

	protected GridBagManager gbm;

	private SelectionModel selectionModel;

	public NameFormulaConfiguration(AbstractSimulationModel<?> model, SelectionModel selectionModel) {
		this.model = model;
		this.selectionModel = selectionModel;
		setTitle("Eigenschaften");
		setSpecial(true);
		gbm = new GridBagManager(this);
		
		lbNameError.setVisible(false);

		gbm.setX(0).setY(10).setWeightX(0).setWeightY(0).setComp(new TitleLabel("Name"));
		gbm.setX(1).setY(10).setWeightY(0).setComp(txtName);
		gbm.setX(0).setWidth(2).setY(11).setWeightY(0).setComp(lbNameError);

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

		this.lbValue = new TitleLabel("Wert");
		gbm.setX(0).setY(1000).setWeightY(0).setComp(lbValue);

		btEdit = new JButton("bearbeiten", IconLoader.getIcon("text-editor", 24));
		gbm.setX(1).setY(1000).setWeightY(0).setComp(btEdit);

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
	public abstract void showFormulaEditor(AbstractNamedSimulationData data);

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

	protected void additionalDataChanged(AbstractNamedSimulationData data) {
		model.fireObjectChanged(data);
	}

	private void dataChanged() {
		if (data == null) {
			return;
		}

		try {
			nameChecker.checkName(txtName.getText());
			data.setName(txtName.getText());
			txtName.setForeground(Color.BLACK);
			model.fireObjectChanged(data);
			lbNameError.setVisible(false);
			
		} catch (InvalidNameException e) {
			txtName.setForeground(Color.RED);
			lbNameError.setVisible(true);
			lbNameError.setText(e.getMessage());
		}
	}

	public AbstractNamedSimulationData getData() {
		return data;
	}

	public void setSelected(AbstractNamedSimulationData data) {
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
		SelectableElement<?>[] selected = selectionModel.getSelected();

		if (selected.length == 1) {
			SelectableElement<?> s = selected[0];

			if (s instanceof AbstractDataView) {
				AbstractSimulationData data = ((AbstractDataView<?>) s).getData();
				if (data instanceof AbstractNamedSimulationData && !(data instanceof TextData)) {
					setSelected((AbstractNamedSimulationData) data);
				}
				return;
			}
		}

		noneSelected();
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}
	
	@Override
	public int getSidebarPosition() {
		return 0;
	}
}