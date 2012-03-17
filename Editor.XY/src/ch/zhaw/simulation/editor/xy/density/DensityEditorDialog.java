package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class DensityEditorDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private DensityListModel listModel;
	private JList list;

	private EditorPanel editor;

	private SimulationXYModel model;

	public DensityEditorDialog(JFrame parent, SimulationXYModel model, Sysintegration sys, FunctionHelp help) {
		super(parent);
		setTitle("Dichte bearbeiten");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.model = model;
		editor = new EditorPanel(model, sys, help);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		add(split);
		
		split.setRightComponent(editor);
		
		
		listModel = new DensityListModel(model);
		list = new JList(listModel);
		list.setCellRenderer(new DensityRenderer());

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedValue() != null) {
					editor.setSelected((DensityData) list.getSelectedValue());
				}
			}
		});
		
		JPanel pLeft = new JPanel();
		GridBagManager gbm = new GridBagManager(pLeft);

		gbm.setX(0).setY(0).setWidth(3).setWeightX(0).setScrollPanel().setComp(list);

		JButton btAdd = new JButton("+");
		JButton btRemove = new JButton("-");
		gbm.setX(0).setY(1).setWeightX(0).setWeightY(0).setComp(btAdd);
		gbm.setX(1).setY(1).setWeightX(0).setWeightY(0).setComp(btRemove);

		btAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addDensity();

			}
		});
		
		split.setLeftComponent(pLeft);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	// TODO DEBUG
	static int tmpNextId = 0;

	protected void addDensity() {
		DensityData d = new DensityData();
		d.setName("d" + tmpNextId++);

		d.setDescription("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et ");

		model.addDensity(d);
	}

	@Override
	public void dispose() {
		listModel.dispose();

		super.dispose();
	}

}
