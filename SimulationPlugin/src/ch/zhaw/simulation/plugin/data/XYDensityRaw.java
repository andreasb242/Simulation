package ch.zhaw.simulation.plugin.data;

public class XYDensityRaw {

	private String densityName;
	private double matrix[][];
	private double maxMinus;
	private double maxPlus;

	public XYDensityRaw(String densityName, int width, int height) {
		this.densityName = densityName;
		matrix = new double[height][width];
		maxMinus = 0.0f;
		maxPlus = 0.0f;
	}

	public void setMatrixValue(int x, int y, double value) {
		if (value < 0) {
			if (value < maxMinus) {
				maxMinus = value;
			}
		} else {
			if (value > maxPlus) {
				maxPlus = value;
			}
		}
		matrix[y][x] = value;
	}

	public double getMatrixValue(int x, int y) {
		return matrix[y][x];
	}

	public double getMaxMinus() {
		return maxMinus;
	}

	public double getMaxPlus() {
		return maxPlus;
	}

	public String getDensityName() {
		return densityName;
	}
}
