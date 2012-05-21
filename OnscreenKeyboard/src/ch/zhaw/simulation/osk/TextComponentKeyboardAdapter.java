package ch.zhaw.simulation.osk;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class TextComponentKeyboardAdapter implements ActionListener {

	private JTextComponent txt;

	private static OnscreenKeyboardWindow osk;
	private static JTextComponent currentTxt;
	private static KeyboardListener listener = new KeyboardListener() {

		@Override
		public void keyInserted(String s) {
			try {
				if (currentTxt != null) {
					int pos = currentTxt.getSelectionEnd();
					currentTxt.getDocument().insertString(pos, s, null);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	};

	public TextComponentKeyboardAdapter(JTextComponent txt) {
		this.txt = txt;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (osk == null) {
			Container parent = txt.getParent();
			while (parent != null && !(parent instanceof Window)) {
				parent = parent.getParent();
			}

			if (parent != null) {
				((Window) parent).addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						osk.dispose();
						osk = null;
						currentTxt = null;
					}

				});
			}

			osk = new OnscreenKeyboardWindow((Window) parent, "Spezialzeichen Tastatur");
			osk.addKeyboardListener(listener);
		}

		osk.setVisible(true);

		currentTxt = txt;
	}

}
