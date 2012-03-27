package ch.zhaw.simulation.model.xy;

public interface SubModelListener {
	public void submodelRemoved(SubModel model);

	public void submodelAdded(SubModel model);

	public void submodelChanged(SubModel model);
}
