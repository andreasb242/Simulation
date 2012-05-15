package ch.zhaw.simulation.diagram;

import java.awt.event.ItemEvent;

import javax.swing.JFrame;

import org.jdesktop.swingx.action.TargetableAction;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.util.StringUtil;
import ch.zhaw.simulation.diagram.DiagramConfigListener.Direction;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class LogButton extends TargetableAction {
	private static final long serialVersionUID = 1L;
	private DiagramConfigModel model;
	private Direction direction;
	private DiagramConfigListener listener;
	private NumberAxis axis;
	private NumberAxis axisLog;

	public LogButton(final JFrame parent, final XYPlot plot, Toolbar toolbar, DiagramConfigModel model, NumberAxis axis, Direction direction) {
		super("Logarithmisch (" + direction.name + ")", "diagram/log-" + direction.name, IconLoader.getIcon("diagram/log-" + direction.name,
				toolbar.getDefaultIconSize()));

		this.model = model;
		this.direction = direction;

		this.axis = axis;

		setStateAction(true);
		toolbar.addToogleAction(this);

		listener = new DiagramConfigAdapter() {
			@Override
			public void setLogEnabled(Direction direction, boolean log) {
				if(LogButton.this.direction != direction) {
					return;
				}
				
				// check if we can show a log axis, all values have to be > 0
				if (log && !canShowLogAxis(direction)) {
					Messagebox.showInfo(parent, "Logarithmische Achsen",
							"<html>Logarithmische achsen können nicht angewendet werden, da das Diagramm Negativ- oder Nullwerte enthält.<br>"
									+ "Blenden Sie nur rein positive Serien ein und versuchen Sie es erneut.</html>");

					LogButton.this.model.setLogEnabled(LogButton.this.direction, false);
					return;
				}

				ValueAxis newAxis;

				if (log) {
					if (LogButton.this.axisLog == null) {
						LogButton.this.axisLog = new LogarithmicAxis(null);
					}

					String oldLabel = LogButton.this.axisLog.getLabel();
					String newLabel = LogButton.this.axis.getLabel();

					if (!StringUtil.equals(oldLabel, newLabel)) {
						LogButton.this.axisLog.setLabel(newLabel);
					}

					newAxis = axisLog;
				} else {
					if (LogButton.this.axisLog == null) {
						return;
					}

					String oldLabel = LogButton.this.axis.getLabel();
					String newLabel = LogButton.this.axisLog.getLabel();

					if (!StringUtil.equals(oldLabel, newLabel)) {
						LogButton.this.axis.setLabel(newLabel);
					}

					newAxis = LogButton.this.axis;
				}
		
				if(direction == Direction.Y) {
					plot.setRangeAxis(newAxis);
				} else {
					plot.setDomainAxis(newAxis);
				}

				if (isSelected() != log) {
					setSelected(log);
				}
			}
		};
		this.model.addListener(listener);
	}

	protected boolean canShowLogAxis(Direction direction) {
		if (direction == Direction.Y) {
			for (SimulationSerie s : model.getCollection()) {
				if (model.isEnabled(s)) {
					for (SimulationEntry d : s.getData()) {
						if (d.value <= 0) {
							return false;
						}
					}
				}
			}

			return true;
		} else {
			// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			return true;
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		boolean newValue = evt.getStateChange() == ItemEvent.SELECTED;

		model.setLogEnabled(direction, newValue);
	}

	@Override
	public void dispose() {
		model.removeListener(listener);
		super.dispose();
	}

}
