package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JLabel;

import org.jdesktop.swingx.action.OpenBrowserAction;

import butti.javalibs.errorhandler.Errorhandler;

public class UrlLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public UrlLabel(String url) {
		super(url);
		final OpenBrowserAction helpAction = new OpenBrowserAction();
		try {
			helpAction.setURI(new URI(url));
		} catch (Exception e) {
			Errorhandler.showError(e, "Konnte URL nicht öffnen");
		}

		setForeground(Color.BLUE);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				helpAction.actionPerformed(null);
			}

		});

		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
}
