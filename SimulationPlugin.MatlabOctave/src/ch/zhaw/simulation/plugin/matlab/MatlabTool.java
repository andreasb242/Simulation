package ch.zhaw.simulation.plugin.matlab;

public enum MatlabTool {
	OCTAVE("Octave"), MATLAB("Matlab"), SCILAB("Scilab");

	public final String name;

	private MatlabTool(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static MatlabTool fromString(String str) {
		for (MatlabTool t : MatlabTool.values()) {
			if (t.name.equalsIgnoreCase(str)) {
				return t;
			}
		}

		return null;
	}
}
