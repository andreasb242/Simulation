package ch.zhaw.simulation.diagram;

import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ItemEvent;

import javax.swing.JFrame;

import org.jdesktop.swingx.action.TargetableAction;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;

import butti.javalibs.gui.messagebox.Messagebox;

import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class LogButton extends TargetableAction {
	private static final long serialVersionUID = 1L;

	public enum Direction {
		X("x"), Y("y");

		public final String name;

		private Direction(String name) {
			this.name = name;
		}
	}

	private NumberAxis axis;
	private NumberAxis axisLog;
	private Direction direction;
	private XYPlot plot;

	public LogButton(final JFrame parent, XYPlot plot, Toolbar toolbar, Direction direction) {
		super("Logarithmisch (" + direction.name + ")", "diagram/log-" + direction.name, IconLoader.getIcon("diagram/log-" + direction.name,
				toolbar.getDefaultIconSize()));
		this.direction = direction;
		this.plot = plot;

		NumberAxis currentAxis = getCurrentAxis();
		
		setStateAction(true);
		toolbar.addToogleAction(this);

		if (currentAxis instanceof LogarithmicAxis) {
			setSelected(true);
			this.axisLog = currentAxis;
			this.axis = new NumberAxis();
		} else {
			this.axis = currentAxis;
			this.axisLog = new LogarithmicAxis(null);
		}
	}

	private NumberAxis getCurrentAxis() {
		if (direction == Direction.Y) {
			return (NumberAxis) this.plot.getRangeAxis();
		} else {
			return (NumberAxis) this.plot.getDomainAxis();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		boolean log = evt.getStateChange() == ItemEvent.SELECTED;

		NumberAxis currentAxis = getCurrentAxis();

		if ((currentAxis instanceof LogarithmicAxis) == log) {
			return;
		}

		NumberAxis newAxis;
		if (log) {
			newAxis = this.axisLog;
		} else {
			newAxis = this.axis;
		}

		AxisPropertySaver saver = new AxisPropertySaver();
		saver.load(currentAxis);
		saver.applyTo(newAxis);

		try {
			setAxis(newAxis);
		} catch (Exception e) {
			Messagebox.showError(null, "(AB)² Simulation", e.getMessage());
			setAxis(currentAxis);
		}
	}

	private void setAxis(NumberAxis newAxis) {
		if (direction == Direction.Y) {
			this.plot.setRangeAxis(newAxis);
		} else {
			this.plot.setDomainAxis(newAxis);
		}
	}

	public static class AxisPropertySaver {
		private String label;
		private Paint linePaint;
		private Font labelFont;
		private Font tickLabelFont;
		private Paint tickLabelPaint;

		public AxisPropertySaver() {
		}

		public void load(NumberAxis axis) {
			this.label = axis.getLabel();
			this.linePaint = axis.getAxisLinePaint();
			this.labelFont = axis.getLabelFont();
			this.tickLabelFont = axis.getTickLabelFont();
			this.tickLabelPaint = axis.getTickLabelPaint();
		}

		public void applyTo(NumberAxis axis) {
			axis.setLabel(this.label);
			axis.setAxisLinePaint(this.linePaint);
			axis.setLabelFont(this.labelFont);
			axis.setTickLabelFont(this.tickLabelFont);
			axis.setTickLabelPaint(this.tickLabelPaint);
		}
	}
}
