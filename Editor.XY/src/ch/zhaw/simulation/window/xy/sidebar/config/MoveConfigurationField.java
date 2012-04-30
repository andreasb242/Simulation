package ch.zhaw.simulation.window.xy.sidebar.config;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import butti.javalibs.controls.TitleLabel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.MesoData.Derivative;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.window.sidebar.config.SingleConfigurationField;

/// TODO !!!!!!!!!
public class MoveConfigurationField extends SingleConfigurationField {
	private SimulationXYModel model;

	public MoveConfigurationField(SimulationXYModel model) {
		this.model = model;
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
			if(obj instanceof Derivative) {
				return obj == d;
			}
			
			return super.equals(obj);
		}
	}

	@Override
	public void init(GroupLayout layout, SequentialGroup g, ParallelGroup leftGroup, ParallelGroup rightGroup) {
		JLabel title = new TitleLabel("Bewegung");

		leftGroup.addComponent(title);
		addComponent(title);
		g.addPreferredGap(ComponentPlacement.RELATED, 15, 20);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(title));

		JLabel lbValue = new JLabel("Ableitung");

		/**
		 * Turn of Eclipse Formater...
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

		JComboBox cbDerivative = new JComboBox(data);
		leftGroup.addComponent(lbValue);
		rightGroup.addComponent(cbDerivative);
		addComponent(lbValue);
		addComponent(cbDerivative);
		g.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lbValue).addComponent(cbDerivative));

		// btEdit = new JButton("bearbeiten", IconLoader.getIcon("text-editor",
		// 24));
		// addComponent(btEdit);
		//
		// rightGroup.addComponent(btEdit);
		//
		// btEdit.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mousePressed(MouseEvent e) {
		// fireActionPerformed(SidebarAction.SHOW_FORMULA_EDITOR, getData());
		// }
		//
		// });

	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean canHandleData(AbstractNamedSimulationData data) {
		System.out.println("->" + data.getClass().getName());

		return data instanceof MesoData;
	}
}
