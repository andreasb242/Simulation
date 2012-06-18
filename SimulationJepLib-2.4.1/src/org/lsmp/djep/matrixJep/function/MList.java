/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/

package org.lsmp.djep.matrixJep.function;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.matrixJep.MatrixNodeFactory;
import org.lsmp.djep.matrixJep.MatrixOperatorSet;
import org.lsmp.djep.matrixJep.MatrixPreprocessor;
import org.lsmp.djep.matrixJep.SpecialPreProcessorI;
import org.lsmp.djep.matrixJep.nodeTypes.ASTMFunNode;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.function.NaryOperatorI;
import org.lsmp.djep.vectorJep.function.VList;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.Operator;
import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;

/**
 * A enhanced version of list, allows matrices and tensors.
 * 
 * @author Rich Morris Created on 27-Nov-2003
 */
@Category(CategoryType.UNDEFINED)
public class MList extends VList implements PrintVisitor.PrintRulesI, NaryOperatorI, SpecialPreProcessorI {
	public MList() {
		numberOfParameters = -1;
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI inputs[]) throws ParseException {
		int eleSize = inputs[0].getNumEles();
		for (int i = 0; i < inputs.length; ++i) {
			for (int j = 0; j < eleSize; ++j) {
				res.setEle(i * eleSize + j, inputs[i].getEle(j));
			}
		}
		return res;
	}

	@Override
	public MatrixNodeI preprocess(ASTFunNode node, MatrixPreprocessor visitor, MatrixJep jep, MatrixNodeFactory nf) throws ParseException {
		MatrixNodeI children[] = visitor.visitChildrenAsArray(node, null);
		Operator listOp = ((MatrixOperatorSet) jep.getOperatorSet()).getMList();
		// What if we have x=[1,2]; y = [x,x]; or z=[[1,2],x];
		// first check if all arguments are TENSORS
		boolean flag = true;
		if (children[0] instanceof ASTMFunNode) {
			if (((ASTMFunNode) children[0]).getOperator() != listOp) {
				flag = false;
			}
		} else {
			flag = false;
		}

		if (flag) {
			ASTMFunNode opNode1 = (ASTMFunNode) children[0];
			Dimensions dim = Dimensions.valueOf(children.length, opNode1.getDim());
			ASTMFunNode res = (ASTMFunNode) nf.buildUnfinishedOperatorNode(listOp);
			int k = 0;
			res.setDim(dim);
			res.jjtOpen();
			for (int i = 0; i < children.length; ++i) {
				ASTMFunNode opNode = (ASTMFunNode) children[i];
				for (int j = 0; j < opNode.jjtGetNumChildren(); ++j) {
					Node child = opNode.jjtGetChild(j);
					res.jjtAddChild(child, k++);
					child.jjtSetParent(res);
				}
			}
			res.jjtClose();
			return res;
		}
		MatrixNodeI node1 = children[0];
		Dimensions dim = Dimensions.valueOf(children.length, node1.getDim());
		ASTMFunNode res = (ASTMFunNode) nf.buildOperatorNode(listOp, children, dim);
		return res;
	}

	private int curEle;

	/** recursive procedure to print the tensor with lots of brackets. **/
	private void bufferAppend(MatrixNodeI node, PrintVisitor pv, int currank) throws ParseException {
		pv.append("[");
		if (currank + 1 >= node.getDim().rank()) {
			// bottom of tree
			for (int i = 0; i < node.getDim().getIthDim(currank); ++i) {
				if (i != 0) {
					pv.append(",");
				}
				node.jjtGetChild(curEle++).jjtAccept(pv, null);
			}
		} else {
			// not bottom of tree
			for (int i = 0; i < node.getDim().getIthDim(currank); ++i) {
				if (i != 0) {
					pv.append(",");
				}
				bufferAppend(node, pv, currank + 1);
			}
		}
		pv.append("]");
	}

	/**
	 * Used to print the TensorNode with all its children. Method implements
	 * PrintVisitor.PrintRulesI.
	 */
	@Override
	public void append(Node node, PrintVisitor pv) throws ParseException {
		curEle = 0;
		bufferAppend((MatrixNodeI) node, pv, 0);
	}

}
