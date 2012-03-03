package ch.zhaw.simulation.plugin.matlab;

public enum SimulationTool {
	OCTAVE("Octave"), MATLAB("Matlab");

	public final String name;

	private SimulationTool(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static SimulationTool fromString(String str) {
		for (SimulationTool t : SimulationTool.values()) {
			if (t.name.equalsIgnoreCase(str)) {
				return t;
			}
		}

		return null;
	}
}
