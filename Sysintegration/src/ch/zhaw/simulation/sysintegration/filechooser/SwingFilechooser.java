package ch.zhaw.simulation.sysintegration.filechooser;

import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;

import ch.zhaw.simulation.sysintegration.SimFileFilter;

public class SwingFilechooser extends AbstractFilechooser {

	@Override
	public File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		JFileChooser chooser = new JFileChooser(lastSavePath);
		chooser.setFileFilter(filefilter);
		if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return checkFileSaveExists(chooser.getSelectedFile().getAbsolutePath(), parent, filefilter, lastSavePath);
		}
		return null;
	}

	@Override
	public File showOpenDialog(Window parent, SimFileFilter filefilter, String lastSavePath) {
		JFileChooser chooser = new JFileChooser(lastSavePath);
		chooser.setFileFilter(filefilter);
		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

}
