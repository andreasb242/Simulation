package ch.zhaw.simulation.editor.xy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.xy.dialog.XYSizeDialog;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.model.xy.SubModelList;
import ch.zhaw.simulation.undo.action.xy.DeleteUndoAction;
import ch.zhaw.simulation.xy.util.ColorIcon;

public class XYEditorControl extends AbstractEditorControl<SimulationXYModel> {

	private XYEditorView view;
	private XYDefaultSettingsHandler defaultSettings;
	private SubmodelHandler submodelHandler = new SubmodelHandler();

	public XYEditorControl(SimulationApplication app, SimulationDocument doc, SimulationXYModel model, JFrame parent, Settings settings) {
		super(parent, settings, app, doc, model);

		// not initialized yet
		if (model.getWidth() == 0) {
			getDefaultSettings().load(model);
		}
	}

	private XYDefaultSettingsHandler getDefaultSettings() {
		if (defaultSettings == null) {
			defaultSettings = new XYDefaultSettingsHandler(getSettings());
		}
		return defaultSettings;
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		Vector<AbstractNamedSimulationData> removedObjects = new Vector<AbstractNamedSimulationData>();

		for (SelectableElement el : elements) {
			if (el instanceof GuiDataTextElement<?>) {
				GuiDataTextElement<?> control = (GuiDataTextElement<?>) el;
				AbstractNamedSimulationData data = (AbstractNamedSimulationData) control.getData();

				removedObjects.add(data);
			}
		}

		getUndoManager().addEdit(new DeleteUndoAction(removedObjects, this));

	}

	@Override
	public XYEditorView getView() {
		return view;
	}

	public void setView(XYEditorView view) {
		this.view = view;
	}

	public SubmodelHandler getSubmodelHandler() {
		return submodelHandler;
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
		MesoData meso = new MesoData(0, 0);
		meso.setSubmodel(getView().getCurrentSelectedSubmodel());
		addComponent(meso, "Meso");
	}

	@Override
	public boolean menuActionPerformedOverwrite(MenuToolbarAction action) {
		switch (action.getType()) {
		case XY_ADD_MESO:
			addMeso();
			return true;

		case XY_MESO_POPUP:
			showMesoPopup((JComponent) action.getData());
			return true;

		case XY_MODEL_SIZE:
			editModelSize();
			return true;
		}

		return false;
	}

	private void showMesoPopup(JComponent button) {
		final SubModelList m = model.getSubmodels();

		JPopupMenu popup = new JPopupMenu();

		for (final SubModel sub : m) {
			JMenuItem mi = new JMenuItem(sub.getName());
			mi.setIcon(new ColorIcon(sub.getColor()));
			popup.add(mi);
			
			mi.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					submodelHandler.fireItemSelected(sub);					
				}
			});
		}

		if (m.getSize() > 0) {
			popup.addSeparator();
		}

		JMenuItem madd = new JMenuItem("Neus Submodell erstellen");
		popup.add(madd);
		madd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				m.addModel(new SubModel());
			}
		});

		popup.show(button, 0, 24);
	}

	private void editModelSize() {
		XYSizeDialog dlg = new XYSizeDialog(getParent(), getModel(), getDefaultSettings());
		dlg.setModal(true);
		dlg.setVisible(true);

		dlg.dispose();
	}
}
