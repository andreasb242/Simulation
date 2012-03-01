package ch.zhaw.simulation.clipboard;

import ch.zhaw.simulation.model.selection.SelectableElement;

public interface TransferableFactory {
	public AbstractTransferable createTransferable(SelectableElement[] selected);
}
