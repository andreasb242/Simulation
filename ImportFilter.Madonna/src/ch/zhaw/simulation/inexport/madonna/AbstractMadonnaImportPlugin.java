package ch.zhaw.simulation.inexport.madonna;


import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;

import ch.zhaw.simulation.inexport.ImportException;

public abstract class AbstractMadonnaImportPlugin extends ObjectImportPlugin {
	protected HashMap<Integer, MadonnaElement> data = new HashMap<Integer, MadonnaElement>();

	public AbstractMadonnaImportPlugin() {
	}

	@Override
	public boolean checkFile(InputStream in) throws IOException {
		// Check header
		byte[] head = new byte[] { 0x01, 'M', 'a', 'd', 'o', 'n', 'n', 'a', 'M', 'o', 'd', 'e', 'l' };
		for (byte b : head) {
			if (in.read() != b) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void read(File file) throws IOException, ImportException {
		PushbackInputStream in = new PushbackInputStream(new FileInputStream(file), 512);

		data.clear();

		// Check header
		if (!checkFile(in)) {
			throw new ImportException("Not a BarkleyMadonna file!");
		}

		int x = 0;

		// Skip unknown data
		while (true) {
			x = in.read();

			if (x == -1) {
				throw new ImportException("No Flowchart found! Only Flowchart can be imported");
			}

			// Serialized magic bytes: 0x7F 0xED
			if (x == 172) {
				if (in.read() == 237) {
					in.unread(237);
					in.unread(172);
					break;
				}
			}
		}

		initObjectInputStream(in);

		String s = readString();

		if (!"MadonnaModel".equals(s)) {
			throw new ImportException("Not a BarkleyMadonna file!");
		}

		int v = readInt();
		if (12 != v) {
			throw new ImportException("BarkleyMadonna file version: " + v + " cannot be read! (Only Version 12 supported)");
		}

		s = readString();

		if (!"Model".equals(s)) {
			throw new ImportException("Model not found in file!");
		}

		loadModel();
	}

	private void loadModel() throws ImportException, IOException {
		readString();
		readPoint();

		readInt();
		readInt();
		readInt();
		readInt();

		readObject();
		if (readBoolean()) {
			readObject();
		}

		int count = readInt();
		for (int i = 0; i < count; i++) {
			String type = readString();

			if ("Arc".equals(type)) {
				parseArc();
			} else if ("Cloud".equals(type)) {
				parseCloud();
			} else if ("Flow".equals(type)) {
				parseFlow();
			} else if ("Formula".equals(type)) {
				parseFormula();
			} else if ("Model".equals(type)) {
			} else if ("Pipe".equals(type)) {
				parsePipe();
			} else if ("Reservoir".equals(type)) {
				parseContainer();
			} else if ("Text".equals(type)) {
				parseText();
			} else {
				throw new ImportException("Type \"" + type + "\" unknown");
			}
		}
	}

	private void parseFormula() throws IOException {
		String name = readString();
		Point pos = readPoint();
		String formula = readString();
		readString();

		loadAdditionalObjectData(new MFormula(name, pos, formula));
	}

	private void parseText() throws IOException {
		Point pos = readPoint();
		readString();
		readInt();
		readInt();
		readInt();
		readString();
		readInt();
		readInt();
		readInt();
		readInt();
		readInt();
		readInt();

		loadAdditionalObjectData(new MText(pos));
	}

	private void parsePipe() throws IOException {
		int fromId = readInt();
		int toId = readInt();

		readFloat();
		readFloat();
		readBoolean();

		loadAdditionalObjectData(new MConnector2(fromId, toId));
	}

	private void parseFlow() throws IOException {
		String name = readString();
		Point pos = readPoint();
		String formula = readString();
		readString();

		loadAdditionalObjectData(new MFlow(name, pos, formula));
	}

	private void parseArc() throws IOException {
		int fromId = readInt();
		int toId = readInt();

		Point point1 = readPoint();
		readInt();
		readInt();

		loadAdditionalObjectData(new MConnector(fromId, toId, point1));
	}

	private void parseCloud() throws IOException {
		Point pos = readPoint();

		loadAdditionalObjectData(new MCloud(pos));
	}

	private void parseContainer() throws IOException {
		String name = readString();
		Point pos = readPoint();

		String formula = readString();
		readBoolean();

		loadAdditionalObjectData(new MContainer(name, pos, formula));
	}

	private void loadAdditionalObjectData(MadonnaElement e) throws IOException {
		int uniqueId = readInt();
		readInt();
		readInt();
		if (readBoolean()) {
			readObject();
		}

		data.put(uniqueId, e);
	}

}
