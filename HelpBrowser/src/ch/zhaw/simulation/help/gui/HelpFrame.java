package ch.zhaw.simulation.help.gui;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.BDialog;

public class HelpFrame extends BDialog {
	private static final long serialVersionUID = 1L;
	private JTextPane html;

	public HelpFrame(JFrame parent) {
		super(parent);

		setTitle("Simulation Hilfe");

		html = new JTextPane();
		add(new JScrollPane(html));

		html.setEditable(false);
		html.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						html.setPage(e.getURL());
					} catch (IOException e1) {
						Errorhandler.logError(e1, "HelpURL not found!");
						e1.printStackTrace();
					}
				}
			}
		});

		html.setBorder(BorderFactory.createEmptyBorder());

		html.setOpaque(true);
		html.setBackground(Color.WHITE);

		try {
			URL url = new URL("man://html/index.html");
			html.setPage(url);
		} catch (Exception e) {
			Errorhandler.logError(e, "HelpURL not found!");
			e.printStackTrace();
		}

		setSize(800, 600);
		setLocationRelativeTo(parent);
	}

	public static void main(String[] args) {
		new HelpFrame(null).setVisible(true);
	}
}
