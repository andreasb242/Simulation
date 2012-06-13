package ch.zhaw.simulation.sysintegration.filechooser.script;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import butti.javalibs.listener.ListenerList;

public class OutputReaderThread extends Thread {
	private String prefix;
	private InputStream input;
	private PrintStream out;
	private ListenerList listener = new ListenerList();

	public OutputReaderThread(String prefix, InputStream input, PrintStream out) {
		this.prefix = prefix;
		this.input = input;
		this.out = out;
	}

	public void addListener(ActionListener l) {
		this.listener.addListener(l);
	}

	public void removeListener(ActionListener l) {
		this.listener.removeListener(l);
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				handleLine(line);
			}
			reader.close();
		} catch (IOException e) {
			System.err.println(prefix);
			e.printStackTrace();
			fireAction(0, prefix + e.getMessage());
		}
	}

	protected void handleLine(String line) {
		out.println(prefix + line);
	}

	private void fireAction(final int id, final String message) {
		ActionEvent e = new ActionEvent(this, id, message);
		listener.actionPerformed(e);
	}

}
