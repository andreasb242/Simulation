package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.model.SimulationAttachment;
import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author: bachi
 */
public class XYModelAttachment implements SimulationAttachment {
	private Node formula = null;

	public XYModelAttachment() {
		super();
	}

	public void optimize(Parser.ParserNodePair parsed) throws ParseException {
		MatrixJep j = parsed.jep;
		Node processed = j.preprocess(parsed.nodes.lastElement());

		formula = j.simplify(processed);
	}

	public String getPreparedFormula(PrintVisitor visitor) {
		if (this.formula == null) {
			throw new NullPointerException();
		}

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream s = new PrintStream(bo);
		visitor.print((Node) this.formula, s);
		s.close();
		return bo.toString();
	}
}
