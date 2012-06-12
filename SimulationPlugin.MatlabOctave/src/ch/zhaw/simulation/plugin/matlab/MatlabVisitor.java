package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.xy.SubModel;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.ParseException;

public class MatlabVisitor extends PrintVisitor {

	private String prefix;
	private SubModel submodel;

	public MatlabVisitor() {
		prefix = "";
	}

	public MatlabVisitor(String prefix) {
		this.prefix = prefix;
		this.submodel = null;
	}

	public MatlabVisitor(String prefix, SubModel submodel) {
		this.prefix = prefix;
		this.submodel = submodel;
	}

	@Override
	public Object visit(ASTVarNode node, Object data) throws ParseException {
		boolean found = false;

		if ("time".equals(node.getName())) {
			sb.append("sim_time");
		} else if ("dt".equals(node.getName())) {
			sb.append("sim_dt");
		} else {
			if (submodel != null) {
				// Replace density-container (placeholder) with real density at a specific position
				for (SimulationDensityContainerData containerData : submodel.getModel().getSimulationDensityContainer()) {
					if (containerData.getName().equals(node.getName())) {
						found = true;
						sb.append("density." + containerData.getDensity().getName() + ".matrix(position.y.value, position.x.value)");
						break;
					}
				}
			}
			if (!found) {
				sb.append(prefix + node.getName() + ".value");
			}
		}
		return data;
	}
}
