package ch.zhaw.simulation.menu.xy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.undo.UndoHandler;

public class XYMenubar extends AbstractMenubar {

	public XYMenubar(Sysintegration sysintegration, UndoHandler um, ClipboardInterface clipboard, AbstractEditorControl<?> control) {
		super(sysintegration, um, clipboard, control, "Flow Modell", SimulationType.FLOW_SIMULATION);
	}

	@Override
	protected void initSimulationMenu() {
		super.initSimulationMenu();

		addMenuItem(mSimulation, "Modell Konfiguration", "size", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.XY_MODEL_SIZE));
			}
		}, null);
	}

}
