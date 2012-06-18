package ch.zhaw.simulation.math;

import org.nfunk.jep.function.PostfixMathCommandI;

import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

public class Function {
	private String name;
	private PostfixMathCommandI command;
	private CategoryType category;
	private String example;
	private String description;

	public Function(String name, PostfixMathCommandI command) {
		this.name = name;
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public CategoryType getCategory() {
		if (this.category == null) {
			this.category = CategoryType.fromFunction(this.command);
		}
		return category;
	}

	public String getExample() {
		if (example == null) {
			Class<?> cls = command.getClass();
			Example annotation = cls.getAnnotation(Example.class);
			if (annotation != null) {
				example = annotation.value();
			} else {
				System.err.println("class \"" + cls.getName() + "\" has no Example annotation!");
				example = "";
			}
		}

		return example;
	}

	public String getDescription() {
		if (description == null) {
			Class<?> cls = command.getClass();
			Description annotation = cls.getAnnotation(Description.class);
			if (annotation != null) {
				description = annotation.value();
			} else {
				System.err.println("class \"" + cls.getName() + "\" has no Description annotation!");
				example = "";
			}
		}
		return description;
	}

	public Class<? extends PostfixMathCommandI> getFunctionClass() {
		return command.getClass();
	}
}
