package ch.zhaw.simulation.clipboard.flow;

import java.util.HashMap;
import java.util.Vector;

import ch.zhaw.simulation.clipboard.AbstractClipboardData;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.flow.gui.FlowEditorView;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;

/**
 * Handles clipboard Paste for Flow model
 * 
 * @author Andreas Butti
 */
public class FlowClipboardData extends AbstractClipboardData<SimulationFlowModel, FlowEditorView> {
	private static final long serialVersionUID = 1L;

	private HashMap<Integer, AbstractSimulationData> data = new HashMap<Integer, AbstractSimulationData>();

	private Vector<TransferData> flows = new Vector<TransferData>();
	private Vector<TransferData> connectors = new Vector<TransferData>();

	public FlowClipboardData() {
	}

	@Override
	protected boolean handleData(TransferData d) {
		switch (d.getType()) {
		case InfiniteSymbol:
			handleInfiniteData(d);
			return true;
		case Container:
			handleContainer(d);
			return true;
		case Parameter:
			handleParameter(d);
			return true;
		case Flow:
			flows.add(d);
			return true;
		case Connector:
			connectors.add(d);
			return true;
		}
		return false;
	}

	private void handleConnector(TransferData c) {
		AbstractSimulationData source = data.get(c.getSource());
		AbstractSimulationData target = data.get(c.getTarget());

		// Verbindung nur möglich wenn alles kopiert wurde
		if (source == null || target == null) {
			return;
		}

		// Ungültige Daten, woher auch immer
		if (!(source instanceof AbstractNamedSimulationData && target instanceof AbstractNamedSimulationData)) {
			return;
		}

		ParameterConnectorData p = new ParameterConnectorData((AbstractNamedSimulationData) source, (AbstractNamedSimulationData) target);
		p.setHelperPoint(c.getPoint());
		model.addConnector(p);

		selectionModel.addSelectedInt(view.findGuiComponent(p));
	}

	private void handleFlow(TransferData f) {
		AbstractSimulationData source = data.get(f.getSource());
		AbstractSimulationData target = data.get(f.getTarget());

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

		FlowConnectorData c = new FlowConnectorData(source, target);
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

		selectionModel.addSelectedInt(view.findGuiComponent(c));
	}

	private void handleInfiniteData(TransferData d) {
		InfiniteData c = new InfiniteData(d.getX(), d.getY());
		data.put(d.getId(), c);
	}

	private void handleParameter(TransferData d) {
		SimulationParameterData c = new SimulationParameterData(d.getX(), d.getY());
		c.setName(d.getName());
		c.setFormula(d.getFormula());

		data.put(d.getId(), c);

		model.addData(c);

		select(c);
	}


	private void handleContainer(TransferData d) {
		SimulationContainerData c = new SimulationContainerData(d.getX(), d.getY());
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

	@Override
	protected void finalizePaste() {
		for (TransferData f : flows) {
			handleFlow(f);
		}

		for (TransferData c : connectors) {
			handleConnector(c);
		}
	}

	@Override
	protected void addElement(AbstractSimulationData data, TransferData transferdata) {
		this.data.put(transferdata.getId(), data);
		model.addData(data);
	}

}
