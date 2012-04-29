package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.editor.xy.density.DensityEditorDialog;
import ch.zhaw.simulation.editor.xy.density.DensityListModel;
import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.model.listener.XYSimulationAdapter;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class DensitySidebar extends JXTaskPane implements SidebarPosition {
	private static final long serialVersionUID = 1L;

	private DensityListModel listModel;
	private JComboBox cbDensity;

	private DensityEditorDialog densityEditor;

	private Object lastSelected = null;
	protected Vector<ActionListener> listenerList = new Vector<ActionListener>();

	public DensitySidebar(final JFrame parent, final SimulationXYModel model, JComponent comp, Sysintegration sys) {
		setTitle("Dichte");

		listModel = new DensityListModel(model);
		cbDensity = new JComboBox(listModel);
		cbDensity.setRenderer(new DensityListCellRenderer());

		densityEditor = new DensityEditorDialog(parent, model, sys);

		add(cbDensity);

		cbDensity.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					selectionChanged();
				}
			}
		});

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		add(p);

		JButton btCreate = new JButton("[+]");
		btCreate.addActionListener(new ActionListener() {

			int id = 1;

			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedName = null;
				for (int i = 0; i < 100; i++) {
					String name = "Dichte " + (id++);
					if (model.isValidDensityName(name, null)) {
						selectedName = name;
						break;
					}
				}

				if (selectedName == null) {
					Messagebox.showError(parent, "Kein Name", "<html><b>Sorry, die Applikation konnte keinen Namen finden für die Dichte,</b><br>"
							+ "versuchen Sie es erneut, oder kontaktieren Sie den Entwickler...</html>");
					return;
				}

				DensityData d = new DensityData();
				d.setName(selectedName);
				d.setDescription("");
				model.addDensity(d);
			}
		});

		p.add(btCreate);

		JButton btDelete = new JButton("[-]");

		btDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DensityData d = (DensityData) cbDensity.getSelectedItem();
				if (d != null) {
					model.removeDensity(d);
				}
			}
		});

		p.add(btDelete);

		JButton btEdit = new JButton(IconLoader.getIcon("edit"));
		btEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DensityData d = (DensityData) cbDensity.getSelectedItem();
				if (d != null) {
					densityEditor.setSelectedDensity(d);
					densityEditor.setVisible(true);
				} else {
					Messagebox.showWarning(parent, "Kein Eintrag gwählt", "Bitte wählen / erstellen sie Zuerst einen Eintrag mit dem [+]");
				}
			}
		});

		p.add(btEdit);

		model.addListener(new XYSimulationAdapter() {

			@Override
			public void densityChanged(DensityData d) {
				if (d == lastSelected) {
					for (ActionListener l : listenerList) {
						l.actionPerformed(null);
					}
				}
			}
		});
	}

	protected void selectionChanged() {
		if (lastSelected == cbDensity.getSelectedItem()) {
			return;
		}
		lastSelected = cbDensity.getSelectedItem();

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
