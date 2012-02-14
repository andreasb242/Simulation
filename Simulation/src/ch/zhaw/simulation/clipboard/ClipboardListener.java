package ch.zhaw.simulation.clipboard;

public interface ClipboardListener {
	public void pasteEnabled(boolean enabled);
	public void cutCopyEnabled(boolean enabled);
}
