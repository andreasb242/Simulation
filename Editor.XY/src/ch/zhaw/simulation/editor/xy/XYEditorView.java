package ch.zhaw.simulation.editor.xy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.elements.ViewComponent;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.xy.XYModel;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class XYEditorView extends AbstractEditorView<XYEditorControl> {
	private static final long serialVersionUID = 1L;

	private DensityDraw density;

	public XYEditorView(XYEditorControl control, TransferableFactory factory) {
		super(control, factory);

		density = new DensityDraw(800, 600);
		density.updateImage();
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		DrawHelper.antialisingOn(g);

		g.drawImage(density.getImage(), 0, 0, this);

		GuiConfig cfg = control.getSysintegration().getGuiConfig();
		g.setColor(cfg.getRasterColor());

		// draw raster
		int raster = 20;
		int w = getWidth();
		int h = getHeight();

		for (int x = 0; x < w; x += raster) {
			g.drawLine(x, 0, x, h);
		}
		for (int y = 0; y < h; y += raster) {
			g.drawLine(0, y, w, y);
		}

		paintElements(g);

		if (showSelection) {
			paintSelection(g);
		}
	}

	@Override
	protected void paintSubComponent(Graphics2D g, Component c) {
		Graphics cg = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());

		if (c instanceof ViewComponent) {
			if (((ViewComponent) c).isDependent()) {
				((ViewComponent) c).paintShadow(g);
			}
			c.paint(cg);
		}
	}

	@Override
	protected void addModellistener() {
		getControl().getModel().addListener(this);
	}

	@Override
	protected void loadDataFromModel() {
		XYModel model = control.getModel();

		for (SimulationObject p : model.getData()) {
			dataAdded(p);
		}
	}

}