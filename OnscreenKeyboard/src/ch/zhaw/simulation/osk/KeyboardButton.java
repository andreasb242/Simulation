package ch.zhaw.simulation.osk;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;

public class KeyboardButton extends AbstractButton {
	private static final long serialVersionUID = 1L;

	public KeyboardButton(String key) {
		setModel(new DefaultButtonModel());
		setUI(new KeyButtonUi());
		setText(key);
		setFocusable(true);
	}

}
