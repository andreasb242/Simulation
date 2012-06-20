/* @author rich
 * Created on 19-Dec-2003
 */
package org.lsmp.djep.matrixJep;

import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.djep.DSymbolTable;
import org.lsmp.djep.djep.diffRules.MultiplyDiffRule;
import org.lsmp.djep.djep.diffRules.PassThroughDiffRule;
import org.lsmp.djep.matrixJep.function.MDiff;
import org.lsmp.djep.matrixJep.function.MIf;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.vectorJep.function.Determinant;
import org.lsmp.djep.vectorJep.function.Diagonal;
import org.lsmp.djep.vectorJep.function.GetDiagonal;
import org.lsmp.djep.vectorJep.function.Length;
import org.lsmp.djep.vectorJep.function.Size;
import org.lsmp.djep.vectorJep.function.Trace;
import org.lsmp.djep.vectorJep.function.Transpose;
import org.lsmp.djep.vectorJep.function.VEle;
import org.lsmp.djep.vectorJep.function.VSum;
import org.lsmp.djep.vectorJep.values.Scaler;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.Node;
import org.nfunk.jep.Operator;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.ParserConstants;
import org.nfunk.jep.function.Power;

/**
 * An extension of JEP which allows advanced vector and matrix handling and
 * differentation.
 * 
 * @author Rich Morris Created on 19-Dec-2003
 */
public class MatrixJep extends DJep {

	private MatrixPreprocessor dec = new MatrixPreprocessor();
	private MatrixVariableFactory mvf = new MatrixVariableFactory();
	private MatrixEvaluator mev = new MatrixEvaluator();

	public MatrixJep() {
		super();
		nf = new MatrixNodeFactory(this);
		symTab = new DSymbolTable(mvf);
		opSet = new MatrixOperatorSet();
		this.parser.setInitialTokenManagerState(ParserConstants.NO_DOT_IN_IDENTIFIERS);

		Operator tens = ((MatrixOperatorSet) opSet).getMList();
		pv.addSpecialRule(tens, (PrintVisitor.PrintRulesI) tens.getPFMC());
		Operator eleAccess = ((MatrixOperatorSet) opSet).getElement();
		pv.addSpecialRule(eleAccess, (PrintVisitor.PrintRulesI) eleAccess.getPFMC());

		addDiffRule(new PassThroughDiffRule(tens.getName(), tens.getPFMC()));
		Operator cross = ((MatrixOperatorSet) opSet).getCross();
		addDiffRule(new MultiplyDiffRule(cross.getName(), cross));
		Operator dot = ((MatrixOperatorSet) opSet).getDot();
		addDiffRule(new MultiplyDiffRule(dot.getName(), dot));
	}

	@Override
	public void addStandardFunctions() {
		super.addStandardFunctions();
		addFunction("pow", new Power());
		this.getFunctionTable().remove("if");
		addFunction("if", new MIf());
		addFunction("ele", new VEle());
		this.getFunctionTable().remove("diff");
		addFunction("diff", new MDiff());
		addFunction("len", new Length());
		addFunction("size", new Size());
		addFunction("diag", new Diagonal());
		addFunction("getdiag", new GetDiagonal());
		addFunction("trans", new Transpose());
		addFunction("det", new Determinant());
		addFunction("trace", new Trace());
		addFunction("vsum", new VSum());
	}

	/**
	 * Evaluate a node. If the result is a scaler it will be unwrapped, i.e. it
	 * will return a Double and not a Scaler.
	 */
	@Override
	public Object evaluate(Node node) throws ParseException {
		Object res = mev.evaluate((MatrixNodeI) node, this);
		if (res instanceof Scaler) {
			return ((Scaler) res).getEle(0);
		}
		return res;
	}

	/** Evaluate a node. Does not unwrap scalers. */
	public Object evaluateRaw(Node node) throws ParseException {
		Object res = mev.evaluate((MatrixNodeI) node, this);
		return res;
	}

	/**
	 * Pre-processes an equation to allow the diff and eval operators to be
	 * used.
	 */
	@Override
	public Node preprocess(Node node) throws ParseException {
		return dec.preprocess(node, this);
	}

	@Override
	public Object getValueAsObject() {
		try {
			Object res = mev.evaluate((MatrixNodeI) getTopNode(), this);
			if (res instanceof Scaler) {
				return ((Scaler) res).getEle(0);
			}
			return res;
		} catch (Exception e) {
			this.errorList.addElement("Error during evaluation:");
			this.errorList.addElement(e.getMessage());
			return null;
		}
	}
}
