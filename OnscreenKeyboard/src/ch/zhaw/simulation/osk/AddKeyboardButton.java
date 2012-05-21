package ch.zhaw.simulation.osk;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

public class AddKeyboardButton {
	private AddKeyboardButton() {
	}

	public static JPanel add(JTextComponent txt) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(txt, BorderLayout.CENTER);

		JButton btKeyboard = new JButton("\u03B1\u03B2");
		btKeyboard.setToolTipText("Spezialzeichen Tastatur");
		TextComponentKeyboardAdapter adapter = new TextComponentKeyboardAdapter(txt);
		btKeyboard.addActionListener(adapter);
		
		p.add(btKeyboard, BorderLayout.EAST);

		return p;
	}

}
