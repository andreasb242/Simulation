package ch.zhaw.simulation.math;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.function.PostfixMathCommandI;

import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.EmptyFormulaException;
import ch.zhaw.simulation.math.exception.NotUsedException;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;

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
		// jep.addComplex();
		jep.getSymbolTable().remove("x");

		// Add all functions from JEP to functionlist[]
		Vector<Function> functionlist = new Vector<Function>();
		for (Entry<String, PostfixMathCommandI> e : jep.getFunctionTable().entrySet()) {
			functionlist.add(new Function(e.getKey(), e.getValue()));
		}
		this.functionlist = functionlist.toArray(new Function[] {});

		// Add all constants from JEP to constlist[]
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

	public ParserNodePair checkCode(String formula, NamedFormulaData data, AbstractSimulationModel<?> model, Vector<AbstractNamedSimulationData> sourcesConst,
			String name) throws EmptyFormulaException, NotUsedException, CompilerError {
		if (formula.isEmpty()) {
			throw new EmptyFormulaException(data);
		}

		// split formula from TextPane of FormulaEditor into lines separated by
		// newlines
		Line[] lines = getFormulas(formula);

		// reset parser
		newParser();

		// duplicate sourcesConst
		Vector<AbstractNamedSimulationData> sources = new Vector<AbstractNamedSimulationData>();
		sources.addAll(sourcesConst);

		/**
		 * For all sources add variables so the parser can parse the formula
		 * 
		 * make them constant, so the parser throws an exception if the users
		 * tries to change some of them
		 */
		for (AbstractNamedSimulationData s : sources) {
			if (jep.getVar(s.getName()) != null) {
				jep.removeVariable(s.getName());
			}

			jep.addConstant(s.getName(), new VarPlaceholder());
		}

		/**
		 * And also do this for all global objects
		 */
		Vector<SimulationGlobalData> globals = model.getGlobalsFor(data);
		for (SimulationGlobalData g : globals) {
			if (jep.getVar(g.getName()) != null) {
				jep.removeVariable(g.getName());
			}

			jep.addConstant(g.getName(), new VarPlaceholder());
		}

		/**
		 * And for the predefined variables
		 */
		jep.addConstant("time", new VarPlaceholder());
		jep.addConstant("dt", new VarPlaceholder());

		Vector<Node> nodes = new Vector<Node>();

		// Iterate over lines
		// parse line by line, do simplification and add node to node-vector
		for (Line line : lines) {
			try {
				Node node = jep.parse(line.text);
				processEquation(node);
				nodes.add(node);
			} catch (ParseException e) {
				throw new CompilerError(data, e.getMessage(), line.line, line.text.length());
			}
		}

		Vector<SimulationGlobalData> usedGlobals = new Vector<SimulationGlobalData>();

		for (Node node : nodes) {
			checkUsedParameter(node, sources, globals, usedGlobals);
		}

		data.setUsedGlobals(usedGlobals);

		// If there are still namedDatas in the vector
		// throw NotUsedException
		if (sources.size() > 0) {
			StringBuilder vars = new StringBuilder();
			for (AbstractNamedSimulationData namedData : sources) {
				vars.append(", ");
				vars.append(namedData.getName());
			}

			throw new NotUsedException(data, vars.substring(2));
		}

		return new ParserNodePair(nodes, jep);
	}

	/**
	 * 
	 * @param node
	 * @param sourcesTmp
	 *            All used Sources are removed from this vector
	 * @param globals
	 * @param usedGlobals
	 */
	private void checkUsedParameter(Node node, Vector<AbstractNamedSimulationData> sourcesTmp, Vector<SimulationGlobalData> globals,
			Vector<SimulationGlobalData> usedGlobals) {
		int len = node.jjtGetNumChildren();
		for (int i = 0; i < len; i++) {
			Node c = node.jjtGetChild(i);
			checkUsedParameter(c, sourcesTmp, globals, usedGlobals);
		}

		if (node instanceof ASTVarNode) {
			ASTVarNode a = (ASTVarNode) node;
			String name = a.getName();

			AbstractNamedSimulationData found = null;
			for (AbstractNamedSimulationData s : sourcesTmp) {
				if (s.getName().equals(name)) {
					found = s;
					break;
				}
			}

			if (found != null) {
				sourcesTmp.remove(found);
			}

			SimulationGlobalData foundGlobal = null;
			for (SimulationGlobalData g : globals) {
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

	// //////////////////////////////////////////////////////////
	// TODO DEBUG
	// //////////////////////////////////////////////////////////

	public void addVar(String name, double value) {
		jep.addVariable(name, value);
	}

	public void setVar(String name, double value) {
		jep.setVarValue(name, value);
	}

	private Node simp;

	public void simplyfy(String formula) throws ParseException {
		Node node = jep.parse(formula);
		// System.err.println("Parsed:\t\t");
		// System.err.println(jep.toString(node));
		Node processed = jep.preprocess(node);
		// System.err.println("Processed:\t");
		// System.err.println(jep.toString(processed));

		simp = jep.simplify(processed);
		// System.err.println("Simplified:\t");
		// System.err.println(jep.toString(simp));

	}

	public double evaluate() throws ParseException {
		return (Double) jep.evaluate(simp);
	}

	// //////////////////////////////////////////////////////////
	// TODO DEBUG
	// //////////////////////////////////////////////////////////

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
}
