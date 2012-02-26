package ch.zhaw.simulation.inexport.dynasys;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.swing.JPanel;

import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.inexport.gui.settings.DynasysImportSettings;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;

import butti.javalibs.config.Settings;

/**
 * Fileformat based on original Delphi source: http://code.google.com/p/dynasys/
 * 
 * @author Andreas Butti
 * 
 */
public class DynasisReader extends BinaryImport {
	private String name;
	private String formula;
	private Point pos;

	private static final boolean DEBUG = true;

	private Vector<TmpParameterArrow> tmpParameterArrow = new Vector<TmpParameterArrow>();
	private Vector<Object> objects = new Vector<Object>();

	private Vector<SimulationObject> readData = new Vector<SimulationObject>();
	private Vector<Connector<?>> readConnectors = new Vector<Connector<?>>();

	private DynasysModel model;
	private String description;
	
	private Settings settings;

	public DynasisReader() {
	}

	@Override
	public void init(Settings settings) {
		this.settings = settings;
		this.model = new DynasysModel(settings);
	}

	@Override
	protected boolean checkFile(InputStream in) throws IOException {
		if (in != null) {
			this.in = in;
		}

		// 3035401 (4 Byte) "Magic bytes"
		if (3035401 != readInt()) {
			return false;
		}

		if (in != null) {
			this.in = null;
		}

		return true;
	}

	@Override
	public void read(File file) throws IOException, ImportException {
		openFile(file);
		// 3035401 (4 Byte) "Magic bytes"
		if (!checkFile(null)) {
			throw new ImportException("Kein Dynasis File!");
		}

		int count = readInt();
		debug("count: " + count);

		for (int i = 0; i < count; i++) {
			String className = readString();
			debug("class name:" + className);

			Object read = new Object();
			if ("TZustandObjekt".equals(className)) {
				read = readContainer();
			} else if ("TWertObjekt".equals(className)) {
				read = readParameter();
			} else if ("TVentilObjekt".equals(className)) {
				read = readVentilObjekt();
			} else if ("TWolkeObjekt".equals(className)) {
				read = readInfiniteData();
			} else if ("TWirkPfeilObjekt".equals(className)) {
				read = readWirkPfeilObjekt();
			} else {
				throw new ImportException("Unknown Object: " + className);
			}

			objects.add(read);
		}

		debug("File read, connect flow arrows");

		dumpObjects();

		connectFlowArrows();

		debug("connect paramater arrows");

		connectParameterArrow();

		if (model.isRealign()) {
			calcPadding();
		}

		readData(44);

		// int i;
		// while((i = in.read()) != -1) {
		// System.out.println("->" + i + ": " + (char)i);
		// }

		description = readString();

		// StartZeit:=R.ReadFloat;
		// EndZeit:=R.ReadFloat;
		// dt:=R.ReadFloat;

		/*
		 * ModellInfo.ReadData(R);
		 */
	}

	private void dumpObjects() {
		if (!DEBUG) {
			return;
		}

		System.out.println("\nDUMP OBJECTS:\n----------------------");

		for (Object o : objects) {
			System.out.println("|" + o.getClass());

			if (o instanceof InfiniteData) {
				InfiniteData d = (InfiniteData) o;
				System.out.println("|id: " + d.getId());
			} else if (o instanceof SimulationParameter) {
				SimulationParameter p = (SimulationParameter) o;
				System.out.println("|id: " + p.getId());
				System.out.println("|name: " + p.getName());
				System.out.println("|formula: " + p.getFormula());
			} else if (o instanceof SimulationContainer) {
				SimulationContainer p = (SimulationContainer) o;
				System.out.println("|id: " + p.getId());
				System.out.println("|name: " + p.getName());
				System.out.println("|formula: " + p.getFormula());
			} else if (o instanceof TmpParameterArrow) {
				TmpParameterArrow p = (TmpParameterArrow) o;
				System.out.println("|from " + p.getSource() + " to " + p.getTarget());
			} else if (o instanceof TmpFlow) {
				TmpFlow f = (TmpFlow) o;
				System.out.println("|from " + f.getSource() + " to " + f.getTarget());
			}

			System.out.println("-------");
		}
		System.out.println("\n");
	}

	private void debug(String string) {
		if (DEBUG) {
			System.out.println(string);
		}
	}

	private void calcPadding() {
		int x = Integer.MAX_VALUE;
		int y = Integer.MAX_VALUE;

		for (SimulationObject s : readData) {
			x = Math.min(x, s.getX());
			y = Math.min(y, s.getY());
		}

		for (Connector<?> c : readConnectors) {
			if (c instanceof FlowConnector) {
				FlowValve p = ((FlowConnector) c).getValve();
				x = Math.min(x, p.getX());
				y = Math.min(y, p.getY());
			}
		}

		for (Object o : objects) {
			if (o instanceof InfiniteData) {
				InfiniteData i = (InfiniteData) o;
				x = Math.min(x, i.getX());
				y = Math.min(y, i.getY());
			}
		}

		int dX = -x + model.getPaddingLeft();
		int dY = -y + model.getPaddingTop();

		for (SimulationObject s : readData) {
			s.move(dX, dY);
		}

		for (Connector<?> c : readConnectors) {
			if (c instanceof FlowConnector) {
				FlowValve p = ((FlowConnector) c).getValve();
				p.move(dX, dY);
			}
		}

		for (Object o : objects) {
			if (o instanceof InfiniteData) {
				InfiniteData i = (InfiniteData) o;
				i.move(dX, dY);
			}
		}
	}

	private void connectFlowArrows() throws ImportException {
		for (int i = 0; i < objects.size(); i++) {
			Object o = objects.get(i);
			if (o instanceof TmpFlow) {
				TmpFlow flow = (TmpFlow) o;

				Object oFrom = objects.get(flow.getSource());
				Object oTo = objects.get(flow.getTarget());

				if (!(oFrom instanceof SimulationObject) || !(oTo instanceof SimulationObject)) {
					throw new ImportException("Flow was from: " + oFrom.getClass() + " to " + oTo.getClass());
				}

				SimulationObject from = (SimulationObject) oFrom;
				SimulationObject to = (SimulationObject) oTo;

				FlowConnector conn = new FlowConnector(from, to);
				FlowValve par = conn.getValve();
				par.setName(flow.getName());
				par.setFormula(flow.getFormula());
				Point pos = flow.getPos();
				par.setX((int) (pos.x * model.getScaleFactor()) - par.getWidth() / 2);
				par.setY((int) (pos.y * model.getScaleFactor()) - par.getHeight() / 2 + model.getFlowPointMove());

				readConnectors.add(conn);
				objects.set(i, conn);
			}
		}
	}

	private void connectParameterArrow() throws ImportException {
		for (TmpParameterArrow flow : tmpParameterArrow) {
			Object oSource = objects.get(flow.getSource());
			Object oTarget = objects.get(flow.getTarget());

			if (oTarget instanceof FlowConnector) {
				FlowConnector flowConnector = (FlowConnector) oTarget;
				oTarget = flowConnector.getValve();
			}
			if (oSource instanceof FlowConnector) {
				FlowConnector flowConnector = (FlowConnector) oSource;
				oSource = flowConnector.getValve();
			}

			if (!(oSource instanceof NamedSimulationObject) || !(oTarget instanceof NamedSimulationObject)) {
				throw new ImportException("Connector was from: " + oSource.getClass() + " to " + oTarget.getClass());
			}

			NamedSimulationObject source = (NamedSimulationObject) oSource;
			NamedSimulationObject target = (NamedSimulationObject) oTarget;

			readConnectors.add(new ParameterConnector(source, target));
		}
	}

	private TmpParameterArrow readWirkPfeilObjekt() throws IOException, ImportException {
		readObject();

		int source = readLsb();
		int target = readLsb();

		TmpParameterArrow flow = new TmpParameterArrow(source, target);
		tmpParameterArrow.add(flow);

		readData(48);// 6 * TPoint (8)
		return flow;
	}

	private InfiniteData readInfiniteData() throws IOException, ImportException {
		readObject();

		readLsb();// VentilIndex

		InfiniteData d = new InfiniteData(0, 0);

		d.setX((int) (pos.x * model.getScaleFactor()) - d.getWidth() / 2);
		d.setY((int) (pos.y * model.getScaleFactor()) - d.getHeight() / 2);
		return d;
	}

	private Object readVentilObjekt() throws IOException, ImportException {
		readObject();

		int source = readLsb();
		int target = readLsb();

		return new TmpFlow(name, formula, pos, source, target);
	}

	private SimulationParameter readParameter() throws IOException, ImportException {
		readObject();

		int valueTable = in.read();

		if (valueTable != 0) {
			throw new ImportException("Data value Table import not supported!");
		}

		readData(32);

		// key:=4;
		// S.Read(xtbf,SizeOf(xtbf));
		// S.Read(xmin,SizeOf(xmin));
		// S.Read(xmax,SizeOf(xmax));
		// S.Read(ymin,SizeOf(ymin));
		// S.Read(ymax,SizeOf(ymax));
		// Tabelle:=nil;
		//
		// if xtbf<>NoTab then begin
		// S.Read(count,SizeOf(count));
		// if count>0 then begin
		// tabelle:=TList.Create;
		// for i:=1 to count do begin
		// x:=S.ReadFloat;
		// y:=S.ReadFloat;
		// punkt:=TPunkt.create;punkt.init(x,y);
		// tabelle.add(Punkt)
		// end;
		// end
		// end

		SimulationParameter p = new SimulationParameter(0, 0);
		readData(p);
		p.setFormula(formula);

		readData.add(p);

		return p;
	}

	private void readData(NamedSimulationObject s) {
		s.setName(name);

		int x = (int) (pos.x * model.getScaleFactor()) - s.getWidth() / 2;
		int y = (int) (pos.y * model.getScaleFactor()) - s.getHeight() / 2;

		s.setX(x);
		s.setY(y);
	}

	private SimulationContainer readContainer() throws IOException, ImportException {
		readObject();
		readData(88);

		SimulationContainer p = new SimulationContainer(0, 0);
		readData(p);
		p.setFormula(formula);

		readData.add(p);

		return p;
	}

	private void readObject() throws IOException, ImportException {
		readLsb(); // width (unused)
		readLsb(); // heigth (unused)
		this.name = readString(33);
		this.formula = readString(254);

		parseFormula();

		// x1, x2, y1, y2
		Rectangle rect = readRect();
		this.pos = new Point((rect.x + rect.width) / 2, (rect.y + rect.height) / 2);

		readI(); // If the formula contains errors(0) or is OK(1), not used
		readI(); // Outgoing count, unused

		// S.Read(AusgangMax,SizeOF(AusgangMax)); // integer
		// S.Read(EingangMax,SizeOF(EingangMax)); // integer
		// S.Read(Ausgaenge,SizeOf(Ausgaenge)); // Array[10]
		// S.Read(Eingaenge,SizeOf(Eingaenge)); // Array[10]
		for (int i = 0; i < 163; i++) {
			in.read();
		}

		// S.Read(Mitte,SizeOf(Mitte)); // TPoint(int, int)
		// Center
		readI();
		readI();
	}

	private void parseFormula() {
		if (formula.isEmpty()) {
			return;
		}

		// Komma durch Punkt ersetzten
		formula = formula.replaceAll("([0-9]),([0-9])", "$1.$2");
	}

	public boolean load(SimulationFlowModel model) {
		model.clear();

		model.putMetainf("imported.type", "Dynasis");

		for (SimulationObject s : readData) {
			model.addData(s);
		}

		for (Connector<?> c : readConnectors) {
			model.addConnector(c);
		}

		for (Object o : objects) {
			if (o instanceof InfiniteData) {
				InfiniteData i = (InfiniteData) o;
				model.addData((InfiniteData) i);
			}
		}

		final int width = 300;
		final int padding = 10;

		if (!"".equals(description)) {
			int maxY = 0;
			for (SimulationObject d : model.getData()) {
				if (width > d.getX() - padding) {
					int y = d.getHeight() + d.getY();
					if (maxY < y) {
						maxY = y;
					}
				}
			}

			TextData txt = new TextData(padding, maxY + padding);
			txt.setText(description);
			txt.setWidth(width);
			txt.setHeight(200);

			model.addData(txt);
		}

		return true;
	}

	private class TmpFlow {
		private String name;
		private String formula;
		private Point pos;
		private int source;
		private int target;

		public TmpFlow(String name, String eingabe, Point pos, int source, int target) {
			this.name = name;
			this.formula = eingabe;
			this.pos = pos;
			this.source = source;
			this.target = target;
		}

		public int getSource() {
			return source;
		}

		public int getTarget() {
			return target;
		}

		public String getName() {
			return name;
		}

		public String getFormula() {
			return formula;
		}

		public Point getPos() {
			return pos;
		}
	}

	private class TmpParameterArrow {
		private int source;
		private int target;

		public TmpParameterArrow(int from, int to) {
			this.source = from;
			this.target = to;
		}

		public int getTarget() {
			return target;
		}

		public int getSource() {
			return source;
		}
	}

	@Override
	public boolean load() throws Exception {
		return true;
	}

	@Override
	public void unload() {
	}

	@Override
	public String[] getFileExtension() {
		return new String[] { ".dyn" };
	}
	
	@Override
	public JPanel getSettingsPanel() {
		return new DynasysImportSettings(this.settings);
	}
}
