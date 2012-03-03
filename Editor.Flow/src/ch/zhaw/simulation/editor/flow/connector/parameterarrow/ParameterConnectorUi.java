package ch.zhaw.simulation.editor.flow.connector.parameterarrow;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.connector.bezier.ConnectorBezierParameterConnector;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;

public class ParameterConnectorUi implements ConnectorUi, SelectionListener {

	private ParameterConnectorData data;

	private ConnectorBezierParameterConnector curve;

	private FlowEditorControl control;

	public ParameterConnectorUi(JComponent parent, ParameterConnectorData connector, FlowEditorControl control) {
		this.control = control;
		this.data = connector;

		control.getSelectionModel().addSelectionListener(this);

		this.curve = new ConnectorBezierParameterConnector(parent, connector, control);
	}

	@Override
	public boolean isConnector(Point point) {
		return curve.isConnector(point);
	}

	@Override
	public void dispose() {
		control.getSelectionModel().removeSelectionListener(this);

		curve.dispose();

		data = null;

		control = null;
	}

	@Override
	public ParameterConnectorData getData() {
		return data;
	}

	@Override
	public void paint(Graphics2D g) {
		curve.paint(g);
	}

	@Override
	public void selectionChanged() {
		curve.setSelected(control.getSelectionModel().isSelected(curve.getMovePoint()));
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}

	@Override
	public SelectableElement getSelectableElement() {
		return curve.getMovePoint();
	}
}
