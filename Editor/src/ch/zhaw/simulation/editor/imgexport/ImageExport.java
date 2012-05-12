package ch.zhaw.simulation.editor.imgexport;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import butti.javalibs.config.Settings;

import ch.zhaw.simulation.dialog.snapshot.ImageExportable;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.vector.VectorExport;

public class ImageExport implements ClipboardOwner, ImageExportable {
	private Graphics2D g;
	private int width = 0;
	private int height = 0;
	private boolean exportBezierHelperPoint;
	private AbstractEditorControl<?> control;

	public ImageExport(AbstractEditorControl<?> control) {
		this.control = control;
		Settings settings = control.getSettings();
		this.exportBezierHelperPoint = settings.isSetting("imageexport.exporthelperpoints", false);
	}

	private void calcSize(AbstractEditorView<?> v, boolean onlySelection) {
		// TODO Grösse berechnen wenn onlySelection = true!!

		this.width = v.getPreferredSize().width;
		this.height = v.getPreferredSize().height;
	}

	@Override
	public void exportToClipboard(boolean onlySelection) {
		AbstractEditorView<?> v = control.getView();
		calcSize(v, onlySelection);

		BufferedImage img = drawToImg(v, onlySelection);
		TransferableImage trans = new TransferableImage(img);
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(trans, this);
	}

	private BufferedImage drawToImg(AbstractEditorView<?> v, boolean onlySelection) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		this.g = img.createGraphics();

		v.visitElements(this, onlySelection, exportBezierHelperPoint);

		g.dispose();

		return img;
	}

	public BufferedImage exportToImage(boolean onlySelection) {
		AbstractEditorView<?> v = control.getView();
		calcSize(v, onlySelection);

		BufferedImage img = drawToImg(v, onlySelection);
		return img;
	}

	@Override
	public void export(boolean onlySelection, String format, File file) throws IOException {
		AbstractEditorView<?> v = control.getView();
		calcSize(v, onlySelection);

		if ("PNG".equals(format)) {
			BufferedImage img = drawToImg(v, onlySelection);
			ImageIO.write(img, "PNG", file);
		} else {
			VectorExport ex = new VectorExport(new FileOutputStream(file), new Dimension(width, height), format);

			this.g = ex.getGraphics();

			v.visitElements(this, onlySelection, exportBezierHelperPoint);

			ex.close();
		}
	}

	public void draw(Paintable c) {
		c.paint(g);
	}

	public void draw(Component c) {
		Rectangle b = c.getBounds();
		Graphics sub = g.create(b.x, b.y, b.width, b.height);
		c.paint(sub);
		sub.dispose();
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// Nothing to do
	}

	@Override
	public boolean supportsSelection() {
		return true;
	}

}
