package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.model.xy.SubModelList;

public class SubmodelSidebar extends JXTaskPane implements SidebarPosition {
	private static final long serialVersionUID = 1L;

	private int nextNr = 1;

	private SubmodelComboboxModel cbModel;
	private JComboBox cbSelect;

	public SubmodelSidebar(final SubModelList model) {
		JButton btAdd = new JButton("Neues modell");
		add(btAdd);

		btAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SubModel sm = new SubModel();
				sm.setName("Model " + (nextNr++));
				model.addModel(sm);
			}
		});

		cbModel = new SubmodelComboboxModel(model);
		cbSelect = new JComboBox(cbModel);
		cbSelect.setRenderer(new SubModelRenderer());

		add(cbSelect);
	}

	@Override
	public int getSidebarPosition() {
		return 0;
	}

	public void dispose() {
		cbModel.dispose();
	}

}
