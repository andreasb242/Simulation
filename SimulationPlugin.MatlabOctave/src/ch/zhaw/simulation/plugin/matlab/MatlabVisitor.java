package ch.zhaw.simulation.plugin.matlab;

import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.ParseException;

public class MatlabVisitor extends PrintVisitor {

	private String prefix;

	public MatlabVisitor() {
		prefix = "";
	}

	public MatlabVisitor(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Object visit(ASTVarNode node, Object data) throws ParseException {
		if ("time".equals(node.getName())) {
			sb.append("sim_time");
		} else if ("dt".equals(node.getName())) {
			sb.append("sim_dt");
		} else {
			sb.append(prefix + node.getName() + ".value");
		}
		return data;
	}
}
