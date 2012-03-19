package ch.zhaw.simulation.diagram;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DiagramTest {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		
		DiagramFrame frame = new DiagramFrame();
		frame.setVisible(true);
	}
}
