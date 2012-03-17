package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.editor.xy.density.DensityEditorDialog;
import ch.zhaw.simulation.editor.xy.density.DensityListModel;
import ch.zhaw.simulation.editor.xy.density.DensityRenderer;
import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class DensitySidebar extends JXTaskPane implements SidebarPosition {
	private static final long serialVersionUID = 1L;

	private DensityListModel listModel;
	private JList list;

	private DensityEditorDialog densityEditor;

	private Object lastSelected = null;
	protected Vector<ActionListener> listenerList = new Vector<ActionListener>();

	public DensitySidebar(JFrame parent, SimulationXYModel model, JComponent comp, Sysintegration sys, FunctionHelp help) {
		setTitle("Dichte");

		listModel = new DensityListModel(model);
		list = new JList(listModel);
		list.setCellRenderer(new DensityRenderer());

		densityEditor = new DensityEditorDialog(parent, model, sys, help);

		add(new JScrollPane(list));

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectionChanged();
			}
		});

		JButton btEditDensity = new JButton("Bearbeiten");
		btEditDensity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				densityEditor.setVisible(true);
			}
		});

		add(btEditDensity);
	}

	protected void selectionChanged() {
		if (lastSelected == list.getSelectedValue()) {
			return;
		}
		lastSelected = list.getSelectedValue();

		for (ActionListener l : listenerList) {
			l.actionPerformed(null);
		}
	}

	public DensityData getSelected() {
		return (DensityData) lastSelected;
	}

	public void dispose() {
		listModel.dispose();
	}

	@Override
	public int getSidebarPosition() {
		return 100;
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(l);
	}

	public void removeActionListener(ActionListener l) {
		listenerList.remove(l);
	}
}
