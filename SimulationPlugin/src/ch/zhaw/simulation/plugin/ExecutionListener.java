package ch.zhaw.simulation.plugin;

/**
 * @author: bachi
 */
public interface ExecutionListener {
	public void executionStarted(String message);
	public void setExecutionMessage(String message);
	public void executionFinished();
}
