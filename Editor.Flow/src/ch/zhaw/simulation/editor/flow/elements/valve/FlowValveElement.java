package ch.zhaw.simulation.editor.flow.elements.valve;

import java.awt.Point;
import java.awt.event.MouseEvent;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector.ConnectorDeletedListener;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class FlowValveElement extends GuiDataTextElement<FlowValve> implements ConnectorDeletedListener {
	private static final long serialVersionUID = 1L;
	private int size;
	private GuiConfig config;
	private FlowConnector connector;
	private FlowValveImage image;

	public FlowValveElement(FlowConnector connector, FlowEditorControl control) {
		super(connector.getValve(), control);

		config = control.getSysintegration().getGuiConfig();
		size = config.getFlowParameterSize();
		image = new FlowValveImage(size, config);

		textY = 30;
		questionmarkY = 45;

		connector.addConnectorDeletedListener(this);

		this.connector = connector;

		FlowValve p = connector.getValve();
		setLocation(new Point(p.getX(), p.getY()));
	}

	public FlowConnector getConnector() {
		return connector;
	}

	@Override
	protected void doubleClicked(MouseEvent e) {
		getControl().showFormulaEditor(getData());
	}

	@Override
	public void connectorDeleted() {
		connector.removeConnectorDeletedListener(this);
		connector = null;
		config = null;
		image = null;
		super.dispose();
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}
}
