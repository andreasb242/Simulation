package ch.zhaw.simulation.jep;

public enum CategoryType {
	BASE, MACRO, CONSTS, TRIGONOMETRIC, LOGARITHMIC, LOGICAL, NUMBER_SETS, SIMULATION, UNDEFINED;

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
