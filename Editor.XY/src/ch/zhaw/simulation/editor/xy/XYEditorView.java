package ch.zhaw.simulation.editor.xy;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.elements.ViewComponent;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.xy.density.DensityDraw;
import ch.zhaw.simulation.editor.xy.element.MesoView;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class XYEditorView extends AbstractEditorView<XYEditorControl> implements XYSimulationListener {
	private static final long serialVersionUID = 1L;

	private DensityDraw density;

	public XYEditorView(XYEditorControl control, TransferableFactory factory) {
		super(control, factory);

		density = new DensityDraw(800, 600);

		loadDataFromModel();
	}

	@Override
	protected void initKeyhandler() {
		super.initKeyhandler();

		registerKeyShortcut('m', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control.addMeso();
			}
		});
	}

	@Override
	protected void paintEditor(Graphics2D g) {
		if (density.isVisible()) {
			g.drawImage(density.getImage(), 0, 0, this);
		}

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
	protected boolean dataAddedImpl(AbstractSimulationData o) {
		if (o instanceof MesoData) {
			add(new MesoView(control, (MesoData) o));
			return true;
		}
		return false;
	}

	@Override
	protected void addModellistener() {
		getControl().getModel().addListener(this);
	}

	@Override
	protected void loadDataFromModel() {
		SimulationXYModel model = control.getModel();

		for (AbstractSimulationData p : model.getData()) {
			dataAdded(p);
		}
	}

	public DensityDraw getDensity() {
		return density;
	}

	@Override
	public void densityAdded(DensityData d) {
	}

	@Override
	public void densityRemoved(DensityData d) {
	}

	@Override
	public void densityChanged(DensityData d) {
	}

	@Override
	public void modelSizeRasterChanged() {
		SimulationXYModel m = getControl().getModel();
		
		this.density.setSize(m.getWidth(), m.getHeight());
		
		// TODO: in thread auslagern!?
		this.density.updateImage();
		
		this.repaint();
	}

}
