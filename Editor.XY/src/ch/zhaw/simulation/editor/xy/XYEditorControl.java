package ch.zhaw.simulation.editor.xy;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.XYModel;

public class XYEditorControl extends AbstractEditorControl<XYModel> {

	private XYEditorView view;

	public XYEditorControl(SimulationApplication app, SimulationDocument doc, XYModel model, JFrame parent, Settings settings) {
		super(parent, settings, app, doc, model);
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		// TODO Auto-generated method stub

	}

	@Override
	public XYEditorView getView() {
		return view;
	}

	public void setView(XYEditorView view) {
		this.view = view;
	}

	@Override
	public void stopAutoparser() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startAutoparser() {
		// TODO Auto-generated method stub
		
	}

}
