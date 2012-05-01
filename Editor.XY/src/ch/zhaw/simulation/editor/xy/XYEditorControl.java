package ch.zhaw.simulation.editor.xy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.TextEditDialog;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.gui.splitmenuitem.SplitMenuitem;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.xy.autoparser.Autoparser;
import ch.zhaw.simulation.editor.xy.dialog.XYSizeDialog;
import ch.zhaw.simulation.icon.IconLoader;
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

	private Autoparser autoparser;
	
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
	protected void delete(SelectableElement<?>[] elements) {
		Vector<AbstractNamedSimulationData> removedObjects = new Vector<AbstractNamedSimulationData>();

		for (SelectableElement<?> el : elements) {
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
		autoparser.stop();
		System.out.println("stop autoparser");
	}

	@Override
	public void startAutoparser() {
		autoparser.start();
		System.out.println("start autoparser");
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
			SplitMenuitem mi = new SplitMenuitem(sub.getName());
			mi.setIcon(new ColorIcon(sub.getColor()));

			mi.addAction(new AbstractAction("", IconLoader.getIcon("edit-delete")) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (Messagebox.showDeleteConfirm(getParent(), "Möchten Sie «" + sub.getName() + "» wirklich löschen?")) {
						m.removeModel(sub);
					}
				}
			});
			mi.addAction(new AbstractAction("", IconLoader.getIcon("edit")) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					final Vector<String> usedNames = new Vector<String>();

					for (SubModel s : m) {
						if (s != sub) {
							usedNames.add(s.getName());
						}
					}

					TextEditDialog dlg = new TextEditDialog(getParent(), "Namen für Submodell eingeben", new TextEditDialog.TextChecker() {

						@Override
						public boolean isValidText(String text) {
							return !usedNames.contains(text);
						}

						@Override
						public String getErrorDescription() {
							return "Der Name ist bereits vergeben";
						}
					});
					dlg.setTitle("Simulation");
					dlg.setText(sub.getName());
					dlg.setVisible(true);

					String text = dlg.getText();
					if (text != null) {
						sub.setName(text);
						m.fireModelChanged(sub);
					}
				}
			});

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

		SplitMenuitem madd = new SplitMenuitem("Neus Submodell erstellen");
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
