package ch.zhaw.simulation.editor.flow.connector.flowarrow;


import java.awt.Point;
import java.util.Vector;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.util.Range;



public class FlowConnectorControl {
	private FlowEditorControl control;
	private FlowConnector connector;

	private FlowCalculator flow1;
	private FlowCalculator flow2;

	private Vector<FlowControlListener> listener = new Vector<FlowControlListener>();
	
	private FlowSimulationAdapter simulationListener;

	public FlowConnectorControl(FlowConnector connector,
			FlowEditorControl control, int size) {
		this.control = control;
		this.connector = connector;

		if(connector.getValve().getX() == -1) {
			centerPoint();
		}

		int arrowSize = control.getSysintegration().getGuiConfig().getFlowArrowSize();
		flow1 = new FlowCalculator(connector.getSource(), connector, 0, 0, size);
		flow2 = new FlowCalculator(connector.getTarget(), connector, arrowSize, arrowSize, size);

		initListener();
		dataChanged();
	}

	public ElementConnector getTarget() {
		return flow2.getTarget();
	}

	private void centerPoint() {
		int x = (connector.getSource().getXCenter() + connector.getTarget().getXCenter()) / 2;
		int y = (connector.getSource().getYCenter() + connector.getTarget().getYCenter()) / 2;
		
		FlowValve pp = connector.getValve();
		
		pp.setX(x - pp.getWidth() / 2);
		pp.setY(y - pp.getHeight() / 2);
	}

	public void dispose() {
		control.getModel().removeListener(simulationListener);
		control.getModel().removeData(connector.getValve());
		
		listener.clear();
		listener = null;
		
		connector.fireFlowConnectorDeleted();
		connector = null;
		
		flow1.dispose();
		flow2.dispose();
	}

	private void initListener() {
		simulationListener = new FlowSimulationAdapter() {
			
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
				if(o == connector.getSource() || o == connector.getTarget() || o == connector.getValve()) {
					FlowConnectorControl.this.dataChanged();
				}
			}
			
			private void checkData(Connector<?> c) {
				if(c == connector) {
					FlowConnectorControl.this.dataChanged();
				}
			}
		};
		control.getModel().addListener(simulationListener);
		
	}

	public void addListener(FlowControlListener l) {
		listener.add(l);
	}
	
	public void removeListener(FlowControlListener l) {
		listener.remove(l);
	}
	
	private void fireRepaint() {
		Range rx = new Range(connector.getSource().getX());
		rx.add(connector.getSource().getX2());
		rx.add(connector.getTarget().getX());
		rx.add(connector.getTarget().getX2());
		
		Range ry = new Range(connector.getSource().getY());
		ry.add(connector.getSource().getY2());
		ry.add(connector.getTarget().getY());
		ry.add(connector.getTarget().getY2());

		for(Point p : flow1.getPoints()) {
			rx.add(p.x);
			ry.add(p.y);
		}

		for(Point p : flow2.getPoints()) {
			rx.add(p.x);
			ry.add(p.y);
		}
		
		for(FlowControlListener l: listener) {
			l.repaint(rx.getMin() - 50, ry.getMin() - 50, rx.getMax() - rx.getMin() + 100, ry.getMax() - ry.getMin() + 100);
		}
	}
	
	public FlowCalculator getFlow1() {
		return flow1;
	}
	
	public FlowCalculator getFlow2() {
		return flow2;
	}

	private void dataChanged() {
		flow1.calc();
		flow2.calc();
		
		fireRepaint();
	}
	
	public interface FlowControlListener {
		void repaint(int x, int y, int width, int height);
	}
}
