package ch.zhaw.simulation.editor.xy;

import javax.swing.JFrame;

import butti.javalibs.gui.BDialog;

public class SubmodelDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	
	
	public SubmodelDialog(JFrame parent) {
		super(parent);
		setTitle("Submodel bearbeiten");
		setModal(true);
		
	}
	
}
