package ch.zhaw.simulation.osk;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import butti.javalibs.config.Config;
import butti.javalibs.util.AWTUtilitiesWrapper;

public class OnscreenKeyboardWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	private float fontSize;

	private Vector<KeyboardListener> listener = new Vector<KeyboardListener>();

	public OnscreenKeyboardWindow(Window parent, String title) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setTitle(title);

		JPanel p = (JPanel) getContentPane();
		p.setBackground(Config.get("keyboard.color.background", Color.BLACK));
		p.setOpaque(true);

		int gap = Config.get("keyboard.gap", 3);
		setLayout(new GridLayout(0, Config.get("keyboard.colums", 5), gap, gap));

		if (gap != 0) {
			p.setBorder(new EmptyBorder(gap, gap, gap, gap));
		}

		for (final String s : Config.getArray("keyboard.keys")) {
			KeyboardButton bt = new KeyboardButton(s);

			bt.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (KeyboardListener l : listener) {
						l.keyInserted(s);
					}
				}
			});

			add(bt);
		}

		fontSize = Config.get("keyboard.fontSize", 14);

		pack();
		setLocationRelativeTo(parent);
		AWTUtilitiesWrapper.setWindowOpacity(this, Config.get("keyboard.opacity", 80) / 100f);
	}

	public void addKeyboardListener(KeyboardListener l) {
		listener.add(l);
	}

	public void removeKeyboardListener(KeyboardListener l) {
		listener.remove(l);
	}

	public void setKeyboardFont(Font f) {
		f = f.deriveFont(fontSize);

		JPanel p = (JPanel) getContentPane();
		for (int i = 0; i < p.getComponentCount(); i++) {
			Component c = p.getComponent(i);
			c.setFont(f);
		}

		pack();
	}
}
