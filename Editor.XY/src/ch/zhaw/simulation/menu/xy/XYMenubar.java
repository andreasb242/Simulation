package ch.zhaw.simulation.menu.xy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.menu.AbstractMenubar;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.undo.UndoHandler;

public class XYMenubar extends AbstractMenubar {

	public XYMenubar(Sysintegration sysintegration, UndoHandler um, ClipboardInterface clipboard) {
		super(sysintegration, um, clipboard);
	}
	
	@Override
	protected void initSimulationMenu() {
		addMenuItem(mSimulation, "Simulationsgrösse", "size", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		}, null);
		
		super.initSimulationMenu();
	}

}
