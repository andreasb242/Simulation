package ch.zhaw.simulation.densitydraw;

public interface DensityListener {

	/**
	 * Update canceled, nothing done
	 */
	public void noActionPerfomed();

	public void actionFailed(Exception reason);

	public void dataUpdated(float min, float max);
	
}
