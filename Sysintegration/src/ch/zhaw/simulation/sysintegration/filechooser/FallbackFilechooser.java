package ch.zhaw.simulation.sysintegration.filechooser;

import java.awt.Window;
import java.io.File;

import ch.zhaw.simulation.sysintegration.SimFileFilter;

public class FallbackFilechooser implements Filechooser {

	private Filechooser fallback;
	private Filechooser mainChooser;

	public FallbackFilechooser(Filechooser mainChooser, Filechooser fallback) {
		this.mainChooser = mainChooser;
		this.fallback = fallback;

		if (this.fallback == null) {
			throw new NullPointerException("fallback == null");
		}
	}

	@Override
	public File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		if (this.mainChooser != null) {
			try {
				return this.mainChooser.showSaveDialog(parent, filefilter, lastSavePath);
			} catch (FilechooserException e) {
				System.err.println("Main Filechooser not working, using fallback...");
				e.printStackTrace();
			}
		}

		return this.fallback.showSaveDialog(parent, filefilter, lastSavePath);
	}

	@Override
	public File showOpenDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		if (this.mainChooser != null) {
			try {
				return this.mainChooser.showOpenDialog(parent, filefilter, lastSavePath);
			} catch (FilechooserException e) {
				System.err.println("Main Filechooser not working, using fallback...");
				e.printStackTrace();
			}
		}

		return this.fallback.showOpenDialog(parent, filefilter, lastSavePath);
	}

}
