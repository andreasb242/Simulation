package ch.zhaw.simulation.clipboard;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Vector;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;

import butti.javalibs.errorhandler.Errorhandler;


public class ClipboardHandler implements FlavorListener, ClipboardOwner, SelectionListener {

	private SimulationControl control;
	private Clipboard cp;
	private boolean lastCutCopyEnabled = false;

	private Vector<ClipboardListener> listener = new Vector<ClipboardListener>();

	public ClipboardHandler(SimulationControl control) {
		this.control = control;
		cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		cp.addFlavorListener(this);

		control.getSelectionModel().addSelectionListener(this);
	}

	@Override
	public void flavorsChanged(FlavorEvent e) {
		boolean supported = false;

		for (DataFlavor f : cp.getAvailableDataFlavors()) {
			if (f.equals(SimulationsTransferable.SIMULATION_FLOWER)) {
				supported = true;
				break;
			}
		}

		firePasteEnabled(supported);
	}

	public void addListener(ClipboardListener l) {
		listener.add(l);
	}

	public void removeListener(ClipboardListener l) {
		listener.remove(l);
	}

	private void fireCutCopyEnabled(boolean enabled) {
		for (ClipboardListener l : listener) {
			l.cutCopyEnabled(enabled);
		}
	}

	private void firePasteEnabled(boolean enabled) {
		for (ClipboardListener l : listener) {
			l.pasteEnabled(enabled);
		}
	}

	public void copy() {
		SelectableElement[] selected = control.getSelectionModel().getSelected();
		if (selected.length > 0) {
			cp.setContents(new SimulationsTransferable(selected, control.getModel()), this);
		}
	}

	public void copy(String contents) {
		cp.setContents(new StringSelection(contents), null);
	}

	public void paste() {
		try {
			Transferable contents = cp.getContents(this);
			ClipboardData transfer = (ClipboardData) contents.getTransferData(SimulationsTransferable.SIMULATION_FLOWER);

			transfer.addToModel(control);
		} catch (Exception e) {
			Errorhandler.showError(e, "Einfügen fehlgeschlagen!");
		}
	}

	public void cut() {
		copy();
		control.deleteSelected();
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	@Override
	public void selectionChanged() {
		boolean enabled = control.getSelectionModel().hasSelected();
		if (enabled != lastCutCopyEnabled) {
			lastCutCopyEnabled = enabled;
			fireCutCopyEnabled(enabled);
		}
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}
}
