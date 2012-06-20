package ch.zhaw.simulation.jep;

public enum CategoryType {
	/** @formatter:off */
	BASE("Base"),
	MACRO("Macro"),
	CONSTS("Constants"),
	CONDITIONAL("Conditional"),
	DISCRET("Discret"),
	TRIGONOMETRIC("Trigonometric"),
	LOGARITHMIC("Logarithmic"),
	LOGICAL("Logical"),
	NUMBER_SETS("Number Sets"),
	SIMULATION("Simulation"),
	MATRIX("Matrix"),
	INTERVAL("Interval"),
	UNDEFINED("Undefined");
	/** @formatter:on */

	private String name;

	private CategoryType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static CategoryType fromFunction(Object command) {
		Class<?> cls = command.getClass();
		Category annotation = cls.getAnnotation(Category.class);
		if (annotation != null) {
			return annotation.value();
		} else {
			System.err.println("class \"" + cls.getName() + "\" has no Category annotation!");
		}

		return UNDEFINED;
	}

}
