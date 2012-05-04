package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class DensityEditorDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private EditorPanel editor;

	public DensityEditorDialog(JFrame parent, SimulationXYModel model, Sysintegration sys) {
		super(parent);
		setTitle("Dichte bearbeiten");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		editor = new EditorPanel(model, sys);

		add(editor);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				editor.saveContents();
				editor.unselecet();
			}
		});
		
		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	public void setSelectedDensity(DensityData d) {
		editor.setSelected(d);
	}

	@Override
	public void dispose() {
		editor.dispose();
		super.dispose();
	}

}
