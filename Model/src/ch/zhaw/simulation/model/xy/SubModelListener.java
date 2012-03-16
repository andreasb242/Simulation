package ch.zhaw.simulation.model.xy;

public interface SubModelListener {
	public void modelRemoved(SubModel model);

	public void modelAdded(SubModel model);

	public void modelChanged(SubModel model);
}
