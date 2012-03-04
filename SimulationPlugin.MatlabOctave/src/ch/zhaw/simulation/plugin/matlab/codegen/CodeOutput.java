package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Code output stream for Matlab / Octave files
 * 
 * @author Andreas Butti
 */
public class CodeOutput {

	/**
	 * The maximum line length
	 */
	private int maxLineLenght = 80;

	/**
	 * The Output stream
	 */
	private PrintStream out;

	/**
	 * Indentation count
	 */
	private int indent = 0;
	
	/**
	 * Ctor
	 * 
	 * @param out
	 *            The output Stream
	 */
	public CodeOutput(OutputStream out) {
		this.out = new PrintStream(out);
	}

	/**
	 * Prints a comment to the file, automatically linebreak
	 * 
	 * @param comment
	 *            The Comment to write
	 */
	public void printComment(String comment) {
		int len = 2;
		String[] words = comment.split(" ");

		printIndentation();
		out.print("% ");

		for (int i = 0; i < words.length; i++) {
			String s = words[i];

			len += s.length();
			out.print(s);

			// if there are another words
			if (i < words.length - 1) {

				if (len >= this.maxLineLenght) {
					out.println();

					printIndentation();
					out.print("% ");
					len = 2;
				} else {
					out.print(" ");
				}
			}
		}
		
		out.println();
	}

	/**
	 * write a newline
	 */
	public void newline() {
		out.println();
	}

	/**
	 * Writes a var=value; out
	 * 
	 * @param var
	 *            The name of the variable
	 * @param value
	 *            The value of the variable
	 */
	public void setVar(String var, double value) {
		printIndentation();
		out.println(var + "=" + value);
	}

	/**
	 * Prints a line
	 * 
	 * @param line
	 *            The line
	 */
	public void println(String line) {
		printIndentation();
		out.println(line);
	}

	/**
	 * Print the indentation
	 */
	private void printIndentation() {
		for(int i = 0; i < this.indent; i++) {
			out.print("\t");
		}
	}
	
	public void indent() {
		indent++;
	}
	
	public void detent() {
		indent--;
	}
	
	public int getMaxLineLenght() {
		return maxLineLenght;
	}

	public void setMaxLineLenght(int maxLineLenght) {
		this.maxLineLenght = maxLineLenght;
	}

	/**
	 * Closes the underling output stream
	 */
	public void close() {
		this.out.close();
	}
}
