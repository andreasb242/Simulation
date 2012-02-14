package gui.diagramm;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import simulation.data.SimulationEntry;
import simulation.data.SimulationSerie;

public class LineRenderer extends DiagrammRenderer {

	public LineRenderer(AffineTransform transform) {
		super(transform);
	}

	@Override
	protected void renderEntry(Graphics2D g, SimulationSerie s, double startTime, double endTime) {
		g.setColor(s.getColor());

		if(s.isConstValue()) {
			Point2D point1 = new Point2D.Double(startTime, s.getConstValue());
			Point2D point2 = new Point2D.Double(endTime, s.getConstValue());

			point1 = transform.transform(point1, null);
			point2 = transform.transform(point2, null);

			g.drawLine((int)point1.getX(), (int) point1.getY(), (int) point2.getX(), (int) point2.getY());
			
			return;
		}

		
		Point2D transformedPoint = null;
		Point2D lastTransformedPoint = null;

		for (SimulationEntry v : s.getData()) {
			
			double val = v.value;

			if (Double.isNaN(val) || Double.isInfinite(val)) {
				lastTransformedPoint = null;
				continue;
			}

			Point2D point = new Point2D.Double(v.time, val);

			lastTransformedPoint = transformedPoint;

			if (transform.transform(point, null) != null) {
				transformedPoint = transform.transform(point, null);
			} else {
				continue;
			}

			if (lastTransformedPoint != null) {
				g.drawLine((int) lastTransformedPoint.getX(), (int) lastTransformedPoint.getY(), (int) transformedPoint.getX(), (int) transformedPoint.getY());
			}
		}
	}
}
