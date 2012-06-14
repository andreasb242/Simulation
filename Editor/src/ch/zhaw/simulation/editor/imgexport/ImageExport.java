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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import butti.javalibs.config.Config;
import ch.zhaw.simulation.dialog.snapshot.ImageExportable;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.vector.VectorExport;

public class ImageExport implements ClipboardOwner, ImageExportable {
	private Graphics2D g;
	private int width = 0;
	private int height = 0;
	private int startX = 0;
	private int startY = 0;

	private boolean exportBezierHelperPoint;
	private AbstractEditorControl<?> control;

	public final static String PROP_KEY_NOT_DRAW_SELECTION = "ImageExport.notDrawSelection";

	public ImageExport(AbstractEditorControl<?> control) {
		this.control = control;
		this.exportBezierHelperPoint = Config.is("imageexport.exporthelperpoints", false);
	}

	private void calcSize(AbstractEditorView<?> v, boolean onlySelection) {
		if (onlySelection) {
			calcSizeSelection(v);
		} else {
			this.width = v.getPreferredSize().width;
			this.height = v.getPreferredSize().height;
		}
	}

	private void calcSizeSelection(AbstractEditorView<?> v) {
		Rectangle size = v.calcSizeSelection(exportBezierHelperPoint);

		int padding = Config.get("selectionCopyPadding", 20);

		this.startX = size.x - padding;
		if (this.startX < 0) {
			this.startX = 0;
		}

		this.startY = size.y - padding;
		if (this.startY < 0) {
			this.startY = 0;
		}

		this.width = size.width + padding * 2;

		if (this.width > v.getPreferredSize().width) {
			this.width = v.getPreferredSize().width;
		}

		this.height = size.height + padding * 2;

		if (this.height > v.getPreferredSize().height) {
			this.height = v.getPreferredSize().height;
		}
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
		drawElementsToGraphics(v, onlySelection);

		return img;
	}

	/**
	 * Draws the elements to the graphics, e.g. only the selected ones
	 * 
	 * @param v
	 *            The view
	 * @param onlySelection
	 *            If only the selection should be drawn
	 */
	private void drawElementsToGraphics(AbstractEditorView<?> v, boolean onlySelection) {
		if (onlySelection) {
			this.g.setTransform(AffineTransform.getTranslateInstance(-startX, -startY));
		}
		v.visitElements(this, exportBezierHelperPoint);
		g.dispose();
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
			drawElementsToGraphics(v, onlySelection);

			ex.close();
		}
	}

	public Graphics2D getGraphics() {
		return g;
	}

	public void draw(Paintable c) {
		c.paint(g, false);
	}

	public void draw(Component c) {
		Rectangle b = c.getBounds();
		Graphics sub = g.create(b.x, b.y, b.width, b.height);
		if (c instanceof JComponent) {
			((JComponent) c).putClientProperty(PROP_KEY_NOT_DRAW_SELECTION, true);
		}
		c.paint(sub);
		if (c instanceof JComponent) {
			((JComponent) c).putClientProperty(PROP_KEY_NOT_DRAW_SELECTION, null);
		}
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
