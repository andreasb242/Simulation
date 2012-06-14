package ch.zhaw.simulation.clipboard.xy;

import ch.zhaw.simulation.clipboard.AbstractClipboardData;
import ch.zhaw.simulation.clipboard.TransferData;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorView;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.MesoData.Derivative;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;

public class XYClipboardData extends AbstractClipboardData<SimulationXYModel, XYEditorView> {

	public static final String MESO_SUBMODEL = "meso.submodel";
	public static final String MESO_DIRECTION_X = "meso.direction.x";
	public static final String MESO_DIRECTION_Y = "meso.direction.y";
	public static final String MESO_DERIVATIVE = "meso.derivative";

	public XYClipboardData(int editorSourceId) {
		super(editorSourceId);
	}

	@Override
	public boolean addToModel(AbstractEditorControl<?> control) {
		if (control instanceof XYEditorControl) {
			XYEditorControl c = (XYEditorControl) control;
			addToModel(c.getSelectionModel(), c.getModel(), c.getView());
			return true;
		}

		return false;
	}

	@Override
	protected boolean handleData(TransferData d) {
		switch (d.getType()) {
		case MesoCompartment:
			handleMesoCompartment(d);
			return true;
		}
		return false;
	}

	private void handleMesoCompartment(TransferData d) {
		MesoData m = new MesoData(d.getX(), d.getY());
		m.setName(d.getName());
		m.setFormula(d.getFormula());
		m.setSubmodel((SubModel) d.getAdditionalData().get(MESO_SUBMODEL));
		m.getDataX().setFormula((String)d.getAdditionalData().get(MESO_DIRECTION_X));
		m.getDataY().setFormula((String)d.getAdditionalData().get(MESO_DIRECTION_Y));
		m.setDerivative((Derivative) d.getAdditionalData().get(MESO_DERIVATIVE));

		model.addData(m);

		select(m);
	}

	@Override
	protected void addElement(AbstractSimulationData data, TransferData transferdata) {
		model.addData(data);
	}
}
