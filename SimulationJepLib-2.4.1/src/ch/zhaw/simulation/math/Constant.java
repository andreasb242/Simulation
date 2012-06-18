package ch.zhaw.simulation.math;

public class Constant {
	public final String name;
	public final Object value;
	public final String description;

	public Constant(String name, Object value) {
		this(name, value, "<no description>");
	}
	
	public Constant(String name, Object value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}
}
