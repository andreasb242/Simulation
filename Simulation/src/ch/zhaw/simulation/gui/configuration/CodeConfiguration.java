package ch.zhaw.simulation.gui.configuration;


import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.model.NamedSimulationObject;


public abstract class CodeConfiguration<E extends NamedSimulationObject> extends AbstractConfiguration<E> {
	private static final long serialVersionUID = 1L;

	private JButton btEdit;

	public CodeConfiguration(final SimulationControl control, String codeName, boolean name) {
		super(control.getModel(), name);

		JLabel lbStartvalue = new JLabel(codeName);
		lbStartvalue.setFont(lbStartvalue.getFont().deriveFont(Font.BOLD));

		gbm.setX(0).setY(40).setWidth(3).setWeightY(0).setComp(lbStartvalue);

		btEdit = new JButton("Formel bearbeiten", IconSVG.getIcon("text-editor", 24));
		gbm.setX(0).setY(41).setWidth(3).setWeightY(0).setComp(btEdit);

		btEdit.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				control.showFormulaEditor(getData());
			}

		});
	}
}
