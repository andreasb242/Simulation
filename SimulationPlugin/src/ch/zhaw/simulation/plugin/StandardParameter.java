package ch.zhaw.simulation.plugin;

public interface StandardParameter {
	public static String SIM_PROPERTY_DOUBLE_PREFIX = "simulation.dproperty.";
	public static String SIM_PROPERTY_STRING_PREFIX = "simulation.sproperty.";

	public static String DT = "simulation.dt";
	public static String START = "simulation.start";
	public static String END = "simulation.end";

	public static String H_FACTOR = "simulation.matlab.initstep";
	public static String MAX_STEP = "simulation.matlab.maxstep";
}
