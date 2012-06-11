package ch.zhaw.simulation.vector;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;

import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;

public class VectorExport {

	private AbstractVectorGraphicsIO vg;
	private Graphics2D g;

	public VectorExport(OutputStream out, Dimension size, String format) {
		if ("SVG".equals(format)) {
			this.vg = new SVGGraphics2D(out, size);
		} else if ("EMF".equals(format)) {
			this.vg = new EMFGraphics2D(out, size);
		} else if ("PS".equals(format)) {
			this.vg = new PSGraphics2D(out, size);
		} else {
			throw new RuntimeException("Unsupported format: " + format);
		}

		// needs to be set, otherwise the export is lousy
		this.vg.setDeviceIndependent(true);
		this.vg.startExport();

		this.g = (Graphics2D) vg.create();
	}

	public Graphics2D getGraphics() {
		return g;
	}

	public void close() throws IOException {
		this.g.dispose();

		this.vg.endExport();
		this.vg.closeStream();
	}
}
