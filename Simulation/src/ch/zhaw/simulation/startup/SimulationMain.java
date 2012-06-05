package ch.zhaw.simulation.startup;

import java.lang.reflect.Method;

import javax.swing.JOptionPane;

/**
 * Loads the main class, and can so catch "ClassNotFoundException"
 * 
 * @author Andreas Butti
 */
public class SimulationMain {
	public static void main(String[] args) {
		try {
			Class<?> cls = Class.forName("ch.zhaw.simulation.startup.SimulationStarter");
			Class<?>[] argTypes = new Class[] { String[].class };
			Method main = cls.getDeclaredMethod("main", argTypes);
			main.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();

			JOptionPane.showMessageDialog(null, "<html>(AB)² Simulation konnte nicht gestartet werden, weil Komponenten fehlen.<br>"
					+ "Installieren Sie die Applikation erneut.</html>", "(AB)² Simulation", JOptionPane.ERROR_MESSAGE);
		}
	}

}
