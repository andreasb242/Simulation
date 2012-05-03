package ch.zhaw.simulation.editor.status;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXStatusBar;

import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.status.StatusListener;

/**
 * This is the statusbar for our editors
 * 
 * @author Andreas Butti
 */
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
		lbStatus.setIcon(IconLoader.getIcon("info", 22));
		emptyStatus = false;
	}

	@Override
	public void setStatusTextError(String text) {
		setStatusText(text);
		lbStatus.setIcon(IconLoader.getIcon("warning", 22));
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
