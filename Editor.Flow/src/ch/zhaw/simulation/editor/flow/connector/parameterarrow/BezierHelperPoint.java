package ch.zhaw.simulation.editor.flow.connector.parameterarrow;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.AbstractView;
import ch.zhaw.simulation.model.flow.BezierConnectorData;
import ch.zhaw.simulation.util.Range;

public class BezierHelperPoint extends AbstractView {
	private static final long serialVersionUID = 1L;

	private int width;

	private BezierConnectorData connector;

	private ConnectorPointImage image;

	public BezierHelperPoint(BezierConnectorData connector, AbstractEditorControl<?> control) {
		super(control);
		this.connector = connector;

		width = control.getSysintegration().getGuiConfig().getConnectorPointWidth();

		setBounds(0, 0, width, width);

		image = new ConnectorPointImage(width, width, control.getSysintegration().getGuiConfig());

		Point p = this.connector.getHelperPoint();
		if (p != null) {
			setPosition(p.x, p.y);
		}
	}

	public BezierConnectorData getConnector() {
		return connector;
	}

	@Override
	public void dispose() {
		connector = null;
		image = null;

		super.dispose();
	}

	public Point getPoint() {
		return new Point(getX() + width / 2, getY() + width / 2);
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		BufferedImage img = image.getImage(isSelected());
		g.drawImage(img, 0, 0, this);
	}

	@Override
	public void moveElement(int dX, int dY) {
		int x = getX() + dX;
		int y = getY() + dY;

		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}

		setLocation(x, y);
		connector.setHelperPoint(new Point(x + width / 2, y + width / 2));
		getControl().getModel().setChanged();

		Range rx = new Range(x);
		rx.add(connector.getSource().getX());
		rx.add(connector.getSource().getX2());
		rx.add(connector.getTarget().getX());
		rx.add(connector.getTarget().getX2());

		Range ry = new Range(y);
		ry.add(connector.getSource().getY());
		ry.add(connector.getSource().getY2());
		ry.add(connector.getTarget().getY());
		ry.add(connector.getTarget().getY2());

		getParent().repaint(rx.getMin(), ry.getMin(), rx.getMax() - rx.getMin(), ry.getMax() - ry.getMin());
	}

	public void setPosition(int x, int y) {
		setLocation(x - width / 2, y - width / 2);
		connector.setHelperPoint(new Point(x, y));
		getControl().getModel().setChanged();
	}
}