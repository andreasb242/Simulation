package ch.zhaw.simulation.dialog.mathoverview;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.rtf.RTFEditorKit;

import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.math.Parser;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.BDialog;
import butti.javalibs.util.FileUtil;

public class MathOverview extends BDialog {
	private static final long serialVersionUID = 1L;
	private JTextPane displayPane;

	public MathOverview(Window parent) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Matheüberblick");

		this.displayPane = new JTextPane();
		this.displayPane.setEditable(false);
		add(new JScrollPane(displayPane));

		initFormulaInformation();
		// TODO only debug
		checkComplete();

		setSize(640, 480);
		setLocationRelativeTo(parent);
	}

	private void checkComplete() {
		Vector<String> contents = new Vector<String>();

		try {
			FileInputStream fstream = new FileInputStream("help/functiondescription/de/contents.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#") || line.isEmpty()) {
					continue;
				}

				contents.add(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Parser p = new Parser();
		for (Function f : p.getFunctionlist()) {
			String cname = f.getFunctionClass().getName();
			if (!contents.contains(cname)) {
				System.err.println("missing function description of: " + cname);
			}
		}

		for (Constant c : p.getConst()) {
			String cname = c.name;
			if (!contents.contains(cname)) {
				System.err.println("missing constant description of: " + cname);
			}
		}
	}

	private void initFormulaInformation() {
		displayPane.setEditorKit(new RTFEditorKit());
		try {
			displayPane.read(new FileInputStream("help/functiondescription/de/contents.rtf"), null);
		} catch (Exception e) {
			Errorhandler.showError(e, "Could not load helpfile");
		}
	}

	public static void main(String[] args) {
		MathOverview m = new MathOverview(null);
		m.setVisible(true);
	}
}
