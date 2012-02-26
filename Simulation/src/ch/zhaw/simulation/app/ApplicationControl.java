package ch.zhaw.simulation.app;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.dialog.aboutdlg.AboutDialog;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.math.console.MatrixConsole;
import ch.zhaw.simulation.window.flow.FlowWindow;
import ch.zhaw.simulation.window.xy.XYWindow;

public class ApplicationControl implements SimulationApplication {

	private JFrame mainFrame;

	public ApplicationControl() {
	}

	public void start(Settings settings, String openfile) {
		int x = 2;
		if (x == 1) {
			XYEditorControl control = new XYEditorControl();
			XYWindow win = new XYWindow(control,new ClipboardInterface() {
				
				@Override
				public void removeListener(ClipboardListener l) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void addListener(ClipboardListener l) {
					// TODO Auto-generated method stub
					
				}
			});
			
			win.setVisible(true);
			

		} else {
			FlowWindow win = new FlowWindow();
			FlowEditorControl control = new FlowEditorControl(this, win, settings);
			win.init(control, control.getClipboard());
			win.addListener(control);
//			control.getUndoManager().addUndoListener(l)
//			getUndoManager().addUndoListener(mb);

			
			//this.mainFrame = new FlowFrame(this, settings, openfile);
			
			this.mainFrame = win;
			mainFrame.setVisible(true);
		}
	}

	public void showAboutDialog() {
		AboutDialog dlg = new AboutDialog(mainFrame);
		dlg.setVisible(true);
	}

	@Override
	public void showMathConsole() {
		new MatrixConsole().setVisible(true);
	}
}
