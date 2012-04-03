package clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import org.junit.Test;

import ch.zhaw.simulation.clipboard.flow.FlowTransferable;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.selection.SelectableElement;

public class ClipboardTest {
	@Test
	public void testFlowCopyPaste() {
		SimulationContainerData container = new SimulationContainerData(5, 5);
		container.setFormula("formula");
		container.setName("kurt");
		
		SelectableElement<?>[] selected = new SelectableElement<?>[]{ new TestSelectable(container) };
		SimulationFlowModel model = new SimulationFlowModel();
		FlowTransferable transferable = new FlowTransferable(null, selected, model);
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		cp.setContents(transferable, new ClipboardOwner() {
			
			@Override
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
			}
		});
	}

}
