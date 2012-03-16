package ch.zhaw.simulation.editor.xy;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.xy.dialog.XYSizeDialog;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class XYEditorControl extends AbstractEditorControl<SimulationXYModel> {

	private XYEditorView view;

	public XYEditorControl(SimulationApplication app, SimulationDocument doc, SimulationXYModel model, JFrame parent, Settings settings) {
		super(parent, settings, app, doc, model);
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		// TODO Delete, XY dialog

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
		// TODO Autoparser XY Dialog

	}

	@Override
	public void startAutoparser() {
		// TODO Autoparser XY Dialog

	}

	public void addMeso() {
		cancelAllActions();
		addComponent(new MesoData(0, 0), "Meso");
	}
	
	@Override
	public boolean menuActionPerformedOverwrite(MenuToolbarAction action) {
		switch (action.getType()) {
		case XY_ADD_MESO:
			addMeso();
			return true;
			
		case XY_MODEL_SIZE:
			editModelSize();
			return true;
		}

		return false;
	}

	private void editModelSize() {
		XYSizeDialog dlg = new XYSizeDialog(getParent(), getModel());
		dlg.setModal(true);
		dlg.setVisible(true);
		
		dlg.dispose();
	}
}
