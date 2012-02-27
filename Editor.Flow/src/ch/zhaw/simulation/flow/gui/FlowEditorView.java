package ch.zhaw.simulation.flow.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.clipboard.flow.FlowTransferable;
import ch.zhaw.simulation.control.flow.DrawModusListener;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.editor.elements.ViewComponent;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ConnectorPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.listener.FlowSimulationListener;

public class FlowEditorView extends AbstractEditorView<FlowEditorControl> implements FlowSimulationListener, DrawModusListener {
	private static final long serialVersionUID = 1L;

	private Vector<ConnectorUi> connectors = new Vector<ConnectorUi>();

	private boolean drawModus = false;
	private AddConnectorUi addConnectorUi;

	private ArrowDragView arrowDrag;

	public FlowEditorView(final FlowEditorControl control) {
		super(control, new TransferableFactory() {

			@Override
			public AbstractTransferable createTransferable(SelectableElement[] selected) {
				return new FlowTransferable(selected, (SimulationFlowModel) control.getModel());
			}

		});

		addConnectorUi = new AddConnectorUi(this, control);

		control.addDrawModusListener(this);

		arrowDrag = new ArrowDragView(addConnectorUi, this);

		add(arrowDrag);
		arrowDrag.setVisible(false);
	}

	@Override
	protected void addModellistener() {
		getControl().getModel().addListener(this);
	}

	@Override
	protected void loadDataFromModel() {
		SimulationFlowModel model = control.getModel();

		for (SimulationObject p : model.getData()) {
			dataAdded(p);
		}

		for (Connector<?> c : model.getConnectors()) {
			connectorAdded(c);
		}
	}

	@Override
	protected void initKeyhandler() {
		super.initKeyhandler();

		// TODO !!! was macht das?
		registerKeyShortcut('$', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (SelectableElement s : selectionModel.getSelected()) {
					if (s instanceof ConnectorPoint) {
						ConnectorPoint c = (ConnectorPoint) s;
						c.getUi().centerMovePoint();
						control.getModel().fireConnectorChanged(c.getConnector());
					}
				}
			}
		});

		registerKeyShortcut('c', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control.addContainer();
			}
		});

		registerKeyShortcut('p', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control.addParameter();
			}
		});

	}

	@Override
	protected void showArrowDrag() {
		SelectableElement[] selected = selectionModel.getSelected();
		if (selected.length == 1 && !drawModus) {
			SelectableElement s = selected[0];
			if (s instanceof ContainerView || s instanceof FlowConnectorParameter || s instanceof ParameterView) {
				arrowDrag.setLocation(s.getX() + s.getWidth(), s.getY());
				arrowDrag.setVisible(true);
				arrowDrag.setElement((GuiDataElement<?>) s);
				return;
			}
		}
		arrowDrag.setVisible(false);
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		DrawHelper.antialisingOn(g);

		// First draw the floconnectors, because they are bigger and may hide
		// the parameterconnectors
		for (ConnectorUi c : connectors) {
			if (c instanceof FlowConnectorUi) {
				c.paint(g);
			}
		}

		for (ConnectorUi c : connectors) {
			if (c instanceof ParameterConnectorUi) {
				c.paint(g);
			}
		}
		paintElements(g);

		if (showSelection) {
			paintSelection(g);
		}
	}

	private void paintElements(Graphics2D g) {
		Vector<ConnectorPoint> tmpPoints = new Vector<ConnectorPoint>();

		for (Component c : getComponents()) {
			Graphics cg = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());

			if (c instanceof ConnectorPoint) {
				tmpPoints.add((ConnectorPoint) c);
			} else if (c instanceof ViewComponent) {
				if (((ViewComponent) c).isDependent()) {
					((ViewComponent) c).paintShadow(g);
				}
				c.paint(cg);
			} else if (!drawModus) {
				if (c.isVisible()) {
					c.paint(cg);
				}
			}
		}

		// Flackern verhindern
		for (ConnectorPoint c : tmpPoints) {
			Graphics cg = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());
			c.paint(cg);
		}

		if (!drawModus) {
			return;
		}
		addConnectorUi.paint(g);
	}

	@Override
	protected boolean dataAddedImpl(SimulationObject o) {
		if (o instanceof SimulationParameter) {
			add(new ParameterView(o.getWidth(), control, (SimulationParameter) o));
			return true;
		} else if (o instanceof SimulationContainer) {
			add(new ContainerView(o.getWidth(), o.getHeight(), control, (SimulationContainer) o));
			return true;
		} else if (o instanceof InfiniteData) {
			add(new InfiniteSymbol((InfiniteData) o, control));
			return true;
		} else if (o instanceof FlowValve) {
			return true;
		}
		return false;
	}

	@Override
	public void dataChanged(SimulationObject o) {
		revalidate();

		GuiDataElement<?> c = findGuiComponent(o);
		if (c == null) {
			repaint();
			return;
		}

		if (control.getModel().getParameterConnectorsTo(o).size() > 0) {
			// Alles neu zeichnen, der Aufwand um zu brechnen was wirklich neu
			// gezeichnet werden muss ist bei kleinen Diagrammen höher als der
			// Aufwand für das ganze neu zeichnen....
			repaint();
		} else {
			c.repaint();
		}

		SimulationObject d = c.getData();
		if (d instanceof NamedSimulationObject && c instanceof GuiDataTextElement<?>) {
			String text = ((NamedSimulationObject) d).getStatusText();
			((GuiDataTextElement<?>) c).setStatus(text);
		}
	}

	@Override
	public void dataRemoved(SimulationObject o) {
		if (o instanceof InfiniteData) {
			removeInfiniteData((InfiniteData) o);
			return;
		}

		super.dataRemoved(o);
	}

	private void removeInfiniteData(InfiniteData o) {
		for (Component c : getComponents()) {
			if (c instanceof InfiniteSymbol) {
				if (((InfiniteSymbol) c).getData() == o) {
					remove(c);
					((InfiniteSymbol) c).dispose();
				}
			}
		}
	}

	public SelectableElement findGuiComponent(ParameterConnector con) {
		for (ConnectorUi c : connectors) {
			if (c instanceof ParameterConnectorUi) {
				ParameterConnectorUi pc = (ParameterConnectorUi) c;

				if (pc.getData() == con) {
					return pc.getSelectableElement();
				}
			}
		}

		return null;
	}

	public Vector<SelectableElement> convertToSelectable(Vector<SimulationObject> shadowData) {
		Vector<SelectableElement> found = new Vector<SelectableElement>();

		for (Component c : getComponents()) {
			if (c instanceof GuiDataElement<?>) {
				SimulationObject d = ((GuiDataElement<?>) c).getData();
				if (shadowData.contains(d)) {
					shadowData.remove(d);
					found.add((SelectableElement) c);
				}
			}
		}

		return found;
	}

	@Override
	public void connectorAdded(Connector<?> c) {
		if (c instanceof FlowConnector) {
			FlowConnectorParameter connectorControl = new FlowConnectorParameter((FlowConnector) c, control);
			add(connectorControl);
			connectors.add(new FlowConnectorUi(this, (FlowConnector) c, control, connectorControl));
			revalidate();
			repaint();
		} else if (c instanceof ParameterConnector) {
			connectors.add(new ParameterConnectorUi(this, (ParameterConnector) c, control));
			revalidate();
			repaint();
		} else {
			throw new RuntimeException("Unknown Connector: " + c.getClass().getName());
		}
	}

	@Override
	public void connectorRemoved(Connector<?> c) {
		for (ConnectorUi ui : connectors.toArray(new ConnectorUi[] {})) {
			if (ui.getData() == c) {
				connectors.remove(ui);
				ui.dispose();
			}
		}
	}

	@Override
	public void connectorChanged(Connector<?> c) {
	}

	@Override
	public void drawModusEnabled() {
		drawModus = true;

		removeMouseListener(selectionListener);
		removeMouseMotionListener(selectionListener);
		removeKeyListener(keyListener);
	}

	@Override
	public void drawModusFinished() {
		drawModus = false;
		addMouseListener(selectionListener);
		addMouseMotionListener(selectionListener);
		addKeyListener(keyListener);
	}

	public boolean isDrawModus() {
		return drawModus;
	}

	public void addConnectArrow() {
		addConnectorUi.addConnectArrow();
	}

	public void addFlowArrow() {
		addConnectorUi.addFlowArrow();
	}

	public void cancelAddArrow() {
		addConnectorUi.cancelAddArrow();
	}

	@Override
	public void clearData() {
		connectors.clear();
		removeAll();
		add(arrowDrag);
	}

	@Override
	protected boolean checkSelection(MouseEvent e) {
		for (ConnectorUi c : connectors) {
			if (c.isConnector(e.getPoint())) {
				SelectableElement selected = c.getSelectableElement();

				if (e.isShiftDown()) {
					selectionModel.addSelected(selected);
				} else if (e.isControlDown()) {
					if (selectionModel.isSelected(selected)) {
						selectionModel.removeSelected(selected);
					} else {
						selectionModel.addSelected(selected);
					}
				} else {
					selectionModel.setSelected(selected);
				}
				return true;
			}
		}
		return false;
	}

}
