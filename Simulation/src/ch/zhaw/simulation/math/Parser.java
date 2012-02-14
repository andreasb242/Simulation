package ch.zhaw.simulation.math;


import java.util.Enumeration;
import java.util.Vector;
import java.util.Map.Entry;


import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.function.PostfixMathCommandI;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationObject;

public class Parser {
	private MatrixJep jep;

	private Function[] functionlist;
	private Constant[] constlist;

	public Parser() {
		newParser();
	}

	private void newParser() {
		jep = new MatrixJep();
		jep.setAllowUndeclared(true);
		jep.setImplicitMul(true);
		jep.setAllowAssignment(true);
		// jep.getSymbolTable().addConstant("test", 15);
		jep.addStandardConstants();
		jep.addStandardDiffRules();
		jep.addStandardFunctions();
		jep.addComplex();
		jep.getSymbolTable().remove("x");

		Vector<Function> functionlist = new Vector<Function>();

		for (Entry<String, PostfixMathCommandI> e : jep.getFunctionTable().entrySet()) {
			functionlist.add(new Function(e.getKey(), e.getValue()));
		}
		this.functionlist = functionlist.toArray(new Function[] {});

		Vector<Constant> constlist = new Vector<Constant>();
		SymbolTable st = jep.getSymbolTable();
		for (Enumeration<?> loop = st.keys(); loop.hasMoreElements();) {
			String s = (String) loop.nextElement();
			Object val = st.getValue(s);
			constlist.add(new Constant(s, val));
		}
		this.constlist = constlist.toArray(new Constant[] {});
	}

	private Line[] getFormulas(String text) {
		Vector<Line> data = new Vector<Line>();
		int line = -1;
		for (String s : text.split("\n")) {
			line++;
			int pos = s.indexOf("//");
			if (pos != -1) {
				s = s.substring(0, pos);
			}
			s = s.trim();
			if (s.isEmpty()) {
				continue;
			}

			data.add(new Line(s, line));
		}

		return data.toArray(new Line[] {});
	}

	public ParserNodePair checkCode(String text, SimulationObject o, Vector<NamedSimulationObject> sources, SimulationControl control, String name)
			throws CompilerError, ParseException, UserException.NotUsedException {
		if (text.isEmpty()) {
			throw new UserException.NotUsedException("Die Formel von " + name + " ist leer!");
		}

		Line[] data = getFormulas(text);
		newParser();

		for (NamedSimulationObject s : sources) {
			if (jep.getVar(s.getName()) != null) {
				jep.removeVariable(s.getName());
			}

			// Dummy Konstanten festlgegen
			jep.addConstant(s.getName(), new VarPlaceholder());
		}

		Vector<SimulationGlobal> globals = control.getModel().getGlobalsFor(o);
		for (SimulationGlobal g : globals) {
			if (jep.getVar(g.getName()) != null) {
				jep.removeVariable(g.getName());
			}

			// Dummy Konstanten festlgegen
			jep.addConstant(g.getName(), new VarPlaceholder());
		}

		// Dummy Konstanten festlgegen
		jep.addConstant("time", new VarPlaceholder());
		jep.addConstant("dt", new VarPlaceholder());

		Vector<Node> nodes = new Vector<Node>();

		for (Line l : data) {
			try {
				Node node = jep.parse(l.text);
				processEquation(node);
				nodes.add(node);
			} catch (ParseException e) {
				throw new CompilerError(e.getMessage(), l.line, l.text.length());
			}
		}

		Vector<SimulationGlobal> usedGlobals = new Vector<SimulationGlobal>();

		for (Node n : nodes) {
			checkUsedParameter(n, sources, globals, usedGlobals);
		}

		o.setUsedGlobals(usedGlobals);
		
		if (sources.size() > 0) {
			StringBuilder vars = new StringBuilder();
			for (NamedSimulationObject n : sources) {
				vars.append(", ");
				vars.append(n.getName());
			}

			throw new UserException.NotUsedException("Der Parameter " + vars.substring(2) + " wird nicht verwendet");
		}
		return new ParserNodePair(nodes, jep);
	}

	private void checkUsedParameter(Node node, Vector<NamedSimulationObject> sources, Vector<SimulationGlobal> globals, Vector<SimulationGlobal> usedGlobals) {
		int len = node.jjtGetNumChildren();
		for (int i = 0; i < len; i++) {
			Node c = node.jjtGetChild(i);
			checkUsedParameter(c, sources, globals, usedGlobals);
		}

		if (node instanceof ASTVarNode) {
			ASTVarNode a = (ASTVarNode) node;
			String name = a.getName();

			NamedSimulationObject found = null;
			for (NamedSimulationObject s : sources) {
				if (s.getName().equals(name)) {
					found = s;
					break;
				}
			}

			if (found != null) {
				sources.remove(found);
			}

			SimulationGlobal foundGlobal = null;
			for (SimulationGlobal g : globals) {
				if (g.getName().equals(name)) {
					foundGlobal = g;
					break;
				}
			}

			if (foundGlobal != null) {
				if (!usedGlobals.contains(foundGlobal)) {
					usedGlobals.add(foundGlobal);
				}
			}
		}
	}

	public Object processEquation(Node node) throws ParseException {
		if (node == null) {
			throw new NullPointerException("node == null");
		}

		// System.err.println("Parsed:\t\t");
		// System.err.println(jep.toString(node));
		Node processed = jep.preprocess(node);
		// System.err.println("Processed:\t");
		// System.err.println(jep.toString(processed));

		Node simp = jep.simplify(processed);
		// System.err.println("Simplified:\t");
		// System.err.println(jep.toString(simp));

		Object val = jep.evaluate(simp);
		// String s = jep.getPrintVisitor().formatValue(val);
		// System.err.println("Value:\t\t" + s);

		return val;
	}

	public Function[] getFunctionlist() {
		return functionlist;
	}

	public Constant[] getConst() {
		return constlist;
	}

	public static class ParserNodePair {
		public final Vector<Node> nodes;
		public final MatrixJep jep;

		public ParserNodePair(Vector<Node> nodes, MatrixJep jep) {
			if (jep == null) {
				throw new NullPointerException("jep == null");
			}
			if (nodes == null) {
				throw new NullPointerException("nodes == null");
			}
			this.nodes = nodes;
			this.jep = jep;
		}
	}

	public static class VarPlaceholder extends Number {
		private static final long serialVersionUID = 1L;

		@Override
		public double doubleValue() {
			return 1;
		}

		@Override
		public float floatValue() {
			return 1;
		}

		@Override
		public int intValue() {
			return 1;
		}

		@Override
		public long longValue() {
			return 1;
		}
	}

	// private Object getVal(NamedSimulationObject s, SimulationControl control)
	// throws ParseException, CompilerError {
	// if(s instanceof SimulationContainer) {
	// return ((SimulationContainer) s).getContainerValue();
	// } else if(s instanceof SimulationParameter) {
	// CopyOfMath value = s.getValue();
	// Node node = s.getParsed();
	//			
	// if(node == null) {
	// Vector<NamedSimulationObject> sources = control.getModel().getSource(s);
	//
	// node = value.checkCode(value.getValue(), sources, control);
	// }
	//
	// return processEquation(node);
	// } else {
	// throw new RuntimeException("Type of source flow is: " +
	// s.getClass().getName());
	// }
	// }
}
