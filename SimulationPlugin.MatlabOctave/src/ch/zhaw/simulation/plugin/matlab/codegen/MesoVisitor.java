package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.*;

import java.util.Vector;

public class MesoVisitor extends PrintVisitor {

	private MesoData meso;
	private Vector<DensityData> density;

	public MesoVisitor(MesoData meso, Vector<DensityData> density) {
		this.meso = meso;
		this.density = density;
	}

	@Override
	public Object visit(ASTVarNode node, Object data) throws ParseException {
		System.out.println("variable : " + node.getName());
		return data;
	}

	@Override
	public Object visit(ASTFunNode node, Object data) throws ParseException {
		System.out.println("function: " + node.getName());
		return data;
	}

	@Override
	public Object visit(ASTConstant node, Object data) {
		System.out.println("const: " + node.getValue());
		return data;
	}

	@Override
	public Object visit(SimpleNode node, Object data) throws ParseException {
		System.out.println("simple: " + node.getId());
		return data;
	}
}
