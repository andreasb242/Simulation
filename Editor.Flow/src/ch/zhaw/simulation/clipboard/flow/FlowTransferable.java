package ch.zhaw.simulation.clipboard.flow;

import java.awt.Point;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.clipboard.TransferData.Type;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.flow.elements.valve.FlowValveElement;
import ch.zhaw.simulation.editor.imgexport.ImageExport;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.selection.SelectableElement;

public class FlowTransferable extends AbstractTransferable<FlowClipboardData> {
	/**
	 * Das Model um zusätzliche Daten abzufragen
	 */
	protected SimulationFlowModel model;

	public FlowTransferable(FlowEditorControl control, SelectableElement<?>[] selected, SimulationFlowModel model) {
		super(control.getEditorId());
		this.model = model;

		if (model == null) {
			throw new NullPointerException("model == null");
		}

		registerTransformers();

		addCopy(selected);

		if (control != null) {
			ImageExport export = new ImageExport(control);
			this.exportedImage = export.exportToImage(true);
		}
	}

	private void registerTransformers() {

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				SimulationContainerData c = (SimulationContainerData) data;
				return new TransferData(c.getId(), c.getX(), c.getY(), Type.Container, c.getName(), c.getFormula(), 0, 0, null);
			}
		}, SimulationContainerData.class);

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				InfiniteData d = (InfiniteData) data;
				return new TransferData(d.getId(), d.getX(), d.getY(), Type.InfiniteSymbol, "", "", 0, 0, null);
			}
		}, InfiniteData.class);

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				SimulationParameterData d = (SimulationParameterData) data;
				return new TransferData(d.getId(), d.getX(), d.getY(), Type.Parameter, d.getName(), d.getFormula(), 0, 0, null);
			}
		}, SimulationParameterData.class);

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {

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

				return new TransferData(d.getId(), d.getX(), d.getY(), Type.Flow, d.getName(), d.getFormula(), source, target, null);
			}
		}, FlowValveData.class);

		// we don't need this, because FlowValveData handle this for us
		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				return null;
			}
		}, FlowConnectorData.class);

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				ParameterConnectorData d = (ParameterConnectorData) data;
				Point p = d.getHelperPoint();
				return new TransferData(0, p.x, p.y, Type.Connector, null, null, d.getSource().getId(), d.getTarget().getId(), d.getHelperPoint());
			}
		}, ParameterConnectorData.class);

	}

	@Override
	public void initClipboardData(int editorSourceId) {
		data = new FlowClipboardData(editorSourceId);
	}

	@Override
	protected Object getClipboardData() {
		return data;
	}
}
