package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

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
	private Vector<SubModelSelectionListener> listener = new Vector<SubModelSelectionListener>();

	private Object lastSelectedObject = null;

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

		cbSelect.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.SELECTED == e.getStateChange()) {
					if (cbSelect.getSelectedItem() != lastSelectedObject) {
						lastSelectedObject = cbSelect.getSelectedItem();
						fireItemSelected(cbSelect.getSelectedItem());
					}
				}
			}
		});

		add(cbSelect);
	}

	protected void fireItemSelected(Object selectedItem) {
		for (SubModelSelectionListener l : this.listener) {
			l.subModelSelected((SubModel) selectedItem);
		}
	}

	@Override
	public int getSidebarPosition() {
		return 0;
	}

	public void addSubModelSelectionListener(SubModelSelectionListener l) {
		if (l == null) {
			throw new NullPointerException();
		}
		listener.add(l);
	}

	public void removeSubModelSelectionListener(SubModelSelectionListener l) {
		listener.remove(l);
	}

	public void dispose() {
		cbModel.dispose();
	}

}
