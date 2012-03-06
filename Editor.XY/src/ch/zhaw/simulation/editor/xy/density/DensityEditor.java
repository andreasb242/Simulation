package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.XYModel;

public class DensityEditor extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;
	private DensityListModel listModel;
	private JList list;

	private EditorPanel editor = new EditorPanel();

	private XYModel model;

	public DensityEditor(JFrame parent, XYModel model) {
		super(parent);
		setTitle("Edit Density");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.model = model;

		gbm = new GridBagManager(this, true);

		listModel = new DensityListModel(model);
		list = new JList(listModel);
		list.setCellRenderer(new DensityRenderer());

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

		gbm.setX(10).setY(0).setHeight(2).setComp(editor);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

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
