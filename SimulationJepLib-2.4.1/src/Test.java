
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Gradient;
import ch.zhaw.simulation.math.Parser;

public class Test {
	public static void main(String[] args) throws ParseException {
		MatrixJep jep = new MatrixJep();
		jep.setAllowUndeclared(true);
		jep.setImplicitMul(true);
		jep.setAllowAssignment(true);
		// jep.getSymbolTable().addConstant("test", 15);
		jep.addStandardConstants();
		jep.addStandardDiffRules();
		jep.addStandardFunctions();
		// jep.addComplex();
		jep.getSymbolTable().remove("x");

		jep.addFunction("grad", new Gradient());
		Node node = jep.parse("grad(\"ab\", \"x\") + 12 + 3*(grad(\"ab\", \"x\") + 2)");
		node = jep.simplify(node);

		PrintVisitor visitor = new PrintVisitor() {
			public Object visit(ASTFunNode node, Object data) throws ParseException {
				if ("grad".equals(node.getName())) {
					return visitGrad(node);
				} else {
					return super.visit(node, data);
				}
			}

			/** prints a standard function: fun(arg,arg) */
			private Object visitGrad(ASTFunNode node) throws ParseException {
				sb.append("meinGradient(");

				Node dichte = node.jjtGetChild(0);

				if (!(dichte instanceof ASTConstant)) {
					System.out.println("error");
					throw new ParseException("sinnvoller text");
				}

				ASTConstant cDichte = (ASTConstant) dichte;

				Node richtung = node.jjtGetChild(1);

				sb.append("«" + cDichte.getValue() + "», ");

				if ("x".equals(richtung.toString())) {
					sb.append("horizontal");
				} else {
					sb.append("vertikal");
				}

				sb.append(")");

				return null;
			}

		};
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream s = new PrintStream(bo);
		visitor.print(node, s);
		s.close();
		System.out.println(bo.toString());

	}
}
