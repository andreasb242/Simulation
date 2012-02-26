package ch.zhaw.simulation.editor.xy;

import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.view.AbstractEditorView;

public class XYEditorView extends AbstractEditorView<XYEditorControl>{
	private static final long serialVersionUID = 1L;

	public XYEditorView(XYEditorControl control, TransferableFactory factory) {
		super(control, factory);
	}

}
