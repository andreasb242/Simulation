package ch.zhaw.simulation.math;

public class VarPlaceholder extends Number {
	private static final long serialVersionUID = 1L;

	private String name;

	public VarPlaceholder() {
	}

	public VarPlaceholder(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

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