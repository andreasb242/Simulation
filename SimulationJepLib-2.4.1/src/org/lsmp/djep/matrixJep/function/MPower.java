/* @author rich
 * Created on 14-Feb-2005
 *
 * See LICENSE.txt for license information.
 */
package org.lsmp.djep.matrixJep.function;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.matrixJep.MatrixNodeFactory;
import org.lsmp.djep.matrixJep.MatrixPreprocessor;
import org.lsmp.djep.matrixJep.SpecialPreProcessorI;
import org.lsmp.djep.matrixJep.nodeTypes.ASTMFunNode;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.function.BinaryOperatorI;
import org.lsmp.djep.vectorJep.function.VPower;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Operator;
import org.nfunk.jep.ParseException;

/**
 * An overloaded Power function compatible with MatrixJep.
 * 
 * @author Rich Morris Created on 14-Feb-2005
 */
public class MPower extends VPower implements SpecialPreProcessorI {
	/**
	 * During preprocessing sets the function to the Cross function if
	 * necessary.
	 */
	public MatrixNodeI preprocess(ASTFunNode node, MatrixPreprocessor visitor, MatrixJep jep, MatrixNodeFactory nf) throws ParseException {
		MatrixNodeI children[] = visitor.visitChildrenAsArray(node, null);

		if (node.jjtGetNumChildren() != 2)
			throw new ParseException("Operator " + node.getOperator().getName() + " must have two elements, it has " + children.length);
		Dimensions lhsDim = children[0].getDim();
		Dimensions rhsDim = children[1].getDim();
		if (rhsDim.equals(Dimensions.ONE)) {
			Dimensions dim = lhsDim;
			return (ASTMFunNode) nf.buildOperatorNode(node.getOperator(), children, dim);
		}
		Operator op = jep.getOperatorSet().getCross();
		BinaryOperatorI bin = (BinaryOperatorI) op.getPFMC();
		Dimensions dim = bin.calcDim(lhsDim, rhsDim);
		return (ASTMFunNode) nf.buildOperatorNode(op, children, dim);
	}
}
