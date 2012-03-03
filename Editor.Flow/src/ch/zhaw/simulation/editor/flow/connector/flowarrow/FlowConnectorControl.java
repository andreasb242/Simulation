package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;

public class FlowConnectorControl {
	private FlowEditorControl control;
	private FlowConnector connector;

	public FlowConnectorControl(FlowConnector connector, FlowEditorControl control, int size) {
		this.control = control;
		this.connector = connector;

		if (connector.getValve().getX() == -1) {
			centerPoint();
		}
	}

	private void centerPoint() {
		int x = (connector.getSource().getXCenter() + connector.getTarget().getXCenter()) / 2;
		int y = (connector.getSource().getYCenter() + connector.getTarget().getYCenter()) / 2;

		FlowValve pp = connector.getValve();

		pp.setX(x - pp.getWidth() / 2);
		pp.setY(y - pp.getHeight() / 2);
	}

	public void dispose() {
		control.getModel().removeData(connector.getValve());

		connector.fireFlowConnectorDeleted();
		connector = null;
	}
}
