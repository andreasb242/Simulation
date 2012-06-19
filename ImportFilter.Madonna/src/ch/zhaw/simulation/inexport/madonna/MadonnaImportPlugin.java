package ch.zhaw.simulation.inexport.madonna;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import ch.zhaw.simulation.inexport.ImportException;
import ch.zhaw.simulation.inexport.ImportPlugin;
import ch.zhaw.simulation.inexport.madonna.binformat.BinaryImporter;
import ch.zhaw.simulation.inexport.madonna.xmlformat.XmlImporter;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;

public class MadonnaImportPlugin extends ImportPlugin {
	private FileImporter importer;

	@Override
	public boolean checkFile(InputStream in) throws IOException {
		// Check header

		byte[] file = new byte[32];

		if (in.read(file) == 32) {
			int header = isValidHeader(file);
			System.out.println("header: " + header);
			return header != 0;
		}

		return true;
	}

	@Override
	public boolean load(SimulationFlowModel model) throws ImportException {
		return this.importer.load(model);
	}

	@Override
	public void read(File file) throws IOException, ImportException {
		PushbackInputStream in = new PushbackInputStream(new FileInputStream(file), 512);

		// Check header
		byte[] header = new byte[32];
		if (in.read(header) != 32) {
			throw new ImportException("Not a BarkleyMadonna file, header could not be checked!");
		}
		int headerType = isValidHeader(header);
		if (headerType == 1) {
			this.importer = new BinaryImporter();
		} else if (headerType == 2) {
			this.importer = new XmlImporter();
		} else {
			throw new ImportException("Not a BarkleyMadonna file!");
		}

		in.unread(header);
		
		this.importer.read(in);

		in.close();
	}

	/**
	 * Checks if this is a valid BM file
	 * 
	 * @param file
	 *            The data read from a file
	 * 
	 * @return 0 if not valid, 1 if valid bin file, 2 if valid XML file
	 */
	private static int isValidHeader(byte[] file) {
		byte[] head = new byte[] { 0x01, 'M', 'a', 'd', 'o', 'n', 'n', 'a', 'M', 'o', 'd', 'e', 'l' };

		// XML File
		String s = new String(file, 0, 10);
		if ("<Document>".equals(s)) {
			return 2;
		}

		// Binary Madonna format
		int i = 0;
		for (byte b : head) {
			if (file[i++] != b) {
				return 0;
			}
		}

		return 1;
	}

	@Override
	public boolean load() throws Exception {
		return true;
	}

	@Override
	public void unload() {
	}

	@Override
	public String[] getFileExtension() {
		return new String[] { ".mmd" };
	}
}
