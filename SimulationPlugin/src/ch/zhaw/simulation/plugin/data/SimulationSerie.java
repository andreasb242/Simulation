package ch.zhaw.simulation.plugin.data;

import java.util.Vector;

public class SimulationSerie {
	private String name;

	private Vector<SimulationEntry> data = new Vector<SimulationEntry>();
	private String type = null;
	private boolean visibel = true;

	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;
	private double constValue;
	private boolean isConstValue = false;

	private int chartId = -1;

	public SimulationSerie(String name) {
		this.name = name;
	}

	public void setChartId(int chartId) {
		this.chartId = chartId;
	}

	public int getChartId() {
		return chartId;
	}

	public void add(double time, double value) {
		if (value > max) {
			max = value;
		}

		if (value < min) {
			min = value;
		}
		data.add(new SimulationEntry(time, value));
	}

	public void setOtherType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
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
			setOtherType(v.getClass().getName());
		}
	}

	public void setConstValue(double value) {
		this.constValue = value;
		isConstValue = true;
	}

	public boolean isConstValue() {
		return isConstValue;
	}

	public double getConstValue() {
		return constValue;
	}

	@Override
	public String toString() {
		return name;
	}

	public int size() {
		return data.size();
	}
}
