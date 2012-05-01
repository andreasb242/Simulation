package clipboard;

import static junit.framework.Assert.*;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.Vector;

import org.junit.Test;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.flow.FlowClipboardData;
import ch.zhaw.simulation.clipboard.flow.FlowTransferable;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionModel;

public class ClipboardTest {
	@Test
	public void testFlowCopyPaste() throws Exception {
		SimulationContainerData container = new SimulationContainerData(5, 6);
		container.setFormula("formula");
		container.setName("kurt");

		SelectableElement<?>[] selected = new SelectableElement<?>[] { new TestSelectable(container) };
		SimulationFlowModel model = new SimulationFlowModel();
		FlowTransferable transferable = new FlowTransferable(null, selected, model);
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();

		cp.setContents(transferable, new ClipboardOwner() {

			@Override
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
			}
		});

		// paste
		Transferable contents = cp.getContents(this);
		FlowClipboardData transfer = (FlowClipboardData) contents.getTransferData(AbstractTransferable.SIMULATION_FLOWER);

		SelectionModel selModel = new SelectionModel();
		SimulationFlowModel modelTarget = new SimulationFlowModel();

		transfer.addToModel(selModel, modelTarget, null);

		Vector<SimulationContainerData> containers = modelTarget.getSimulationContainer();
		assertEquals(containers.size(), 1);

		container = containers.get(0);

		assertEquals(container.getX(), 5);
		assertEquals(container.getY(), 6);
		assertEquals(container.getFormula(), "formula");
		assertEquals(container.getName(), "kurt");
	}

}
