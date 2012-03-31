package ch.zhaw.simulation.gui.codeditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.gui.BDialog;

public class FormularEditorDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private FormulaEditorPanel editor;

	public FormularEditorDialog(JFrame parent, Sysintegration sys, AbstractSimulationModel<?> model) {
		super(parent);

		editor = new FormulaEditorPanel(sys, model, new Vector<String>());
		add(editor);

		pack();
		new WindowPositionSaver(this);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				editor.checkFormula();
			}

		});
		setLocationRelativeTo(parent);
	}

	public void setData(AbstractNamedSimulationData data) {
		setTitle(data.getName());
		editor.setData(data);
	}

	public void setTitle(String title) {
		super.setTitle(title + " - Formeleditor");
	}
	
	@Override
	public void dispose() {
		editor.dispose();
		super.dispose();
	}
}
