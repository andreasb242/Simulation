package ch.zhaw.simulation.clipboard.flow;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Vector;

import ch.zhaw.simulation.clipboard.ClipboardData;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.flow.gui.FlowEditorView;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;

public class FlowClipboardData extends Vector<TransferData> implements ClipboardData {
	private static final long serialVersionUID = 1L;

	private SelectionModel selectionModel;
	private SimulationFlowModel model;
	private FlowEditorView view;

	private HashMap<Integer, SimulationObject> data = new HashMap<Integer, SimulationObject>();

	private Vector<TransferData> flows = new Vector<TransferData>();
	private Vector<TransferData> connectors = new Vector<TransferData>();
	private boolean flowModel;

	public FlowClipboardData() {
	}

	// TODO: im vordergrund einfügen!!!
	public void addToModel(SelectionModel selectionModel, SimulationFlowModel model, FlowEditorView view) {
		this.selectionModel = selectionModel;

		this.model = model;
		this.view = view;

		this.flowModel = model.getModelType() == SimulationType.FLOW_SIMULATION;

		selectionModel.clearSelection();

		for (TransferData d : this) {
			switch (d.getType()) {
			case InfiniteSymbol:
				if (flowModel) {
					handleInfiniteData(d);
				}
				break;
			case Container:
				if (flowModel) {
					handleContainer(d);
				}
				break;
			case Parameter:
				if (flowModel) {
					handleParameter(d);
				}
				break;
			case Text:
				handleText(d);
				break;
			case Flow:
				if (flowModel) {
					flows.add(d);
				}
				break;
			case Connector:
				if (flowModel) {
					connectors.add(d);
				}
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
		if (!flowModel) {
			throw new InvalidParameterException();
		}

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
		c.getValve().setName(f.getName());

		c.getValve().setFormula(f.getFormula());

		data.put(f.getId(), c.getValve());

		if (source instanceof InfiniteData) {
			model.addData(source);
			select(source);
		}
		if (target instanceof InfiniteData) {
			model.addData(target);
			select(target);
		}

		model.addConnector(c);

		select(c.getValve());
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

	@Override
	public boolean addToModel(AbstractEditorControl<?> control) {
		if (control instanceof FlowEditorControl) {
			FlowEditorControl c = (FlowEditorControl) control;
			addToModel(c.getSelectionModel(), c.getModel(), c.getView());
			return true;
		}

		return false;
	}
}