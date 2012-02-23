package ch.zhaw.simulation.clipboard;


import java.util.HashMap;
import java.util.Vector;

import ch.zhaw.simulation.gui.DocumentView;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.flow.InfiniteData;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationContainer;
import ch.zhaw.simulation.model.flow.SimulationDocument;
import ch.zhaw.simulation.model.flow.SimulationObject;
import ch.zhaw.simulation.model.flow.SimulationParameter;
import ch.zhaw.simulation.model.flow.TextData;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;


public class ClipboardData extends Vector<TransferData> {
	private static final long serialVersionUID = 1L;
	private SelectionModel selectionModel;
	private SimulationDocument model;
	private DocumentView view;

	private HashMap<Integer, SimulationObject> data = new HashMap<Integer, SimulationObject>();

	private Vector<TransferData> flows = new Vector<TransferData>();
	private Vector<TransferData> connectors = new Vector<TransferData>();

	public void addToModel(SimulationControl control) {
		selectionModel = control.getSelectionModel();
		selectionModel.clearSelection();
		model = control.getModel();
		view = control.getView();

		for (TransferData d : this) {
			switch (d.getType()) {
			case InfiniteSymbol:
				handleInfiniteData(d);
				break;
			case Container:
				handleContainer(d);
				break;
			case Parameter:
				handleParameter(d);
				break;
			case Text:
				handleText(d);
				break;
			case Flow:
				flows.add(d);
				break;
			case Connector:
				connectors.add(d);
				break;
			default:
				throw new RuntimeException("Unhandled Type: " + d.getType());
			}
		}

		for (TransferData f : flows) {
			handleFlow(f);
		}

		for (TransferData c : connectors) {
			handleConnector(c);
		}
		
		selectionModel.fireSelectionChanged();
	}

	private void handleConnector(TransferData c) {
		SimulationObject source = data.get(c.getSource());
		SimulationObject target = data.get(c.getTarget());

		// Verbindung nur möglich wenn alles kopiert wurde
		if (source == null || target == null) {
			return;
		}

		// Ungültige Daten, woher auch immer
		if (!(source instanceof NamedSimulationObject && target instanceof NamedSimulationObject)) {
			return;
		}

		ParameterConnector p = new ParameterConnector((NamedSimulationObject) source, (NamedSimulationObject) target);
		p.setConnectorPoint(c.getPoint());
		model.addConnector(p);
		selectionModel.addSelectedInt(view.findGuiComponent(p));
	}

	private void handleFlow(TransferData f) {
		SimulationObject source = data.get(f.getSource());
		SimulationObject target = data.get(f.getTarget());

		// Verbindung alleine wird nicht verarbeitet
		if (source == null && target == null) {
			return;
		}

		// Verbindung von nichts nach unendlich nicht zulassen
		if (source == null && target instanceof InfiniteData) {
			return;
		}

		// Verbindung von unendlich nach nichts nicht zulassen
		if (target == null && source instanceof InfiniteData) {
			return;
		}

		int x = f.getX();
		int y = f.getY();

		if (target == null) {
			int xi = x;
			int yi = y;

			if (x > 70) {
				// 1. Prio: links von Parameterflow
				xi = x - 70;
			} else if (y > 70) {
				// 2. Prio: oben von Parameterflow
				yi = y - 70;
			} else {
				// 3. Prio: unten von Parameterflow
				yi = y + 70;
			}

			target = new InfiniteData(xi, yi);
		}

		if (source == null) {
			// 1. Prio: rechts von Parameterflow
			source = new InfiniteData(x + 70, y);
		}

		FlowConnector c = new FlowConnector(source, target);
		c.getParameterPoint().setName(f.getName());

		c.getParameterPoint().setFormula(f.getFormula());

		data.put(f.getId(), c.getParameterPoint());

		if (source instanceof InfiniteData) {
			model.addData(source);
			select(source);
		}
		if (target instanceof InfiniteData) {
			model.addData(target);
			select(target);
		}

		model.addConnector(c);

		select(c.getParameterPoint());
	}

	private void handleInfiniteData(TransferData d) {
		InfiniteData c = new InfiniteData(d.getX(), d.getY());
		data.put(d.getId(), c);
	}

	private void handleParameter(TransferData d) {
		SimulationParameter c = new SimulationParameter(d.getX(), d.getY());
		c.setName(d.getName());
		c.setFormula(d.getFormula());

		data.put(d.getId(), c);

		model.addData(c);

		select(c);
	}

	private void handleText(TransferData d) {
		TextData t = new TextData(d.getX(), d.getY());
		t.setName(d.getName());
		t.setText(d.getFormula());

		data.put(d.getId(), t);

		model.addData(t);

		select(t);
	}

	private void select(SimulationObject c) {
		selectionModel.addSelectedInt(view.findGuiComponent(c));
	}

	private void handleContainer(TransferData d) {
		SimulationContainer c = new SimulationContainer(d.getX(), d.getY());
		c.setName(d.getName());
		c.setFormula(d.getFormula());

		data.put(d.getId(), c);

		model.addData(c);

		select(c);
	}
}
