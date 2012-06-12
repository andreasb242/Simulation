package ch.zhaw.simulation.window.xy.sidebar.config;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import butti.javalibs.controls.TitleLabel;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.NamedFormulaData.Status;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.XYSimulationAdapter;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.MesoData.Derivative;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.window.sidebar.config.SidebarActionListener.SidebarAction;
import ch.zhaw.simulation.window.sidebar.config.SingleConfigurationField;
import ch.zhaw.simulation.window.xy.sidebar.config.font.FontLoader;

/**
 * The configruation fields for X and Y Formula for movements of the Meso
 * Component
 * 
 * @author Andreas Butti
 */
public class MoveConfigurationField extends SingleConfigurationField {
	private SimulationXYModel model;
	private JComboBox cbDerivative;
	
	private JPanel pX = new JPanel();
	private JPanel pY = new JPanel();

	private JLabel lbX = new JLabel("x");
	private JLabel lbY = new JLabel("y");
	
	private JLabel stateX = new JLabel("?");
	private JLabel stateY = new JLabel("?");

	XYSimulationListener changeListener = new XYSimulationAdapter() {
		@Override
		public void dataChanged(AbstractSimulationData o) {
			if(o == getData()) {
			updateFormulaStatus();
			}
		}
	};

	public MoveConfigurationField(SimulationXYModel model) {
		this.model = model;
		model.addListener(changeListener);
	}

	@Override
	public void dispose() {
		model.removeListener(changeListener);
	}

	@Override
	public int getOrder() {
		return 400;
	}

	protected JButton btEdit;

	private static class DerivativeEntry {
		final Derivative d;
		final String name;

		public DerivativeEntry(Derivative d, String name) {
			this.d = d;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Derivative) {
				return obj == d;
			}

			return super.equals(obj);
		}
	}

	@Override
	public void init(GroupLayout layout, SequentialGroup g, ParallelGroup layoutBoth, ParallelGroup leftGroup, ParallelGroup rightGroup) {
		JLabel title = new TitleLabel("Bewegung");

		leftGroup.addComponent(title);
		addComponent(title);
		g.addPreferredGap(ComponentPlacement.RELATED, 15, 20);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(title));

		JLabel lbValue = new JLabel("Ableitung");

		/**
		 * Turn of Eclipse Formatter...
		 * 
		 * Menu Window / Preferences Java / Code Style / Formatter: [Edit], Tab
		 * "Off/On Tags"
		 * 
		 * @formatter:off
		 */
		DerivativeEntry[] data = new DerivativeEntry[] {
				new DerivativeEntry(Derivative.NO_DERIVATIVE, "Keine Ableitung"),
				new DerivativeEntry(Derivative.FIRST_DERIVATIVE, "Erste Ableitung"),
				new DerivativeEntry(Derivative.SECOND_DERIVATIVE, "Zweite Ableitung")
			};
		/**
		 * @formatter:on
		 */

		cbDerivative = new JComboBox(data);
		leftGroup.addComponent(lbValue);
		rightGroup.addComponent(cbDerivative);
		addComponent(lbValue);
		addComponent(cbDerivative);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lbValue).addComponent(cbDerivative));

		cbDerivative.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DerivativeEntry d = (DerivativeEntry) cbDerivative.getSelectedItem();
				MesoData m = (MesoData) getData();
				m.setDerivative(d.d);
				setDerivative(d.d);
			}
		});

		// /////////////////////////////////
		// / Edit X

		JButton btEditX = new JButton("bearbeiten", IconLoader.getIcon("text-editor", 24));
		addComponent(btEditX);

		rightGroup.addComponent(btEditX);

		btEditX.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				MesoData d = (MesoData) getData();
				editFormula(d, d.getDataX());
			}

		});

		pX.setLayout(new GridLayout(1, 0));
		pX.add(lbX);
		pX.add(stateX);
		pX.setOpaque(false);
		lbX.setFont(FontLoader.getFont());
		stateX.setForeground(Color.RED);
		leftGroup.addComponent(pX);
		rightGroup.addComponent(btEditX);
		addComponent(pX);
		addComponent(btEditX);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(pX).addComponent(btEditX));

		// /////////////////////////////////
		// / Edit X

		JButton btEditY = new JButton("bearbeiten", IconLoader.getIcon("text-editor", 24));
		addComponent(btEditY);

		rightGroup.addComponent(btEditY);

		btEditY.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				MesoData d = (MesoData) getData();
				editFormula(d, d.getDataY());
			}

		});

		pY.setLayout(new GridLayout(1, 0));
		pY.add(lbY);
		pY.add(stateY);
		pY.setOpaque(false);
		lbY.setFont(FontLoader.getFont());
		stateY.setForeground(Color.RED);

		leftGroup.addComponent(pY);
		rightGroup.addComponent(btEditY);
		addComponent(pY);
		addComponent(btEditY);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(pY).addComponent(btEditY));

	}
	
	protected void updateFormulaStatus() {
		MesoData d = (MesoData) getData();
		if(d == null) {
			return;
		}
		
		if(d.getDataX().getStatus() == Status.SYNTAX_OK) {
			stateX.setText(" ");
		} else {
			stateX.setText("?");
		}

		if(d.getDataY().getStatus() == Status.SYNTAX_OK) {
			stateY.setText(" ");
		} else {
			stateY.setText("?");
		}
	}

	protected void editFormula(MesoData meso, NamedFormulaData data) {
		fireActionPerformed(SidebarAction.SHOW_FORMULA_EDITOR, data);
	}

	@Override
	protected void loadData() {
		MesoData m = (MesoData) getData();
		Derivative d = m.getDerivative();

		// setSelectedObject does not work, because it compares the enum with
		// our object instead of our object (which overrides the equals method)
		// with the enum...
		ComboBoxModel model = cbDerivative.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object e = model.getElementAt(i);
			if (e.equals(d)) {
				cbDerivative.setSelectedIndex(i);
				break;
			}
		}

		setDerivative(d);
		updateFormulaStatus();
	}

	private void setDerivative(Derivative d) {
		if (d == Derivative.FIRST_DERIVATIVE) {
			lbX.setText("ẋ");
			lbY.setText("ẏ");
		} else if (d == Derivative.SECOND_DERIVATIVE) {
			lbX.setText("ẍ");
			lbY.setText("ÿ");
		} else {
			lbX.setText("x");
			lbY.setText("y");
		}
	}

	@Override
	protected boolean canHandleData(AbstractNamedSimulationData data) {
		return data instanceof MesoData;
	}
}
