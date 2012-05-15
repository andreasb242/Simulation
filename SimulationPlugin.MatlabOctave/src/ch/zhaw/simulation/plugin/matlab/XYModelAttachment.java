package ch.zhaw.simulation.plugin.matlab;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.model.SimulationAttachment;
import ch.zhaw.simulation.plugin.matlab.optimizer.FlowModelOptimizer;
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
	private Node formula;
	private Node dataX;
	private Node dataY;
	private FlowModelOptimizer flowModelOptimizer;

	public XYModelAttachment() {
		super();
	}

	public void optimize(Parser.ParserNodePair parsed) throws ParseException {
		formula = privateOptimize(parsed);
	}

	public void optimizeDataX(Parser.ParserNodePair parsed) throws ParseException {
		dataX = privateOptimize(parsed);
	}

	public void optimizeDataY(Parser.ParserNodePair parsed) throws ParseException {
		dataY = privateOptimize(parsed);
	}

	private Node privateOptimize(Parser.ParserNodePair parsed) throws ParseException {
		MatrixJep j = parsed.jep;
		Node processed = j.preprocess(parsed.nodes.lastElement());

		// don't simplify node-tree!
		// if you do, grad() will get cut out
		return processed;
	}

	public String getPreparedFormula(PrintVisitor visitor) {
		return getPrivateFormula(formula, visitor);
	}

	public String getDataXFormula(PrintVisitor visitor) {
		return getPrivateFormula(dataX, visitor);
	}

	public String getDataYFormula(PrintVisitor visitor) {
		return getPrivateFormula(dataY, visitor);
	}

	private String getPrivateFormula(Node node, PrintVisitor visitor) {
		if (node == null) {
			throw new NullPointerException();
		}

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream s = new PrintStream(bo);
		visitor.print(node, s);
		s.close();
		return bo.toString();
	}


}
