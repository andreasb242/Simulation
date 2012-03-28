package ch.zhaw.simulation.editor.xy;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.elements.ViewComponent;
import ch.zhaw.simulation.editor.layout.SimulationLayout;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.xy.density.DensityDraw;
import ch.zhaw.simulation.editor.xy.element.meso.MesoView;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.model.xy.SubModelListener;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class XYEditorView extends AbstractEditorView<XYEditorControl> implements XYSimulationListener, SubModelSelectionListener, SubModelListener {
	private static final long serialVersionUID = 1L;

	private DensityDraw density;
	private SubModel currentSelectedSubmodel = null;

	public XYEditorView(XYEditorControl control, TransferableFactory factory) {
		super(control, factory);

		SimulationXYModel m = control.getModel();
		m.getSubmodels().addListener(this);

		density = new DensityDraw(m.getWidth(), m.getHeight());

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
		SimulationXYModel model = getControl().getModel();

		if (model.isShowDensityColor()) {
			BufferedImage img = density.getImage();
			g.drawImage(img, 0, 0, this);
		}

		GuiConfig cfg = control.getSysintegration().getGuiConfig();
		g.setColor(cfg.getRasterColor());

		// draw raster
		SimulationXYModel m = getControl().getModel();

		if (m.isShowGrid()) {
			Point zero = m.getZero();
			int raster = m.getGrid();

			int top = zero.y % raster;
			int left = zero.x % raster;

			int w = Math.min(getWidth(), m.getWidth());
			int h = Math.min(getHeight(), m.getHeight());

			for (int x = left; x < w; x += raster) {
				g.drawLine(x, 0, x, h);
			}
			for (int y = top; y < h; y += raster) {
				g.drawLine(0, y, w, y);
			}

			g.setColor(cfg.getRasterColorZero());

			g.drawLine(zero.x, 0, zero.x, h);
			g.drawLine(0, zero.y, w, zero.y);

		}
		paintElements(g);

		if (showSelection) {
			paintSelection(g);
		}
	}

	public SubModel getCurrentSelectedSubmodel() {
		return currentSelectedSubmodel;
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

		modelSizeRasterChanged();
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

		updateDensity(null, true);

		SimulationLayout l = (SimulationLayout) getLayout();
		l.setMinWidth(m.getWidth());
		l.setMinHeight(m.getHeight());

		this.revalidate();
		this.repaint();
	}

	/**
	 * If the formula is <code>null</code> or empty nothing is draw
	 */
	public void updateDensity(String formula, boolean onlyUpdate) {
		if (!onlyUpdate) {
			this.density.setFormula(formula);
		}

		getControl().getStatus().setStatusTextInfo("Dichte wird berechnet...");
		this.density.updateImageAsynchron(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getControl() != null && getControl().getStatus() != null) {
					getControl().getStatus().clearStatus();
				}

				if (e.getID() == 0) {
					repaint();
				}
			}
		});
	}

	@Override
	public void subModelSelected(SubModel submodel) {
		currentSelectedSubmodel = submodel;
	}

	@Override
	public void dispose() {
		control.getModel().getSubmodels().removeListener(this);
		super.dispose();
	}

	@Override
	public void submodelRemoved(SubModel model) {
		repaintMesoViews();
	}

	private void repaintMesoViews() {
		for (int i = 0; i < getComponentCount(); i++) {
			Component c = getComponent(i);
			if (c instanceof MesoView) {
				((MesoView) c).dataChanged();
			}
		}
	}

	@Override
	public void submodelAdded(SubModel model) {
		repaintMesoViews();
	}

	@Override
	public void submodelChanged(SubModel model) {
	}
}
