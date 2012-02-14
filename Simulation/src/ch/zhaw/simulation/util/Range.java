package ch.zhaw.simulation.util;

public class Range {
	private int max;
	private int min;
	
	public Range(int initial) {
		max = initial;
		min = initial;
	}
	
	public void add(int value) {
		max = Math.max(value, max);
		min = Math.min(value, min);
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMin() {
		return min;
	}
}
