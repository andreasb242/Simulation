package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * @author: bachi
 */
public class DormandPrinceCodeGenerator extends AdaptiveStepCodeGenerator {

	protected void printButcherTableau(CodeOutput out) {
		out.println("sim_a = [ 1/5    3/40   44/45   19372/6561    9017/3168      35/384");
		out.println("            0    9/40  -56/15  -25360/2187      -355/33           0");
		out.println("            0       0    32/9   64448/6561   46732/5247    500/1113");
		out.println("            0       0       0     -212/729       49/176     125/192");
		out.println("            0       0       0            0  -5103/18656  -2187/6784");
		out.println("            0       0       0            0            0       11/84");
		out.println("            0       0       0            0            0           0 ];");
		out.newline();

		out.println("sim_b = [ 71/57600; 0; -71/16695; 71/1920; -17253/339200; 22/525; -1/40 ];");
		out.newline();

		out.println("sim_c = [ 1/5, 3/10, 4/5, 8/9, 1, 1];");
		out.newline();
	}

}
