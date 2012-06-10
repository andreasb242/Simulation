package ch.zhaw.simulation.vector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import junit.framework.TestCase;

public class VectorExportTest extends TestCase {
	public VectorExportTest() {
	}

	public void testSVG() throws IOException {
		testVector("SVG", "expected.svg");
	}

	private void testVector(String format, String file) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		VectorExport e = new VectorExport(out, new Dimension(100, 100), format);

		Graphics2D g = e.getGraphics();

		g.setColor(Color.BLUE);
		g.fillOval(10, 10, 30, 30);

		g.dispose();
		e.close();

		String original = fileGetContents(VectorExportTest.class.getResourceAsStream(file));

		String[] linesOriginal = original.split(System.getProperty("line.separator"));
		String[] linesGet = out.toString().split(System.getProperty("line.separator"));

		assertEquals(linesOriginal.length, linesGet.length);

		for (int i = 0; i < linesOriginal.length; i++) {
			String o = linesOriginal[i];
			String get = linesGet[i];

			assertEquals(i + ":" + o, i + ":" + get);
		}
	}

	public void testPS() throws IOException {
		testVector("PS", "expected.ps");
	}

	public void testEMF() throws IOException {
		testVectorBin("EMF", "expected.emf");
	}

	private void testVectorBin(String format, String file) throws IOException {
		OutputStream out = new FileOutputStream("/tmp/unittest_export." + format);
		VectorExport e = new VectorExport(out, new Dimension(100, 100), format);

		Graphics2D g = e.getGraphics();

		g.setColor(Color.BLUE);
		g.fillOval(10, 10, 30, 30);

		g.dispose();
		e.close();
		out.close();

		byte[] expected = fileGetContentsBin(VectorExportTest.class.getResourceAsStream(file));
		byte[] get = fileGetContentsBin(new FileInputStream("/tmp/unittest_export." + format));

		assertEquals(expected.length, get.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], get[i]);
		}
	}

	public static byte[] fileGetContentsBin(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}

	public static String fileGetContents(InputStream in) {
		StringBuffer content = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = br.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			in.close();
		} catch (Exception e) {
			System.out.println("fileGetContents: " + in);
			e.printStackTrace();
			return null;
		}

		return content.toString();
	}
}
