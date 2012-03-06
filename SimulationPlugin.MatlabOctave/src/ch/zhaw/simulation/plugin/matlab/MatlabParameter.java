package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.plugin.StandardParameter;

/**
 * @author: bachi
 */
public interface MatlabParameter extends StandardParameter {
	public static String NUMERIC_METHOD = "simulation.matlab.numeric_method";
	public static String INIT_STEP = "simulation.matlab.initstep";
	public static String MAX_STEP = "simulation.matlab.maxstep";
}
