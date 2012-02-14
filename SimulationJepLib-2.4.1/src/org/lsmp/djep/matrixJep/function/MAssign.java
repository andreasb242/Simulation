package org.lsmp.djep.matrixJep.function;

import java.util.Stack;

import org.lsmp.djep.matrixJep.MatrixEvaluator;
import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.matrixJep.MatrixNodeFactory;
import org.lsmp.djep.matrixJep.MatrixPreprocessor;
import org.lsmp.djep.matrixJep.MatrixSpecialEvaluationI;
import org.lsmp.djep.matrixJep.MatrixVariable;
import org.lsmp.djep.matrixJep.MatrixVariableI;
import org.lsmp.djep.matrixJep.SpecialPreProcessorI;
import org.lsmp.djep.matrixJep.nodeTypes.ASTMFunNode;
import org.lsmp.djep.matrixJep.nodeTypes.ASTMVarNode;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Assign;
import org.nfunk.jep.function.LValueI;

/**
 * A matrix enabled assignment function. The lhs of an assignment must be a
 * variable.
 * 
 * @author Rich Morris Created on 23-Feb-2004
 */
public class MAssign extends Assign implements MatrixSpecialEvaluationI, SpecialPreProcessorI {
	public MAssign() {
		numberOfParameters = 2;
	}

	/**
	 * The run method should not be called. Use {@link #evaluate} instead.
	 */
	public void run(Stack<Object> s) throws ParseException {
		throw new ParseException("Eval should not be called by Evaluator");
	}

	/**
	 * A special methods for evaluating an assignment. When an assignment is
	 * encountered, first evaluate the rhs. Then set the value of the lhs to the
	 * result.
	 */
	public MatrixValueI evaluate(MatrixNodeI node, MatrixEvaluator visitor, MatrixJep j) throws ParseException {
		if (node.jjtGetNumChildren() != 2)
			throw new ParseException("Assignment operator must have 2 operators.");

		// evaluate the value of the right-hand side. Left on top of stack

		MatrixValueI rhsVal = (MatrixValueI) node.jjtGetChild(1).jjtAccept(visitor, null);

		// Set the value of the variable on the lhs.
		Node lhsNode = node.jjtGetChild(0);
		if (lhsNode instanceof ASTMVarNode) {
			ASTMVarNode vn = (ASTMVarNode) lhsNode;
			MatrixVariableI var = (MatrixVariableI) vn.getVar();
			var.setMValue(rhsVal);
			return rhsVal;
		} else if (lhsNode instanceof ASTMFunNode && ((ASTMFunNode) lhsNode).getPFMC() instanceof LValueI) {
			((LValueI) ((ASTMFunNode) lhsNode).getPFMC()).set(visitor, lhsNode, rhsVal);
			return rhsVal;
		}

		throw new ParseException("Assignment should have a variable for the lhs.");
	}

	public MatrixNodeI preprocess(ASTFunNode node, MatrixPreprocessor visitor, MatrixJep mjep, MatrixNodeFactory nf) throws ParseException {
		MatrixNodeI children[] = visitor.visitChildrenAsArray(node, null);

		if (node.jjtGetNumChildren() != 2)
			throw new ParseException("Operator " + node.getOperator().getName() + " must have two elements, it has " + children.length);
		Dimensions rhsDim = children[1].getDim();
		if (children[0] instanceof ASTVarNode) {
			MatrixVariable var = (MatrixVariable) ((ASTVarNode) children[0]).getVar();
			var.setDimensions(rhsDim);
			Node copy = mjep.deepCopy(children[1]);
			Node simp = mjep.simplify(copy);
			// Node preproc = (Node) simp.jjtAccept(this,data);
			var.setEquation(simp);
		}
		// TODO cope with ArrayAccess. Should really set the array access
		// equations
		return (ASTMFunNode) nf.buildOperatorNode(node.getOperator(), children, rhsDim);
	}

}
