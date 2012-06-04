package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.plugin.StandardParameter;

/**
 * @author: bachi
 */
public interface MatlabParameter extends StandardParameter {
	public static String WORKPATH = "matlab.workpath";
	public static String DEFAULT_WORKPATH = System.getProperty("user.home");
	
	public static String TOOL = "matlab.tool";
	public static String DEFAULT_TOOL = "Octave";

	public static String EXEC_MATLAB_PATH = "matlab.exec.matlab";
	public static String EXEC_OCTAVE_PATH = "matlab.exec.octave";
	public static String EXEC_SCILAB_PATH = "matlab.exec.scilab";

	public static String DEFAULT_EXEC_MATLAB_PATH = "matlab";
	public static String DEFAULT_EXEC_OCTAVE_PATH = "octave";
	public static String DEFAULT_EXEC_SCILAB_PATH = "scilab";

	public static String JUST_GENERATE = "matlab.just_generate";
	public static boolean DEFAULT_JUST_GENERATE = false;
}
