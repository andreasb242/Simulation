package ch.zhaw.simulation.menu.flow;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.undo.UndoHandler;

public class FlowMenubar extends AbstractMenubar {

	public FlowMenubar(Sysintegration sysintegration, UndoHandler um, ClipboardInterface clipboard, AbstractEditorControl<?> control) {
		super(sysintegration, um, clipboard, control, "XY Modell", SimulationType.XY_MODEL);
	}

	protected void addAdditionalSimulation() {
		addMenuItem(mSimulation, "Forml Übersicht", "overview", MenuToolbarActionType.FORMULA_OVERVIEW, sysmenu.getFormulaOverview());
		mSimulation.addSeparator();
	}

}
