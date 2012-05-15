package ch.zhaw.simulation.plugin.matlab.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Vector;

import javax.swing.SwingUtilities;

public class OutputReaderThread extends Thread {
	private String prefix;
	private InputStream input;
	private PrintStream out;
	private Vector<ActionListener> listener = new Vector<ActionListener>();

	public OutputReaderThread(String prefix, InputStream input, PrintStream out) {
		this.prefix = prefix;
		this.input = input;
		this.out = out;
	}

	public void addListener(ActionListener l) {
		this.listener.add(l);
	}

	public void removeListener(ActionListener l) {
		this.listener.remove(l);
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				out.println(prefix + line);
			}
			reader.close();
		} catch (IOException e) {
			System.err.println(prefix);
			e.printStackTrace();
			// TODO fireAction(0, prefix + e.getMessage());
		}
	}

	private void fireAction(final int id, final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ActionEvent e = new ActionEvent(this, id, message);
				for (ActionListener l : listener) {
					l.actionPerformed(e);
				}
			}
		});
	}

}
