package ch.zhaw.simulation.osk;

import java.awt.Font;
import java.io.IOException;

import ch.zhaw.simulation.osk.OnscreenKeyboardWindow;

import butti.javalibs.config.Config;

public class KeyboardTest {
	public static void main(String[] args) throws IOException {
		Config.initConfig("(AB)²Simulation", "AB2Simulation");
		OnscreenKeyboardWindow kb = new OnscreenKeyboardWindow(null, "Spezialzeichen Tastatur");
		kb.setKeyboardFont(new Font("Sans", Font.BOLD, 12));

		kb.addKeyboardListener(new KeyboardListener() {

			@Override
			public void keyInserted(String s) {
				System.out.println("->" + s);
			}
		});

		kb.setVisible(true);

	}
}
