package ch.zhaw.simulation.sysintegration;

public interface SysintegrationEventlistener {
	public enum EventType {
		ABOUT, EXIT, PREFERENCES, OPEN
	};
	
	public void sysEvent(EventType type, String param);
}
