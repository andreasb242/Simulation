package ch.zhaw.simulation.sim.mo;

import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.ParseException;

public class MOVisitor extends PrintVisitor {
	@Override
	public Object visit(ASTVarNode node, Object data) throws ParseException {
		if ("time".equals(node.getName())) {
			sb.append("_time");
		} else if ("dt".equals(node.getName())) {
			sb.append("_dt");
		} else {
			sb.append(node.getName() + ".value");
		}
		return data;
	}
}
