package ch.zhaw.simulation.diagram;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import butti.javalibs.config.Config;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

public class DiagramTest {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		Config.initConfig("Simulation");
		
		SimulationCollection collection = new SimulationCollection(0, 100);

		SimulationSerie s = new SimulationSerie("Aaaaaa");
		s.add(0, 0);
		s.add(10, 10);
		s.add(20, 11);
		s.add(30, 15);
		s.add(40, 20);
		s.add(50, 10);
		s.add(60, 15);
		s.add(70, -3);
		s.add(80, 0);
		s.add(90, 12);
		s.add(100, 20);

		collection.addSeries(s);

		s = new SimulationSerie("Bbbbbbb");
		s.add(0, 15);
		s.add(10, 14);
		s.add(20, 13);
		s.add(30, 12);
		s.add(40, 11);
		s.add(50, 10);
		s.add(60, 9);
		s.add(70, 8);
		s.add(80, 7);
		s.add(90, 6);
		s.add(100, 5);

		collection.addSeries(s);

		SimulationConfiguration config = new SimulationConfiguration();
		DiagramFrame frame = new DiagramFrame(collection, config, "Test", SysintegrationFactory.getSysintegration());

		frame.setVisible(true);
	}
}
