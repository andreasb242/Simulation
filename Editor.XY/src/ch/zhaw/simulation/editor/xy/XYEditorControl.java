package ch.zhaw.simulation.editor.xy;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.XYModel;

public class XYEditorControl extends AbstractEditorControl<XYModel> {

	private XYEditorView view;
	
	public XYEditorControl(JFrame parent, Settings settings) {
		super(parent, settings);
		
		view = new XYEditorView(this);
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		// TODO Auto-generated method stub

	}

	@Override
	public XYEditorView getView() {
		return view;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}
