package ch.zhaw.simulation.clipboard.xy;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.clipboard.TransferData.Type;
import ch.zhaw.simulation.editor.imgexport.ImageExport;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.element.meso.MesoView;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class XYTransferable extends AbstractTransferable {
	/**
	 * Das Model um zusätzliche Daten abzufragen
	 */
	protected SimulationXYModel model;

	/**
	 * Alle Daten die ins Clipboard kopiert werden
	 */
	protected XYClipboardData data;

	public XYTransferable(XYEditorControl control, SelectableElement[] selected, SimulationXYModel model) {
		this.model = model;

		if (model == null) {
			throw new NullPointerException("model == null");
		}

		addCopy(selected);

		ImageExport export = new ImageExport(control);
		this.exportedImage = export.exportToImage(true);
	}

	@Override
	protected void addCopy(SelectableElement s) {
		if (s instanceof MesoView) {
			MesoData m = ((MesoView) s).getData();

			TransferData t = new TransferData(m.getId(), m.getX(), m.getY(), Type.MesoCompartment, m.getName(), m.getFormula(), 0, 0, null);
			t.getAdditionalData().put(XYClipboardData.MESO_SUBMODEL, m.getSubmodel());

			data.add(t);
		} else {
			throw new RuntimeException("Class not handled in copy / paste: " + s.getClass().getName());
		}
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
