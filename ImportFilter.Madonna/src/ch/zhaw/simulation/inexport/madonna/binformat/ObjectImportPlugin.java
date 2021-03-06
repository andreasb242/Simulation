package ch.zhaw.simulation.inexport.madonna.binformat;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PushbackInputStream;

import butti.javalibs.errorhandler.Errorhandler;

/**
 * Baseclass for IO Reading
 * 
 * @author Andreas Butti
 */
public abstract class ObjectImportPlugin {
	private ObjectInputStream oi;

	public ObjectImportPlugin() {
	}

	protected void initObjectInputStream(PushbackInputStream in) throws IOException {
		oi = new ObjectInputStream(in);
	}

	public String readString() throws IOException {
		try {
			return (String) oi.readObject();
		} catch (ClassNotFoundException e) {
			Errorhandler.logError(e, "Try to read String");
			throw new IOException("Could not read String");
		}
	}

	public void readObject() {
		try {
			oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean readBoolean() throws IOException {
		return oi.readBoolean();
	}

	public int readInt() throws IOException {
		return oi.readInt();
	}

	public float readFloat() throws IOException {
		return oi.readFloat();
	}

	public Point readPoint() throws IOException {
		Point point = new Point();
		point.x = oi.readInt();
		point.y = oi.readInt();
		return point;
	}
}
