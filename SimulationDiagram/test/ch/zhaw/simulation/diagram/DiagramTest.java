package ch.zhaw.simulation.diagram;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import butti.javalibs.config.Config;
import butti.javalibs.config.FileSettings;
import butti.javalibs.config.Settings;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

public class DiagramTest {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException,
			IOException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		Config.initConfig("Simulation");

		SimulationCollection collection = new SimulationCollection(0, 100);

		SimulationSerie s;
		s = new SimulationSerie("Aaaaaa");
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

		s = new SimulationSerie("Ccccccc");
		s.add(0, 15);
		s.add(10, 14);
		s.add(20, 13);
		s.add(30, 12);
		s.add(40, -30);
		s.add(50, -22);
		s.add(60, -17);
		s.add(70, -11);
		s.add(80, -2);
		s.add(90, 6);
		s.add(100, 5);

		collection.addSeries(s);

		// TODO testen nur mit diesen Daten
		// s = new SimulationSerie("Ddddddddd");
		// s.add(0, 1000);
		// s.add(10, 1020);
		// s.add(20, 1013);
		// s.add(30, 1012);
		// s.add(40, 1010);
		// s.add(50, 1000);
		// s.add(60, 1002);
		// s.add(70, 1003);
		// s.add(80, 1005);
		// s.add(90, 1006);
		// s.add(100, 1005);
		//
		// collection.addSeries(s);

		Settings settings = new FileSettings("settings.ini");

		SimulationConfiguration config = new SimulationConfiguration();
		DiagramFrame frame = new DiagramFrame(collection, settings, config, "Test", SysintegrationFactory.getSysintegration());

		frame.setVisible(true);
	}
}