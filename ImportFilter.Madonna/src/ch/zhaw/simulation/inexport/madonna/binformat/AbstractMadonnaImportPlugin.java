package ch.zhaw.simulation.inexport.madonna.binformat;

import java.awt.Point;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.HashMap;

import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.inexport.madonna.FileImporter;

/**
 * Berkeley Madonna import Plugin for (AB)² Simulation
 * 
 * This is the file parser
 * 
 * The .mmd Fileformat is a binary file, which contains a Java serializes
 * stream, this stream is searched an read
 * 
 * @author Andreas Butti
 */
public abstract class AbstractMadonnaImportPlugin extends ObjectImportPlugin implements FileImporter {
	protected HashMap<Integer, MadonnaElement> data = new HashMap<Integer, MadonnaElement>();

	public AbstractMadonnaImportPlugin() {
	}

	@Override
	public void read(PushbackInputStream in) throws IOException, ImportException {
		data.clear();

		int x = 0;

		// Skip unknown data
		while (true) {
			x = in.read();

			if (x == -1) {
				throw new ImportException("No Flowchart found! Only Flowchart can be imported");
			}

			// Serialized magic bytes: 0xAC 0xED
			if (x == 0xAC) {
				if (in.read() == 0xED) {
					in.unread(0xED);
					in.unread(0xAC);
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

	/**
	 * Loads the Main model
	 * 
	 * @throws ImportException
	 *             If there is something within the file which is not supported
	 *             by our importer
	 */
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
				parseConnector();
			} else if ("Cloud".equals(type)) {
				parseInfinite();
			} else if ("Flow".equals(type)) {
				parseValve();
			} else if ("Formula".equals(type)) {
				parseFormula();
			} else if ("Model".equals(type)) {
				throw new ImportException("Submodel cannot be handled by this importer, delete the submodel and try importing it again");
			} else if ("Pipe".equals(type)) {
				parseFlow();
			} else if ("Reservoir".equals(type)) {
				parseContainer();
			} else if ("Text".equals(type)) {
				parseText();
			} else {
				throw new ImportException("Type \"" + type + "\" unknown");
			}
		}
	}

	/**
	 * Reads a formula
	 */
	private void parseFormula() throws IOException {
		String name = readString();
		Point pos = readPoint();
		String formula = readString();
		readString();

		loadAdditionalObjectData(new MParameter(name, pos, formula));
	}

	/**
	 * Parses a textfield
	 */
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

	/**
	 * Reads a flow element, the Valve is parsed separate
	 */
	private void parseFlow() throws IOException {
		int fromId = readInt();
		int toId = readInt();

		readFloat();
		readFloat();
		readBoolean();

		loadAdditionalObjectData(new MFlow(fromId, toId));
	}

	/**
	 * Parses the Valve within a Flow
	 */
	private void parseValve() throws IOException {
		String name = readString();
		Point pos = readPoint();
		String formula = readString();
		readString();

		loadAdditionalObjectData(new MValve(name, pos, formula));
	}

	/**
	 * Parses a parameter connector
	 */
	private void parseConnector() throws IOException {
		int fromId = readInt();
		int toId = readInt();

		Point point1 = readPoint();
		readInt();
		readInt();

		loadAdditionalObjectData(new MConnector(fromId, toId, point1));
	}

	/**
	 * Parses an infinite source of a flow
	 */
	private void parseInfinite() throws IOException {
		Point pos = readPoint();

		loadAdditionalObjectData(new MInfinite(pos));
	}

	/**
	 * Parses a container
	 */
	private void parseContainer() throws IOException {
		String name = readString();
		Point pos = readPoint();

		String formula = readString();
		readBoolean();

		loadAdditionalObjectData(new MContainer(name, pos, formula));
	}

	/**
	 * Parses the default header of an element
	 * 
	 * @param e
	 *            The element to read
	 */
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
