package ch.zhaw.simulation.editor.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JToolTip;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.SimulationAdapter;
import ch.zhaw.simulation.model.listener.SimulationListener;
import ch.zhaw.simulation.util.gui.InfoTooltip;

public abstract class GuiDataTextElement<T extends AbstractNamedSimulationData> extends AbstractDataView<T> {
	private static final long serialVersionUID = 1L;

	protected int textY = 10;
	protected int questionmarkY = 15;

	// TODO !!! private
	protected String name = null;
	// TODO PRIVATE
	protected int textX = 0;

	private int paddingX = 4;

	private InfoTooltip tip;

	private SimulationListener changeListener;

	public GuiDataTextElement(T data, AbstractEditorControl<?> control) {
		super(data, control);
		tip = new InfoTooltip();
		changeListener = control.getModel().addSimulationListener(new SimulationAdapter() {
			@Override
			public void dataChanged(AbstractSimulationData o) {
				recalcFontMetrics(null);
			}
		});

		setToolTipText(" ");

		tip.setTitle(data.getName());
	}

	@Override
	public JToolTip createToolTip() {
		return tip;
	}

	// TODO PRIVATE
	protected void recalcFontMetrics(Graphics g) {
		if (getData() != null) {
			tip.setTitle(getData().getName());
			tip.setFormula(getData().getFormula());
		}

		if (g == null) {
			g = getGraphics();
		}
		if (g == null) {
			return;
		}

		String name = getData().getName();
		textX = (getWidth() - paddingX - g.getFontMetrics().stringWidth(name)) / 2 + paddingX / 2;

		if (textX < 0) {
			textX = paddingX / 2;
			this.name = shortName(name);
		} else {
			this.name = name;
		}
	}

	@Override
	protected void doubleClicked(MouseEvent e) {
		getControl().showFormulaEditor(getData());
	}

	protected abstract GuiImage getImage();

	@Override
	public void paint(Graphics g) {
		DrawHelper.antialisingOn(g);
		if (name == null) {
			recalcFontMetrics(g);
		}

		g.drawImage(getImage().getImage(isSelected()), 0, 0, this);

		g.setColor(Color.BLACK);

		g.drawString(name, textX, textY);

		if (!getData().getStaus().equals(AbstractNamedSimulationData.Status.SYNTAX_OK)) {
			g.setColor(Color.RED);

			int x = (getWidth() - g.getFontMetrics().stringWidth("?")) / 2;
			g.drawString("?", x, questionmarkY);
		}
	}

	@Override
	public void paintShadow(Graphics2D g) {
		GuiImage img = getImage();
		BufferedImage shadow = img.getDependencyImage();
		if (shadow == null) {
			return;
		}

		Point offset = img.getDependencyOffset();

		g.drawImage(shadow, getX() - offset.x, getY() - offset.y, this);
	}

	@Override
	/**
	 * Own repaint because of the shadow
	 */
	public void repaint() {
		if (getImage().getDependencyImage() == null) {
			super.repaint();
			return;
		}

		int x = getX();
		int y = getY();
		int width = getWidth();
		int height = getHeight();

		Point offset = getImage().getDependencyOffset();

		Container parent = getParent();
		if (parent != null) {
			parent.repaint(x - offset.x, y - offset.y, width + 2 * offset.x, height + 2 * offset.y);
		}
	}

	private String shortName(String name) {
		Graphics g = getGraphics();
		if (g == null) {
			return name;
		}
		FontMetrics m = g.getFontMetrics();
		int w = getWidth() - paddingX - m.stringWidth("..");

		for (int i = 0; i < w; i++) {
			if (g.getFontMetrics().stringWidth(name.substring(0, i)) > w) {
				return name.substring(0, i - 1) + "..";
			}
		}

		return name;
	}

	public void setStatus(String text) {
		tip.setStatus(text);
	}

	@Override
	public void dispose() {
		getModel().removeListener(changeListener);

		super.dispose();
	}
}
