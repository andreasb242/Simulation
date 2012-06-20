package ch.zhaw.simulation.inexport.madonna;

import java.util.HashMap;
import java.util.Map.Entry;

import butti.javalibs.util.StringUtil;

public class FormulaImporter {
	private static HashMap<String, String> replacements = new HashMap<String, String>();

	static {
		replacements.put("LOGN(", "ln(");
		replacements.put("LOG10(", "log(");
		replacements.put("INT(", "floor(");
		replacements.put("MEAN(", "avg(");
		replacements.put("ARCSIN(", "asin(");
		replacements.put("ARCCOS(", "acos(");
		replacements.put("ARCTAN(", "atan(");
		replacements.put("ARCSINH(", "asinh(");
		replacements.put("ARCCOSH(", "acosh(");
		replacements.put("ARCTANH(", "atanh(");

		// TODO SWITCH(x, y) Same as x >= y
		// TODO IF x THEN y ELSE z
	}

	public static String convert(String formula) {
		for (Entry<String, String> r : replacements.entrySet()) {
			formula = StringUtil.replace(formula, r.getKey(), r.getValue());
		}

		return formula;
	}

}
