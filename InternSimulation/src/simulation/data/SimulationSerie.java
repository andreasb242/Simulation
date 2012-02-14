package simulation.data;

import java.awt.Color;
import java.util.Vector;

public class SimulationSerie {
	private String name;
	private Vector<SimulationEntry> data = new Vector<SimulationEntry>();
	private String type = null;
	private Color color;
	private boolean visibel = true;

	private double min;
	private double max;
	private double constValue;
	private boolean isConstValue = false;

	public SimulationSerie(String name) {
		this.name = name;
	}

	public void add(double time, double value) {
		data.add(new SimulationEntry(time, value));
	}

	public void setOtherType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setVisibel(boolean visibel) {
		this.visibel = visibel;
	}

	public boolean isVisibel() {
		return visibel;
	}

	public Vector<SimulationEntry> getData() {
		return data;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMin() {
		return min;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMax() {
		return max;
	}

	public double getMaxY() {
		if (data.isEmpty()) {
			System.out.println("dataset " + getName() + " is empty!");
			return 0;
		}
		return data.lastElement().time;
	}

	public void add(double time, Object v) {
		if (v instanceof Double) {
			add(time, ((Double) v).doubleValue());
		} else {
			// TODO: Unbekannte Typen handeln
			setOtherType(v.getClass().getName());
		}
	}

	public void setConbstValue(double value) {
		this.constValue = value;
		isConstValue = true;
	}

	public boolean isConstValue() {
		return isConstValue;
	}

	public double getConstValue() {
		return constValue;
	}
}
