package ch.zhaw.simulation.flow.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Vector;

import butti.javalibs.util.ExtendableRange;
import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.clipboard.flow.FlowTransferable;
import ch.zhaw.simulation.control.flow.DrawModusListener;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.editor.elements.ViewComponent;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.BezierHelperPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.density.DensityContainerView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.flow.elements.valve.FlowValveElement;
import ch.zhaw.simulation.editor.imgexport.ImageExport;
import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.listener.FlowSimulationListener;
import ch.zhaw.simulation.model.selection.SelectableElement;

public class FlowEditorView extends AbstractEditorView<FlowEditorControl> implements FlowSimulationListener, DrawModusListener {
	private static final long serialVersionUID = 1L;

	private Vector<ConnectorUi> connectors = new Vector<ConnectorUi>();

	private boolean drawModus = false;
	private AddConnectorUi addConnectorUi;

	private ArrowDragView arrowDrag;

	/**
	 * The point handles, needed only for drawing components
	 */
	private LinkedList<BezierHelperPoint> tmpPoints = new LinkedList<BezierHelperPoint>();

	private boolean mainWindow;

	private boolean showGrid;

	public static final int FLOW_GRID_RASTER = 20;

	public FlowEditorView(final FlowEditorControl control, boolean mainWindow) {
		super(control, new TransferableFactory() {

			@Override
			public AbstractTransferable<?> createTransferable(SelectableElement<?>[] selected) {
				return new FlowTransferable(control, selected, (SimulationFlowModel) control.getModel());
			}

		});

		this.mainWindow = mainWindow;

		this.showGrid = control.getSettings().isSetting("flowview.showgrid", false);

		addConnectorUi = new AddConnectorUi(this, control);

		control.addDrawModusListener(this);

		this.arrowDrag = new ArrowDragView(addConnectorUi, this);

		add(this.arrowDrag);
		this.arrowDrag.setVisible(false);

		loadDataFromModel();
	}

	@Override
	public SimulationType getSimulationType() {
		return SimulationType.FLOW_SIMULATION;
	}

	@Override
	protected void addModellistener() {
		getControl().getModel().addListener(this);
	}

	@Override
	protected void loadDataFromModel() {
		SimulationFlowModel model = control.getModel();

		for (AbstractSimulationData p : model.getData()) {
			dataAdded(p);
		}

		for (AbstractConnectorData<?> c : model.getConnectors()) {
			connectorAdded(c);
		}
	}

	@Override
	protected void initKeyhandler() {
		super.initKeyhandler();

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

		if (!this.mainWindow) {
			registerKeyShortcut('d', new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					control.addDensity();
				}
			});
		}
	}

	@Override
	protected void showArrowDrag() {
		SelectableElement<?>[] selected = selectionModel.getSelected();
		if (selected.length == 1 && !drawModus) {
			SelectableElement<?> s = selected[0];
			if (s instanceof ContainerView || s instanceof FlowValveElement || s instanceof ParameterView) {
				AbstractSimulationData data = (AbstractSimulationData) s.getData();

				/*
				 * If we access s.getX() and getY() we get zero, because they
				 * return the position of the JComponent, which is set by the
				 * layout manger, but if we e.g. copy & paste the layout manager
				 * didn't place the components.
				 * 
				 * So we access the underlying model, which always contains the
				 * right values
				 */

				arrowDrag.setLocation(data.getX() + data.getWidth(), data.getY());
				arrowDrag.setVisible(true);
				arrowDrag.setElement((AbstractDataView<?>) s);
				return;
			}
		}
		arrowDrag.setVisible(false);
	}

	public void showGrid(boolean visible) {
		this.showGrid = visible;
		repaint();
	}
	
	public boolean isShowGrid() {
		return showGrid;
	}

	@Override
	protected void paintEditor(Graphics2D g) {
		if (this.showGrid) {
			drawGrid(g);
		}

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

	private void drawGrid(Graphics2D g) {
		int w = getWidth();
		int h = getHeight();
		
		g.setColor(Color.LIGHT_GRAY);
		
		for (int x = FLOW_GRID_RASTER; x < w; x += FLOW_GRID_RASTER) {
			g.drawLine(x, 0, x, h);
		}

		for (int y = FLOW_GRID_RASTER; y < h; y += FLOW_GRID_RASTER) {
			g.drawLine(0, y, w, y);
		}
	}

	@Override
	protected void paintElements(Graphics2D g) {
		tmpPoints.clear();
		super.paintElements(g);

		for (BezierHelperPoint c : tmpPoints) {
			Graphics cg = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());
			c.paint(cg);
		}

		if (!drawModus) {
			return;
		}
		addConnectorUi.paint(g);
	}

	@Override
	protected void paintSubComponent(Graphics2D g, Component c) {
		Graphics cg = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());

		if (c instanceof BezierHelperPoint) {
			tmpPoints.add((BezierHelperPoint) c);
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

	@Override
	protected boolean dataAddedImpl(AbstractSimulationData o) {
		if (o instanceof SimulationParameterData) {
			add(new ParameterView(o.getWidth(), control, (SimulationParameterData) o));
			return true;
		} else if (o instanceof SimulationContainerData) {
			add(new ContainerView(o.getWidth(), o.getHeight(), control, (SimulationContainerData) o));
			return true;
		} else if (o instanceof SimulationDensityContainerData) {
			add(new DensityContainerView(o.getWidth(), o.getHeight(), control, (SimulationDensityContainerData) o));
			return true;
		} else if (o instanceof InfiniteData) {
			add(new InfiniteSymbol((InfiniteData) o, control));
			return true;
		} else if (o instanceof FlowValveData) {
			return true;
		}
		return false;
	}

	public void visitElements(ImageExport export, boolean exportHelperPoints) {
		for (ConnectorUi c : connectors) {
			if (c instanceof FlowConnectorUi) {
				export.draw(c);
			}
		}

		for (ConnectorUi c : connectors) {
			if (c instanceof ParameterConnectorUi) {
				export.draw(c);
			}
		}

		super.visitElements(export, exportHelperPoints);
	}

	@Override
	public void dataChanged(AbstractSimulationData o) {
		revalidate();

		/**
		 * This code is for repaint handling, its not really nice, but if we
		 * repaint everything the application gets to slow, so this is a little
		 * workaround to get fast responsibility
		 */

		AbstractDataView<?> c = findGuiComponent(o);
		if (c == null) {
			repaint();
			return;
		}

		Vector<AbstractConnectorData<?>> connectors = control.getModel().getConnectorsTo(o);

		if (connectors.size() > 0) {
			repaintRect(c, connectors);
		} else {
			c.repaint();
		}

		AbstractSimulationData d = c.getData();
		if (d instanceof AbstractNamedSimulationData && c instanceof GuiDataTextElement<?>) {
			String text = ((AbstractNamedSimulationData) d).getStatusText();
			((GuiDataTextElement<?>) c).setStatus(text);
		}
	}

	private void repaintRect(AbstractDataView<?> c, Vector<AbstractConnectorData<?>> connectors) {
		ExtendableRange range = new ExtendableRange();

		range.addRect(c.getBounds());

		for (ConnectorUi con : this.connectors) {
			if (connectors.remove(con.getData())) {
				for (SelectableElement<?> sel : con.getSelectableElements()) {
					range.addPoint(sel.getX(), sel.getY());
					range.addPoint(sel.getX() + sel.getWidth(), sel.getY() + sel.getHeight());
				}

				addElementToRange(range, con.getData().getSource());
				addElementToRange(range, con.getData().getTarget());
			}
		}

		repaint(range.getX(), range.getY(), range.getWidth(), range.getHeight());
	}

	private void addElementToRange(ExtendableRange range, AbstractSimulationData obj) {
		AbstractDataView<?> c = findGuiComponent(obj);
		if (c == null) {
			return;
		}
		range.addRect(c.getBounds());
	}

	@Override
	public void dataRemoved(AbstractSimulationData o) {
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

	public SelectableElement<?>[] findGuiComponent(AbstractConnectorData<?> con) {
		for (ConnectorUi c : connectors) {
			if (c.getData() == con) {
				return c.getSelectableElements();
			}
		}

		return null;
	}

	public Vector<SelectableElement<?>> convertToSelectable(Vector<AbstractSimulationData> shadowData) {
		Vector<SelectableElement<?>> found = new Vector<SelectableElement<?>>();

		for (Component c : getComponents()) {
			if (c instanceof AbstractDataView<?>) {
				AbstractSimulationData d = ((AbstractDataView<?>) c).getData();
				if (shadowData.contains(d)) {
					shadowData.remove(d);
					found.add((SelectableElement<?>) c);
				}
			}
		}

		return found;
	}

	@Override
	public void connectorAdded(AbstractConnectorData<?> c) {
		if (c instanceof FlowConnectorData) {
			FlowValveElement connectorControl = new FlowValveElement((FlowConnectorData) c, control);
			add(connectorControl);
			connectors.add(new FlowConnectorUi(this, (FlowConnectorData) c, control, connectorControl));
			revalidate();
			repaint();
		} else if (c instanceof ParameterConnectorData) {
			connectors.add(new ParameterConnectorUi(this, (ParameterConnectorData) c, control));
			revalidate();
			repaint();
		} else {
			throw new RuntimeException("Unknown Connector: " + c.getClass().getName());
		}
	}

	@Override
	public void connectorRemoved(AbstractConnectorData<?> c) {
		for (ConnectorUi ui : connectors.toArray(new ConnectorUi[] {})) {
			if (ui.getData() == c) {
				connectors.remove(ui);
				ui.dispose();
			}
		}
	}

	@Override
	public void connectorChanged(AbstractConnectorData<?> c) {
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
				SelectableElement<?>[] selected = c.getSelectableElements();

				if (e.isShiftDown()) {
					selectionModel.addSelected(selected);
				} else if (e.isControlDown()) {
					if (selectionModel.isSelected(selected[0])) {
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
