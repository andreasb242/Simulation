package ch.zhaw.simulation.plugin.matlab.codegen;


/**
 * @author: bachi
 */
public class FehlbergCodeGenerator extends AdaptiveMinorStepCodeGenerator {

	@Override
	protected void printButcherTableau(CodeOutput out) {
		out.printComment("Bucher tableau");
		out.println("sim_a = [ 1/4    3/32   1932/2197     439/216        -8/27");
		out.println("            0    9/32  -7200/2197          -8            2");
		out.println("            0       0   7296/2197    3680/513   -3544/2565");
		out.println("            0       0           0   -845/4104    1859/4104");
		out.println("            0       0           0           0       -11/40");
		out.println("            0       0           0           0            0 ];");
		out.newline();

		out.println("sim_b = [ -1/360; 0; 128/4275; 2197/75240; -1/50; -22/55 ];");
		out.newline();

		out.println("sim_c = [ 1/4, 3/8, 12/13, 1, 1/2];");
		out.newline();
	}
}
