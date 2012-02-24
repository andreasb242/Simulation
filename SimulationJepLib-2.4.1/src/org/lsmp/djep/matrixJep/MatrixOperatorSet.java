/* @author rich
 * Created on 27-Jul-2003
 */
package org.lsmp.djep.matrixJep;

import org.lsmp.djep.matrixJep.function.MArrayAccess;
import org.lsmp.djep.matrixJep.function.MAssign;
import org.lsmp.djep.matrixJep.function.MList;
import org.lsmp.djep.matrixJep.function.MPower;
import org.lsmp.djep.vectorJep.function.ExteriorProduct;
import org.lsmp.djep.vectorJep.function.MAdd;
import org.lsmp.djep.vectorJep.function.MDivide;
import org.lsmp.djep.vectorJep.function.MDot;
import org.lsmp.djep.vectorJep.function.MMultiply;
import org.lsmp.djep.vectorJep.function.MSubtract;
import org.lsmp.djep.vectorJep.function.MUMinus;
import org.lsmp.djep.xjep.XOperator;
import org.lsmp.djep.xjep.XOperatorSet;
import org.nfunk.jep.Operator;

/**
 * The set of operators used in matricies.
 * 
 * @author Rich Morris Created on 27-Jul-2003
 */
public class MatrixOperatorSet extends XOperatorSet {
	private Operator TENSOR = new XOperator("TENSOR", new MList(), XOperator.NARY);

	public Operator getMList() {
		return TENSOR;
	}

	public MatrixOperatorSet() {
		super();
		OP_ADD.setPFMC(new MAdd());
		OP_SUBTRACT.setPFMC(new MSubtract());
		// TODO_YEP fix commutatitivity for matrix mult. How?
		OP_MULTIPLY.setPFMC(new MMultiply());
		OP_DIVIDE.setPFMC(new MDivide());
		OP_POWER.setPFMC(new MPower());
		OP_UMINUS.setPFMC(new MUMinus());
		OP_DOT.setPFMC(new MDot());
		OP_CROSS = new XOperator("^^", "^", new ExteriorProduct(), XOperator.BINARY + XOperator.RIGHT, ((XOperator) OP_CROSS).getPrecedence());
		OP_ASSIGN.setPFMC(new MAssign());
		OP_LIST.setPFMC(new MList());
		OP_ELEMENT.setPFMC(new MArrayAccess());
		// OP_RANGE.setPFMC(new VRange());
	}

	public Operator[] getOperators() {
		Operator ops1[] = super.getOperators();
		Operator ops2[] = new Operator[ops1.length + 1];
		System.arraycopy(ops1, 0, ops2, 0, ops1.length);
		ops2[ops1.length] = TENSOR;
		return ops2;
	}
}
