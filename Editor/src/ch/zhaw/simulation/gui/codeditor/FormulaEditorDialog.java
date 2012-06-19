package ch.zhaw.simulation.gui.codeditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;

import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class FormulaEditorDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private FormulaEditorPanel editor;

	public FormulaEditorDialog(JFrame parent, Sysintegration sys, AbstractSimulationModel<?> model) {
		super(parent);

		editor = new FormulaEditorPanel(sys, model, new Vector<String>(), true);
		add(editor);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				editor.saveContents();
			}

		});
		setModal(true);
		new WindowPositionSaver(this);
	}

	public void setData(NamedFormulaData data, Vector<DensityData> density) {
		String title = data.getName();

		if (title == null) {
			title = "Unbekannt";
		}

		if (data instanceof SimulationContainerData) {
			title += " - Initialwert";
		} else if (data instanceof SimulationParameterData || data instanceof SimulationGlobalData) {
			title += " - Formel";
		} else if (data instanceof FlowValveData) {
			title += " - Fluss";
		} else {
			System.err.println("Unknonwn Type in FormularEditorDialog:  " + data.getClass());
		}

		setTitle(title);
		editor.setData(data, density);
	}

	public void unselect() {
		editor.unselect();
	}

	@Override
	public void dispose() {
		editor.dispose();
		super.dispose();
	}
}
