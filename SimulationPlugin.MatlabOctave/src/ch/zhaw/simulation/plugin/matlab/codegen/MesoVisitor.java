package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.plugin.matlab.MatlabVisitor;
import org.nfunk.jep.*;

import java.util.Vector;

public class MesoVisitor extends MatlabVisitor {

	private MesoData meso;
	private Vector<DensityData> densityList;

	public MesoVisitor(MesoData meso, Vector<DensityData> density) {
		this.meso = meso;
		this.densityList = density;
	}

	@Override
	public Object visit(ASTFunNode node, Object data) throws ParseException {
		if ("grad".equals(node.getName())) {
			return visitGrad(node, data);
		} else {
			return super.visit(node, data);
		}
	}

	private Object visitGrad(ASTFunNode node, Object data) throws ParseException {
		Node densityNode;
		Node derivateNode;
		ASTConstant densityConst;
		ASTConstant derivateConst;
		boolean isValid = false;

		if (node.jjtGetNumChildren() != 2) {
			throw new ParseException("sinnvoller text");
		}

		densityNode = node.jjtGetChild(0);
		derivateNode = node.jjtGetChild(1);

		if(!(densityNode instanceof ASTConstant) || !(derivateNode instanceof ASTConstant)) {
			throw new ParseException("Keine Konstante");
		}

		densityConst = (ASTConstant) densityNode;
		derivateConst = (ASTConstant) derivateNode;

		for (DensityData density : densityList) {
			if (densityConst.getValue().equals(density.getName())) {
				isValid = true;
				break;
			}
		}

		if (!isValid) {
			throw new ParseException("Nicht valid");
		}

		/* d1.grad.dx(m0.position.approx.y.value, m0.position.approx.x.value)) */
		sb.append("density." + densityConst.getValue() + ".grad.d" + derivateConst.getValue() + "(");
		sb.append(meso.getName() + ".position.approx.y.value, " + meso.getName() + ".position.approx.x.value)");

		return data;
	}
}
