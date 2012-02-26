package ch.zhaw.simulation.clipboard;

import ch.zhaw.simulation.model.flow.selection.SelectableElement;

public interface TransferableFactory {
	public AbstractTransferable createTransferable(SelectableElement[] selected);
}
