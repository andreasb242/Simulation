package ch.zhaw.simulation.editor.status;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXStatusBar;

import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.status.StatusListener;

public class StatusLabelHandler implements StatusListener {
	private JXStatusBar sBar = new JXStatusBar();
	private JLabel lbStatus = new JLabel(" ");
	private boolean emptyStatus = true;

	public StatusLabelHandler() {
		sBar.add(lbStatus);
	}

	@Override
	public void clearStatus() {
		if (emptyStatus) {
			return;
		}
		lbStatus.setIcon(null);
		setStatusText(" ");
		emptyStatus = true;

	}

	@Override
	public void setStatusText(String text) {
		lbStatus.setText(text);
		lbStatus.setIcon(null);
		emptyStatus = false;
	}

	@Override
	public void setStatusTextInfo(String text) {
		setStatusText(text);
		lbStatus.setIcon(IconSVG.getIcon("info", 22));
		emptyStatus = false;
	}

	public JXStatusBar getStatusBar() {
		return sBar;
	}

	public void dispose() {
		this.sBar = null;
		this.lbStatus = null;
	}

}
