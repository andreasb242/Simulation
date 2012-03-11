package ch.zhaw.simulation.gui.codeditor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import butti.javalibs.errorhandler.Errorhandler;

public class Autocomplete implements DocumentListener {
	private JTextPane text;

	private static final String COMMIT_ACTION = "commit";

	private static enum Mode {
		INSERT, COMPLETION
	};

	private final ArrayList<AutocompleteWord> words = new ArrayList<AutocompleteWord>();
	private Mode mode = Mode.INSERT;

	private int cursorPos;

	public Autocomplete(JTextPane text) {
		this.text = text;
		text.getDocument().addDocumentListener(this);

		InputMap im = text.getInputMap();
		ActionMap am = text.getActionMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());
	}

	public void clearAutocomplete() {
		words.clear();
	}

	public void addAutocomplete(AutocompleteWord word) {
		words.add(word);
		Collections.sort(words, new Comparator<AutocompleteWord>() {

			@Override
			public int compare(AutocompleteWord o1, AutocompleteWord o2) {
				return o1.getContents().compareTo(o2.getContents());
			}
		});
	}

	public void changedUpdate(DocumentEvent ev) {
	}

	public void removeUpdate(DocumentEvent ev) {
	}

	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1) {
			return;
		}

		int pos = ev.getOffset();
		String content = null;
		try {
			content = text.getText(0, pos + 1);
		} catch (BadLocationException e) {
			Errorhandler.logError(e, "Autocomplete::insertUpdate failed");
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w))) {
				break;
			}
		}
		if (pos - w < 2) {
			// Too few chars
			return;
		}

		String prefix = content.substring(w + 1).toLowerCase();
		int n = Collections.binarySearch(words, prefix);

		if (n < 0 && -n <= words.size()) {
			AutocompleteWord match = words.get(-n - 1);
			if (match.getContents().startsWith(prefix)) {
				// A completion is found
				String completion = match.getContents().substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));

				cursorPos = match.getPos();
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;
		}
	}

	private class CompletionTask implements Runnable {
		private String completion;
		private int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		public void run() {
			try {
				text.getDocument().insertString(position, completion, null);
				text.setCaretPosition(position + completion.length());
				text.moveCaretPosition(position);
				mode = Mode.COMPLETION;
			} catch (BadLocationException e) {
				Errorhandler.logError(e);
			}
		}
	}

	private class CommitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = text.getSelectionEnd();
				text.setCaretPosition(pos + cursorPos);
				mode = Mode.INSERT;
			} else {
				text.replaceSelection("\n");
			}
		}
	}

	public static class AutocompleteWord implements Comparable<String> {
		private String contents;
		private int pos;

		public AutocompleteWord(String contents, int pos) {
			this.contents = contents;
			this.pos = pos;
		}

		public String getContents() {
			return contents;
		}

		public int getPos() {
			return pos;
		}

		@Override
		public int compareTo(String o) {
			return contents.compareTo(o);
		}
	}
}
