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
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

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
	private ClipboardHandler<?> clipboardHandler;

	public OverviewWindow(JFrame parent, ClipboardHandler<?> clipboardHandler, SimulationFlowModel doc, GuiConfig config) {
		super(parent);
		setTitle("Übersicht");
		this.doc = doc;
		this.clipboardHandler = clipboardHandler;

		model = new OverviewListModel(doc);
		list = new SortableTable(model);
		list.setSearchEnabled(true);

		add(list, BorderLayout.CENTER);

		list.setCellRenderer(new EntryRenderer(config));

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

		Vector<SimulationGlobalData> data1 = doc.getSimulationGlobal();

		if (data1.size() != 0) {
			txt.append("Globals:\n");
			for (SimulationGlobalData g : data1) {
				addObject(txt, g);
			}
			txt.append("\n");
		}

		Vector<SimulationContainerData> data2 = doc.getSimulationContainer();
		if (data2.size() != 0) {
			txt.append("Container:\n");
			for (SimulationContainerData c : data2) {
				addObject(txt, c);
			}
			txt.append("\n");
		}

		Vector<SimulationParameterData> data3 = doc.getSimulationParameter();
		if (data3.size() != 0) {
			txt.append("Parameter:\n");
			for (SimulationParameterData g : data3) {
				addObject(txt, g);
			}
			txt.append("\n");
		}

		Vector<FlowConnectorData> data4 = doc.getFlowConnectors();
		if (data4.size() != 0) {
			txt.append("Flüsse:\n");
			for (FlowConnectorData f : data4) {
				addObject(txt, f);
			}
		}

		clipboardHandler.copy(txt.toString());
	}

	private String getName(AbstractSimulationData o) {
		if (o instanceof AbstractNamedSimulationData) {
			return ((AbstractNamedSimulationData) o).getName();
		} else if (o instanceof InfiniteData) {
			return "Unendlich";
		}
		return "type: " + o.getClass() + " #" + Integer.toHexString(o.hashCode());
	}

	private void addObject(StringBuilder txt, FlowConnectorData f) {
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

	private void addObject(StringBuilder txt, AbstractNamedSimulationData g) {
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
