package ch.zhaw.simulation.dialog.overview;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.flow.InfiniteData;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationContainer;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.SimulationGlobal;
import ch.zhaw.simulation.model.flow.SimulationObject;
import ch.zhaw.simulation.model.flow.SimulationParameter;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.messagebox.Messagebox;

import butti.javalibs.controls.listcontrol.SortableTable;
import butti.javalibs.errorhandler.Errorhandler;

public class OverviewWindow extends BDialog {
	private static final long serialVersionUID = 1L;
	private OverviewListModel model;
	private SortableTable list;
	private SimulationFlowModel doc;
	private SimulationControl control;

	public OverviewWindow(JFrame parent, SimulationControl control) {
		super(parent);
		setTitle("Übersicht");
		this.doc = control.getModel();
		this.control = control;

		model = new OverviewListModel(doc);
		list = new SortableTable(model);
		list.setSearchEnabled(true);

		add(list, BorderLayout.CENTER);

		list.setCellRenderer(new EntryRenderer(control.getConfig()));

		JPanel pBottom = new JPanel();
		pBottom.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btCopy = ButtonFactory.createButton("In Zwischenablage kopieren", false);
		JButton btClose = ButtonFactory.createButton("Schliessen", true);

		btClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btCopy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					copyClipboard();
					Messagebox.showInfo(OverviewWindow.this, "Inhalt kopiert", "Der Inhalt wurde als Text in die Zwischenablage kopiert.");
				} catch (Exception ex) {
					String msg = "Der Text konnte nicht kopiert werden, es ist ein Problem aufgetreten.";
					Messagebox.showError(OverviewWindow.this, "Kopieren nicht möglich", msg);
					Errorhandler.logError(ex);
				}
			}
		});

		pBottom.add(btCopy);
		pBottom.add(btClose);

		add(pBottom, BorderLayout.SOUTH);
	}

	protected void copyClipboard() {
		StringBuilder txt = new StringBuilder();

		Vector<SimulationGlobal> data1 = doc.getSimulationGlobal();

		if (data1.size() != 0) {
			txt.append("Globals:\n");
			for (SimulationGlobal g : data1) {
				addObject(txt, g);
			}
			txt.append("\n");
		}

		Vector<SimulationContainer> data2 = doc.getSimulationContainer();
		if (data2.size() != 0) {
			txt.append("Container:\n");
			for (SimulationContainer c : data2) {
				addObject(txt, c);
			}
			txt.append("\n");
		}

		Vector<SimulationParameter> data3 = doc.getSimulationParameter();
		if (data3.size() != 0) {
			txt.append("Parameter:\n");
			for (SimulationParameter g : data3) {
				addObject(txt, g);
			}
			txt.append("\n");
		}

		Vector<FlowConnector> data4 = doc.getFlowConnectors();
		if (data4.size() != 0) {
			txt.append("Flüsse:\n");
			for (FlowConnector f : data4) {
				addObject(txt, f);
			}
		}

		ClipboardHandler cb = control.getClipboard();
		cb.copy(txt.toString());
	}

	private String getName(SimulationObject o) {
		if (o instanceof NamedSimulationObject) {
			return ((NamedSimulationObject) o).getName();
		} else if (o instanceof InfiniteData) {
			return "Unendlich";
		}
		return "type: " + o.getClass() + " #" + Integer.toHexString(o.hashCode());
	}

	private void addObject(StringBuilder txt, FlowConnector f) {
		txt.append(f.getValve().getName());
		txt.append(": von ");
		txt.append(getName(f.getSource()));
		txt.append(" nach ");
		txt.append(getName(f.getTarget()));
		txt.append(":");

		for (String s : f.getValve().getFormula().split("\n")) {
			txt.append("\t");
			txt.append(s);
			txt.append("\n");
		}
	}

	private void addObject(StringBuilder txt, NamedSimulationObject g) {
		txt.append(g.getName());
		txt.append(":");

		for (String s : g.getFormula().split("\n")) {
			txt.append("\t");
			txt.append(s);
			txt.append("\n");
		}
	}

	public void showWindow() {
		model.refresh();
		super.setVisible(true);
		pack();
		setSize(Math.max(getWidth(), 300), Math.max(getHeight(), 500));
		setLocationRelativeTo(getParent());
	}
}
