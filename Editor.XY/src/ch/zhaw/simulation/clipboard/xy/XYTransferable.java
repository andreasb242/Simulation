package ch.zhaw.simulation.clipboard.xy;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.clipboard.TransferData.Type;
import ch.zhaw.simulation.editor.imgexport.ImageExport;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class XYTransferable extends AbstractTransferable<XYClipboardData> {
	/**
	 * Das Model um zusätzliche Daten abzufragen
	 */
	protected SimulationXYModel model;

	public XYTransferable(XYEditorControl control, SelectableElement<?>[] selected, SimulationXYModel model) {
		this.model = model;

		if (model == null) {
			throw new NullPointerException("model == null");
		}

		registerTransformers();

		addCopy(selected);

		ImageExport export = new ImageExport(control);
		this.exportedImage = export.exportToImage(true);
	}

	private void registerTransformers() {

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				MesoData m = (MesoData) data;

				TransferData t = new TransferData(m.getId(), m.getX(), m.getY(), Type.MesoCompartment, m.getName(), m.getFormula(), 0, 0, null);
				t.getAdditionalData().put(XYClipboardData.MESO_SUBMODEL, m.getSubmodel());

				return t;
			}
		}, MesoData.class);
	}

	@Override
	public void initClipboardData() {
		data = new XYClipboardData();
	}

	@Override
	protected Object getClipboardData() {
		return data;
	}
}
