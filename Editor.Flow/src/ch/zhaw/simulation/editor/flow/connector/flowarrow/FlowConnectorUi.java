package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.connector.bezier.FlowBezierConnector;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.valve.FlowValveElement;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class FlowConnectorUi implements ConnectorUi, SelectionListener {
	private FlowConnectorControl flowControl;

	private FlowArrowImage arrowImage;
	private JComponent parent;

	private FlowConnector connector;
	private GuiConfig config;

	private FlowValveElement valve;

	private FlowEditorControl control;

	private FlowBezierConnector connector1;
	private FlowBezierConnector connector2;

	private Rectangle lastBound = new Rectangle();

	private FlowSimulationAdapter simulationListener = new FlowSimulationAdapter() {

		@Override
		public void dataAdded(SimulationObject o) {
			checkData(o);
		}

		@Override
		public void dataRemoved(SimulationObject o) {
			checkData(o);
		}

		@Override
		public void dataChanged(SimulationObject o) {
			checkData(o);
		}

		@Override
		public void connectorAdded(Connector<?> c) {
			checkData(c);
		}

		@Override
		public void connectorChanged(Connector<?> c) {
			checkData(c);
		}

		@Override
		public void connectorRemoved(Connector<?> c) {
			checkData(c);
		}

		private void checkData(SimulationObject o) {
			if (o == connector.getSource() || o == connector.getTarget() || o == connector.getValve()) {
				FlowConnectorUi.this.fireRepaint();
			}
		}

		private void checkData(Connector<?> c) {
			if (c == connector) {
				FlowConnectorUi.this.fireRepaint();
			}
		}
	};

	public FlowConnectorUi(JComponent parent, FlowConnector connector, FlowEditorControl control, FlowValveElement valve) {
		this.parent = parent;
		this.valve = valve;
		this.connector = connector;
		this.config = control.getSysintegration().getGuiConfig();
		this.control = control;

		int arrowSize = config.getFlowArrowSize();
		arrowImage = new FlowArrowImage(arrowSize, config);

		flowControl = new FlowConnectorControl(connector, control, arrowImage.getArrowWidth());

		control.getSelectionModel().addSelectionListener(this);

		connector1 = new FlowBezierConnector(parent, connector.getBezierSource(), control, false);
		connector2 = new FlowBezierConnector(parent, connector.getBezierTarget(), control, true);

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
		connector1 = null;
		connector2 = null;
	}

	@Override
	public FlowConnector getData() {
		return connector;
	}

	@Override
	public void paint(Graphics2D g) {
		connector1.paint(g);
		connector2.paint(g);
	}

	@Override
	public SelectableElement getSelectableElement() {
		return valve;
	}

	@Override
	public boolean isConnector(Point point) {
		return connector1.isConnector(point) || connector2.isConnector(point);
	}

	private void fireRepaint() {
		// TODO wird nie aufgerufen!!
		System.out.println("repaint");

		Rectangle bounds = connector1.getBounds();

		Rectangle r = bounds.union((Rectangle) lastBound);

		// TODO repaint 
//		parent.repaint(r.x - 100, r.y - 100, r.width + 200, r.height + 200);
		parent.repaint();

		lastBound = bounds;
	}

	@Override
	public void selectionChanged() {
		boolean selected = control.getSelectionModel().isSelected(valve);
		connector1.setSelected(selected);
		connector2.setSelected(selected);
	}

	@Override
	public void selectionMoved(int dX, int dY) {
		if (control.getSelectionModel().isSelected(valve) || control.getSelectionModel().isSelected(connector1.getMovePoint())
				|| control.getSelectionModel().isSelected(connector2.getMovePoint())) {
			FlowConnectorUi.this.fireRepaint();
		}
	}
}
