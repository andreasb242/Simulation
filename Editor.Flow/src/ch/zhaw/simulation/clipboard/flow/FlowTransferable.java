package ch.zhaw.simulation.clipboard.flow;

import java.awt.Point;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.clipboard.TransferData.Type;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.BezierHelperPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.flow.elements.valve.FlowValveElement;
import ch.zhaw.simulation.editor.view.TextView;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.selection.SelectableElement;

public class FlowTransferable extends AbstractTransferable {
	/**
	 * Das Model um zusätzliche Daten abzufragen
	 */
	protected SimulationFlowModel model;

	/**
	 * Alle Daten die ins Clipboard kopiert werden
	 */
	protected FlowClipboardData data;

	public FlowTransferable(SelectableElement[] selected, SimulationFlowModel model) {
		this.model = model;

		if (model == null) {
			throw new NullPointerException("model == null");
		}

		addCopy(selected);
	}

	@Override
	protected void addCopy(SelectableElement s) {
		if (s instanceof ContainerView) {
			SimulationContainerData c = ((ContainerView) s).getData();
			data.add(new TransferData(c.getId(), c.getX(), c.getY(), Type.Container, c.getName(), c.getFormula(), 0, 0, null));
		} else if (s instanceof InfiniteSymbol) {
			InfiniteData d = ((InfiniteSymbol) s).getData();
			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.InfiniteSymbol, "", "", 0, 0, null));
		} else if (s instanceof ParameterView) {
			SimulationParameterData d = ((ParameterView) s).getData();
			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.Parameter, d.getName(), d.getFormula(), 0, 0, null));
		} else if (s instanceof TextView) {
			TextData d = ((TextView) s).getData();
			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.Text, d.getName(), d.getText(), 0, 0, null));
		} else if (s instanceof FlowValveElement) {
			FlowValveData d = ((FlowValveElement) s).getData();

			int source = 0;
			int target = 0;

			for (FlowConnectorData f : model.getFlowConnectors()) {
				if (f.getValve() == d) {
					source = f.getSource().getId();
					target = f.getTarget().getId();
					break;
				}
			}

			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.Flow, d.getName(), d.getFormula(), source, target, null));
		} else if (s instanceof BezierHelperPoint) {
			AbstractConnectorData<?> condata = ((BezierHelperPoint) s).getConnectorData();

			if (condata instanceof ParameterConnectorData) {
				ParameterConnectorData d = (ParameterConnectorData) condata;
				Point p = d.getHelperPoint();
				data.add(new TransferData(0, p.x, p.y, Type.Connector, null, null, d.getSource().getId(), d.getTarget().getId(), d.getHelperPoint()));
			}
		} else {
			throw new RuntimeException("Class not handled in copy / paste: " + s.getClass().getName());
		}
	}

	@Override
	public void initClipboardData() {
		data = new FlowClipboardData();
	}

	@Override
	protected Object getClipboardData() {
		return data;
	}
}
