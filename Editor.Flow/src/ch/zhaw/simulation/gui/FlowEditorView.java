package ch.zhaw.simulation.gui;

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
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.editor.elements.ViewComponent;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ConnectorPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.global.GlobalView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.view.TextView;
import ch.zhaw.simulation.gui.control.DrawModusListener;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionListener;
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

		initSpecialKeyhandler();

		initComponent();

		addConnectorUi = new AddConnectorUi(this, control);

		control.addDrawModusListener(this);

		arrowDrag = new ArrowDragView(addConnectorUi, this);

		add(arrowDrag);
		arrowDrag.setVisible(false);

		setOpaque(false);
	}

	private void initSpecialKeyhandler() {
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

		registerKeyShortcut('g', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control.addGlobal();
			}
		});
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

	private void initComponent() {
		final SimulationFlowModel model = control.getModel();

		for (SimulationObject p : model.getData()) {
			dataAdded(p);
		}

		for (Connector<?> c : model.getConnectors()) {
			connectorAdded(c);
		}

		model.addListener(this);

		addKeyListener(keyListener);

		addMouseMotionListener(selectionListener);
		addMouseListener(selectionListener);

		selectionModel.addSelectionListener(new SelectionListener() {

			@Override
			public void selectionChanged() {
				for (Component c : getComponents()) {
					setLayer(c, DEFAULT_LAYER, 0);
				}

				SelectableElement[] selected = selectionModel.getSelected();

				for (SelectableElement e : selected) {
					setLayer((Component) e, POPUP_LAYER, 1);
				}
				showArrowDrag();
			}

			@Override
			public void selectionMoved(int dX, int dY) {
				showArrowDrag();
			}

			private void showArrowDrag() {
				SelectableElement[] selected = selectionModel.getSelected();
				if (selected.length == 1 && !drawModus) {
					SelectableElement s = selected[0];
					if (s instanceof ContainerView || s instanceof FlowConnectorParameter || s instanceof ParameterView) {
						arrowDrag.setLocation(s.getX() + s.getWidth(), s.getY());
						arrowDrag.setVisible(true);
						arrowDrag.setElement((GuiDataElement<?>) s);
						setLayer(arrowDrag, POPUP_LAYER, 2);
						return;
					}
				}
				arrowDrag.setVisible(false);
			}
		});
	}

	@Override
	public void dataAdded(SimulationObject o) {
		if (o instanceof SimulationParameter) {
			add(new ParameterView(o.getWidth(), control, (SimulationParameter) o));
		} else if (o instanceof SimulationGlobal) {
			add(new GlobalView(o.getWidth(), control, (SimulationGlobal) o));
		} else if (o instanceof SimulationContainer) {
			add(new ContainerView(o.getWidth(), o.getHeight(), control, (SimulationContainer) o));
		} else if (o instanceof InfiniteData) {
			add(new InfiniteSymbol((InfiniteData) o, control));
		} else if (o instanceof FlowValve) {
		} else if (o instanceof TextData) {
			TextView view = new TextView(control, (TextData) o);
			add(view);
			view.paintText();
		} else {
			throw new RuntimeException("Unknown SimulationObject: " + o.getClass().getName());
		}

		revalidate();
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
		}

		GuiDataElement<?> c = findGuiComponent(o);
		if (c != null) {
			remove(c);
			repaint();
			c.dispose();
		}
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

	@Override
	public void dataSaved(boolean saved) {
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

	public GuiDataElement<?> getElementAt(int x, int y) {
		for (Component comp : getComponents()) {
			if (comp instanceof GuiDataElement<?> && comp.getBounds().contains(x, y)) {
				return (GuiDataElement<?>) comp;
			}
		}
		return null;
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

	public void selectElement(SimulationObject o) {
		selectionModel.clearSelection();

		for (Component c : getComponents()) {
			if (c instanceof GuiDataElement<?>) {
				GuiDataElement<?> e = ((GuiDataElement<?>) c);
				SimulationObject d = e.getData();
				if (d.equals(o)) {
					selectionModel.setSelected(e);
					break;
				}
			}
		}

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
