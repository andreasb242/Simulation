package ch.zhaw.simulation.help.model;

import java.util.Vector;

public class FunctionInformation {
	private String description;
	private String name;
	private Vector<Parameter> parameters = new Vector<Parameter>();
	private String functionClass;

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getParameter() {
		StringBuilder param = new StringBuilder();
		int optionalCount = 0;

		for (Parameter p : parameters) {
			if (p.getDefaultValue() != null) {
				param.append(" [, ");
				param.append(p.getName());
				param.append(" = ");
				param.append(p.getDefaultValue());
				optionalCount++;
			} else {
				param.append(", ");
				param.append(p.getName());
			}
		}

		for (; optionalCount > 0; optionalCount--) {
			param.append("]");
		}

		if (param.length() < 2) {
			return "";
		}

		return param.substring(2);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addParam(String name, String def) {
		parameters.add(new Parameter(name, def));
	}

	public void addUndefinedParam() {
		parameters.add(new UndefinedParameter());
	}

	public static class Parameter {
		private String name;
		private String defaultValue;

		public Parameter() {
		}

		public Parameter(String name, String def) {
			this.name = name;
			this.defaultValue = def;
		}

		public String getName() {
			return name;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	public static class UndefinedParameter extends Parameter {
		@Override
		public String getName() {
			return "...";
		}

		@Override
		public String getDefaultValue() {
			return null;
		}
	}

	public void setFunctionClass(String functionClass) {
		this.functionClass = functionClass;
	}

	public String getFunctionClass() {
		return functionClass;
	}
}
