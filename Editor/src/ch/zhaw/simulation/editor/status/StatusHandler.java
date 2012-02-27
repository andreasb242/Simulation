package ch.zhaw.simulation.editor.status;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXStatusBar;

import ch.zhaw.simulation.icon.IconSVG;

public class StatusHandler {
	private JXStatusBar sBar = new JXStatusBar();
	private JLabel lbStatus = new JLabel(" ");
	private boolean emptyStatus = true;

	public StatusHandler() {
		sBar.add(lbStatus);
	}
	
	public void clearStatus() {
		if (emptyStatus) {
			return;
		}
		lbStatus.setIcon(null);
		setStatusText(" ");
		emptyStatus = true;
		
	}

	public void setStatusText(String text) {
		lbStatus.setText(text);
		lbStatus.setIcon(null);
		emptyStatus = false;
	}

	public void setStatusTextInfo(String text) {
		setStatusText(text);
		lbStatus.setIcon(IconSVG.getIcon("info", 22));
		emptyStatus = false;
	}
	public JXStatusBar getStatusBar() {
		return sBar;
	}

}
