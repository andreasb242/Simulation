package ch.zhaw.simulation.editor.flow.connector.parameterarrow;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.connector.bezier.ConnectorBezierParameterConnector;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;

public class ParameterConnectorUi implements ConnectorUi, SelectionListener {

	private ParameterConnectorData data;

	private ConnectorBezierParameterConnector curve;

	private FlowEditorControl control;

	private FlowSimulationAdapter listener = new FlowSimulationAdapter() {

		@Override
		public void connectorChanged(AbstractConnectorData<?> c) {
			if (c == data) {
				repaintConenctor();
			}
		};
	};

	private JComponent parent;

	public ParameterConnectorUi(JComponent parent, ParameterConnectorData connector, FlowEditorControl control) {
		this.control = control;
		this.data = connector;
		this.parent = parent;

		control.getSelectionModel().addSelectionListener(this);
		control.getModel().addListener(listener);

		this.curve = new ConnectorBezierParameterConnector(parent, connector, connector, control);
	}

	@Override
	public boolean isConnector(Point point) {
		return curve.isConnector(point);
	}

	@Override
	public void dispose() {
		control.getModel().removeListener(listener);
		control.getSelectionModel().removeSelectionListener(this);

		curve.dispose();

		data = null;

		control = null;

		parent = null;
	}

	@Override
	public ParameterConnectorData getData() {
		return data;
	}

	private void repaintConenctor() {
		Rectangle rect = this.curve.getAndClearRepaintBounds();
		parent.repaint(rect.x - 50, rect.y - 50, rect.width + 100, rect.height + 100);
	}

	@Override
	public void paint(Graphics2D g) {
		curve.paint(g);
	}

	@Override
	public void selectionChanged() {
		curve.setSelected(control.getSelectionModel().isSelected(curve.getMovePoint()));
		repaintConenctor();
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}

	@Override
	public SelectableElement<?>[] getSelectableElements() {
		return new SelectableElement<?>[] { curve.getMovePoint() };
	}
}
