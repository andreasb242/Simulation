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
		replacements.put("MEAN(", "Avg(");
		
		// TODO SWITCH(x, y)	Same as x >= y
		//replacements.put("", "");
	}

	public static String convert(String formula) {
		for (Entry<String, String> r : replacements.entrySet()) {
			formula = StringUtil.replace(formula, r.getKey(), r.getValue());
		}

		return formula;
	}

}
