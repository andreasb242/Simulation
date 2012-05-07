package ch.zhaw.simulation.inexport.dynasys;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ch.zhaw.simulation.inexport.ImportPlugin;


/**
 * This is the baseclass for IO reading of the Dynasys File
 * 
 * @author Andreas Butti
 */
public abstract class BinaryImportPlugin extends ImportPlugin {
	protected InputStream in;

	public BinaryImportPlugin() {
	}

	protected void openFile(File file) throws FileNotFoundException {
		in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	}

	protected int readInt() throws IOException {
		int len = 0;
		int tmp = in.read();
		if (tmp == 0x06 || tmp == 0x02) {
			return in.read();
		}
		if (tmp == 0x0C || tmp == 0x04) {
			return readLsb();
		}

		return len;
	}

	protected String readString() throws IOException {
		int len = readInt();

		char[] data = new char[len];

		for (int i = 0; i < len; i++) {
			data[i] = (char) in.read();
		}

		return new String(data);
	}

	protected String readString(int size) throws IOException {
		int len = in.read();

		int i;

		char[] data = new char[len];

		for (i = 0; i < len; i++) {
			data[i] = (char) in.read();
		}

		for (; i < size - 1; i++) {
			in.read();
		}

		return new String(data);
	}

	protected void readData(int len) throws IOException {
		byte[] lastData = new byte[len];
		in.read(lastData);
	}

	/**
	 * List ein LSB Int (10er 100er 1000er 10000er...)
	 * 
	 * @return int
	 */
	protected int readLsb() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();

		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1);
	}

	protected int readI() throws IOException {
		in.read();
		in.read();
		int c3 = in.read();
		int c4 = in.read();

		int i = c4 << 8 | c3;
		return i;
	}

	protected Rectangle readRect() throws IOException {
		int i1 = readI();
		int i2 = readI();
		int i3 = readI();
		int i4 = readI();
		return new Rectangle(i1, i2, i3, i4);
	}

}
