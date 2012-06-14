package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.connector.bezier.FlowBezierConnector;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.valve.FlowValveElement;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class FlowConnectorUi implements ConnectorUi, SelectionListener {
	private FlowConnectorControl flowControl;

	private FlowArrowImage arrowImage;
	private JComponent parent;

	private FlowConnectorData connector;
	private GuiConfig config;

	private FlowValveElement valve;

	private FlowEditorControl control;

	private FlowBezierConnector connector1;
	private FlowBezierConnector connector2;

	private FlowSimulationAdapter simulationListener = new FlowSimulationAdapter() {

		@Override
		public void dataAdded(AbstractSimulationData o) {
			checkData(o);
		}

		@Override
		public void dataRemoved(AbstractSimulationData o) {
			checkData(o);
		}

		@Override
		public void dataChanged(AbstractSimulationData o) {
			checkData(o);
		}

		@Override
		public void connectorAdded(AbstractConnectorData<?> c) {
			checkData(c);
		}

		@Override
		public void connectorChanged(AbstractConnectorData<?> c) {
			checkData(c);
		}

		@Override
		public void connectorRemoved(AbstractConnectorData<?> c) {
			checkData(c);
		}

		private void checkData(AbstractSimulationData o) {
			if (o == connector.getSource() || o == connector.getTarget() || o == connector.getValve()) {
				FlowConnectorUi.this.fireRepaint();
			}
		}

		private void checkData(AbstractConnectorData<?> c) {
			if (c == connector) {
				FlowConnectorUi.this.fireRepaint();
			}
		}
	};

	public FlowConnectorUi(JComponent parent, FlowConnectorData connector, FlowEditorControl control, FlowValveElement valve) {
		this.parent = parent;
		this.valve = valve;
		this.connector = connector;
		this.config = control.getSysintegration().getGuiConfig();
		this.control = control;

		int arrowSize = config.getFlowArrowSize();
		arrowImage = new FlowArrowImage(arrowSize, config);

		flowControl = new FlowConnectorControl(connector, control, arrowImage.getArrowWidth());

		control.getSelectionModel().addSelectionListener(this);

		connector1 = new FlowBezierConnector(parent, connector.getBezierSource(), connector, control, false);
		connector2 = new FlowBezierConnector(parent, connector.getBezierTarget(), connector, control, true);

		control.getModel().addListener(simulationListener);
	}

	@Override
	public void dispose() {
		parent = null;
		control.getSelectionModel().removeSelectionListener(this);
		control.getModel().removeListener(simulationListener);

		control = null;
		flowControl.dispose();
		flowControl = null;
		connector = null;
		valve = null;

		connector1.dispose();
		connector2.dispose();

		connector1 = null;
		connector2 = null;
	}

	@Override
	public FlowConnectorData getData() {
		return connector;
	}

	@Override
	public void paint(Graphics2D g) {
		connector1.paint(g);
		connector2.paint(g);
	}

	@Override
	public void paint(Graphics2D g, boolean selected) {
		boolean oldSelection1 = connector1.isSelected();
		boolean oldSelection2 = connector2.isSelected();
		connector1.setSelected(selected);
		connector2.setSelected(selected);

		connector1.paint(g);
		connector2.paint(g);

		connector1.setSelected(oldSelection1);
		connector2.setSelected(oldSelection2);
	}

	@Override
	public SelectableElement<?>[] getSelectableElements() {
		return new SelectableElement[] { valve, connector1.getMovePoint(), connector2.getMovePoint() };
	}

	@Override
	public boolean isConnector(Point point) {
		return connector1.isConnector(point) || connector2.isConnector(point);
	}

	private void fireRepaint() {
		Rectangle bounds1 = connector1.getAndClearRepaintBounds();
		Rectangle bounds2 = connector2.getAndClearRepaintBounds();

		Rectangle r = bounds1.union(bounds2);

		// extend the range so our arrows are also correct repainted
		parent.repaint(r.x - 50, r.y - 50, r.width + 100, r.height + 100);
	}

	public Rectangle getBounds() {
		Rectangle bounds1 = connector1.getBounds();
		Rectangle bounds2 = connector2.getBounds();

		return bounds1.union(bounds2);
	}

	@Override
	public void selectionChanged() {
		boolean selected = control.getSelectionModel().isSelected(valve);
		boolean s1 = connector1.setSelected(selected);
		boolean s2 = connector2.setSelected(selected);
		if (s1 || s2) {
			fireRepaint();
		}
	}

	@Override
	public void selectionMoved(int dX, int dY) {
		if (control.getSelectionModel().isSelected(valve) || control.getSelectionModel().isSelected(connector1.getMovePoint())
				|| control.getSelectionModel().isSelected(connector2.getMovePoint())) {
			FlowConnectorUi.this.fireRepaint();
		}
	}
}
