package ch.zhaw.simulation.gui;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLayeredPane;

import ch.zhaw.simulation.editor.connector.ConnectorUi;
import ch.zhaw.simulation.editor.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.connector.flowarrow.FlowConnectorUi;
import ch.zhaw.simulation.editor.connector.parameterarrow.ConnectorPoint;
import ch.zhaw.simulation.editor.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.editor.elements.GuiDataTextElement;
import ch.zhaw.simulation.editor.elements.TextView;
import ch.zhaw.simulation.editor.elements.container.ContainerView;
import ch.zhaw.simulation.editor.elements.global.GlobalView;
import ch.zhaw.simulation.editor.elements.parameter.ParameterView;
import ch.zhaw.simulation.gui.control.DrawModusListener;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.gui.layout.SimulationLayout;
import ch.zhaw.simulation.model.InfiniteData;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.model.SimulationListener;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.TextData;
import ch.zhaw.simulation.model.connection.Connector;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;
import ch.zhaw.simulation.model.connection.ParameterConnector;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.util.DrawHelper;


public class DocumentView extends JLayeredPane implements SimulationListener, DrawModusListener {
	private static final long serialVersionUID = 1L;

	private SelectionModel selectionModel;

	private SimulationControl control;

	private Vector<ConnectorUi> connectors = new Vector<ConnectorUi>();

	private boolean showSelection = false;
	private int sX = 0;
	private int sY = 0;
	private int sWidth = 0;
	private int sHeight = 0;

	private boolean drawModus = false;
	private AddConnectorUi addConnectorUi;

	private ArrowDragView arrowDrag;

	private MouseAdapter selectionListener = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
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
					return;
				}
			}

			selectionModel.clearSelection();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			sX = (int) e.getPoint().getX();
			sY = (int) e.getPoint().getY();
			showSelection = true;
			requestFocus();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			showSelection = false;
			sWidth = 0;
			sHeight = 0;

			selectionModel.acceptTmpSelection();

			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			sWidth = (int) e.getPoint().getX() - sX;
			sHeight = (int) e.getPoint().getY() - sY;
			updateTempSelection();
			repaint();
		}

	};

	private KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (showSelection) {
					showSelection = false;
					selectionModel.clearTmpSelection();
					repaint();
				} else {
					selectionModel.clearSelection();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				control.deleteSelected();
				e.consume();
			} else if (e.getKeyChar() == '$') {
				for (SelectableElement s : selectionModel.getSelected()) {
					if (s instanceof ConnectorPoint) {
						ConnectorPoint c = (ConnectorPoint) s;
						c.getUi().centerMovePoint();
						control.getModel().fireConnectorChanged(c.getConnector());
					}
				}
			} else if (e.getKeyChar() == 'c') {
				control.addContainer();
			} else if (e.getKeyChar() == 'p') {
				control.addParameter();
			} else if (e.getKeyChar() == 'g') {
				control.addGlobal();
			}
		}

	};

	public DocumentView(SimulationControl control) {
		this.control = control;

		selectionModel = control.getSelectionModel();

		setBackground(Color.WHITE);
		setFocusable(true);

		setLayout(new SimulationLayout());

		initComponent();

		addConnectorUi = new AddConnectorUi(this, control);

		control.addDrawModusListener(this);

		arrowDrag = new ArrowDragView(addConnectorUi, this);

		add(arrowDrag);
		arrowDrag.setVisible(false);

		setOpaque(false);
	}

	private void updateTempSelection() {
		Vector<SelectableElement> tmp = new Vector<SelectableElement>();

		for (Component c : getComponents()) {
			if (c instanceof SelectableElement) {
				if (isInSelection(c)) {
					tmp.add((SelectableElement) c);
				}
			}
		}

		selectionModel.setTmpSelection(tmp);
	}

	private boolean isInSelection(Component c) {
		return isInSelection(c.getX(), c.getY(), c.getWidth(), c.getHeight());
	}

	private boolean isInSelection(int x, int y, int w, int h) {
		Rectangle selectionRange = getSelectionRange();

		int sX = (int) selectionRange.getX();
		int sY = (int) selectionRange.getY();
		int sW = (int) selectionRange.getWidth();
		int sH = (int) selectionRange.getHeight();

		int hw = w / 2;
		int hh = h / 2;

		int ex = x + w;
		int ey = y + h;

		// obere bzw. linke Hälfe selektiert
		if (x + hw >= sX && y + hh >= sY && ex <= sX + sW && ey <= sY + sH) {
			return true;
		}

		// Untere bzw. rechte Hälfte selektiert
		if (x >= sX && y >= sY && ex - hw <= sX + sW && ey - hh <= sY + sH) {
			return true;
		}

		return false;
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

	private void paintSelection(Graphics2D g) {
		// Selektion zeichnen
		GuiConfig cfg = control.getConfig();

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cfg.getSelectionAlpha()));

		g.setColor(cfg.getSelectionForegroundColor());

		Rectangle selectionRange = getSelectionRange();

		int x = (int) selectionRange.getX();
		int y = (int) selectionRange.getY();
		int w = (int) selectionRange.getWidth();
		int h = (int) selectionRange.getHeight();

		g.fillRect(x, y, w, h);

		g.setColor(cfg.getSelectionColor());
		g.drawRect(x, y, w, h);
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

	private Rectangle getSelectionRange() {
		int x = sX;
		int y = sY;
		int w = sWidth;
		int h = sHeight;

		x = Math.max(0, x);
		y = Math.max(0, y);

		if (sWidth < 0) {
			x += sWidth;
			w = -sWidth;
		}

		if (sHeight < 0) {
			y += sHeight;
			h = -sHeight;
		}

		return new Rectangle(x, y, w, h);
	}

	private void initComponent() {
		final SimulationDocument model = control.getModel();

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

	public GuiDataElement<?> findGuiComponent(SimulationObject b) {
		for (Component c : getComponents()) {
			if (c instanceof GuiDataElement<?>) {
				GuiDataElement<?> e = (GuiDataElement<?>) c;

				if (e.getData() == b) {
					return e;
				}
			}
		}

		return null;
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
		} else if (o instanceof FlowParameterPoint) {
		} else if (o instanceof TextData) {
			TextView view = new TextView(control, (TextData) o);
			add(view);
			view.paintText();
		} else {
			throw new RuntimeException("Unknown SimulationObject: " + o.getClass().getName());
		}
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
}
