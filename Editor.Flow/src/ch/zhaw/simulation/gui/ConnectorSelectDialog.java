package ch.zhaw.simulation.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.gui.control.FlowToolbar;
import ch.zhaw.simulation.gui.control.FlowEditorControl;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.gui.messagebox.Messagebox;


public class ConnectorSelectDialog extends BDialog {
	private static final long serialVersionUID = 1L;
	private boolean flow = false;

	private GridBagManager gbm;

	public ConnectorSelectDialog(FlowEditorControl control) {
		super(control.getParent());
		setModal(true);

		gbm = new GridBagManager(this);

		setTitle("Verbindung wählen");

		gbm.setX(0).setY(0).setWeightX(0).setComp(new JLabel(Messagebox.QUESTION_ICON));

		gbm.setX(1).setY(0).setComp(new JLabel("Bitte wählen Sie den Verbindungstyp"));

		ImageIcon flowArrowIcon = FlowToolbar.addShadow(new FlowArrowImage(24, control.getSysintegration().getGuiConfig()).getImage(false));

		JButton btFlow = new JButton("Fluss");
		btFlow.setIcon(flowArrowIcon);
		btFlow.setIconTextGap(20);
		btFlow.setHorizontalAlignment(SwingConstants.LEFT);

		btFlow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				flow = true;
				setVisible(false);
			}
		});

		gbm.setX(1).setY(2).setComp(btFlow);

		JButton btParam = new JButton("Parameter");
		btParam.setIcon(FlowToolbar.drawArrowIcon(control.getSysintegration().getGuiConfig()));
		gbm.setX(1).setY(3).setComp(btParam);
		btParam.setIconTextGap(20);
		btParam.setHorizontalAlignment(SwingConstants.LEFT);

		btParam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		pack();
		setLocationRelativeTo(control.getParent());
	}

	public boolean isFlow() {
		return flow;
	}
}
