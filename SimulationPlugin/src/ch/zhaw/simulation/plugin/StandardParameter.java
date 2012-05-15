package ch.zhaw.simulation.plugin;

public interface StandardParameter {
	/**
	 * Simulation settings
	 */
	public static String SIM_PROPERTY_DOUBLE_PREFIX = "simulation.dproperty.";
	public static String SIM_PROPERTY_STRING_PREFIX = "simulation.sproperty.";

	public static String DT = "simulation.sidebar.dt";
	public static String START = "simulation.sidebar.start";
	public static String END = "simulation.sidebar.end";

	public static double DEFAULT_DT = 0.1;
	public static double DEFAULT_START = 0;
	public static double DEFAULT_END = 10;

	public static String H_FACTOR = "simulation.sidebar.hfactor";
	public static String MAX_STEP = "simulation.sidebar.maxstep";
	public static String TOLERANCE = "simulation.sidebar.tolerance";

	public static double DEFAULT_H_FACTOR = 1.25;
	public static double DEFAULT_MAX_STEP = 0.5;
	public static double DEFAULT_TOLERANCE = 0.001;
}
