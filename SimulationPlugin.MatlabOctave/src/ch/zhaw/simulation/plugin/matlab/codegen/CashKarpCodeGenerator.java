package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * @author: bachi
 */
public class CashKarpCodeGenerator extends AdaptiveMinorStepCodeGenerator {

	@Override
	protected void printButcherTableau(CodeOutput out) {
		out.printComment("Bucher tableau");
		out.println("sim_a = [ 1/5    3/40    3/10    -11/54     1631/55296");
		out.println("            0    9/40   -9/10       5/2        175/512");
		out.println("            0       0     6/5    -70/27      575/13824");
		out.println("            0       0       0    -35/27   44275/110592");
		out.println("            0       0       0         0       253/4096");
		out.println("            0       0       0         0              0 ];");
		out.newline();

		out.println("sim_b = [ -277/64512; 0; 6925/370944; -6925/202752; -277/14336; 277/7084 ];");
		out.newline();

		out.println("sim_c = [ 1/5, 3/10, 3/5, 1, 7/8];");
		out.newline();
	}
}
