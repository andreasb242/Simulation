package ch.zhaw.simulation.editor.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import butti.javalibs.util.DrawHelper;
import butti.javalibs.util.ExtendableRange;
import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.editor.elements.global.GlobalView;
import ch.zhaw.simulation.editor.imgexport.ImageExport;
import ch.zhaw.simulation.editor.layout.SimulationLayout;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.listener.SimulationListener;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.undo.UndoHandler;

public abstract class AbstractEditorView<C extends AbstractEditorControl<?>> extends JPanel implements SimulationListener, SelectionListener {
	private static final long serialVersionUID = 1L;

	/**
	 * The editor controller
	 */
	protected C control;

	/**
	 * The selection model
	 */
	protected SelectionModel selectionModel;

	/**
	 * Shows a selection rectange (while selecting)
	 */
	protected boolean showSelection = false;

	/**
	 * This is a map mit key shortcuts, e.g. if you press c to add a container
	 */
	private HashMap<Character, ActionListener> keyShortcuts = new HashMap<Character, ActionListener>();

	/**
	 * Undo handler
	 */
	private UndoHandler undoHandler = new UndoHandler();

	/**
	 * Clipboard handler
	 */
	private ClipboardHandler<C> clipboard;

	/**
	 * The key listener
	 */
	protected KeyListener keyListener = new KeyAdapter() {

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

			} else {
				ActionListener listener = keyShortcuts.get(e.getKeyChar());
				if (listener != null) {
					listener.actionPerformed(null);
				}
			}
		}

	};

	/**
	 * Selection coordinates
	 */
	private int sX = 0;
	private int sY = 0;
	private int sWidth = 0;
	private int sHeight = 0;

	/**
	 * Mouse listener, used for selecting
	 */
	protected MouseAdapter selectionListener = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!checkSelection(e)) {
				selectionModel.clearSelection();
			}
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

	public AbstractEditorView(C control, TransferableFactory factory) {
		this.control = control;

		if (control == null) {
			throw new NullPointerException("control == null");
		}

		setLayout(new SimulationLayout());

		clipboard = new ClipboardHandler<C>(control, factory);
		selectionModel = control.getSelectionModel();

		setBackground(Color.WHITE);
		setFocusable(true);
		setOpaque(false);

		initKeyhandler();

		initComponent();
	}

	/**
	 * Initialize keyhandlers for keyboard shortcuts
	 */
	protected void initKeyhandler() {
		registerKeyShortcut('g', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control.addGlobal();
			}
		});

		registerKeyShortcut('t', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				control.addText();
			}
		});
	}

	private void initComponent() {
		addModellistener();

		addKeyListener(keyListener);

		addMouseMotionListener(selectionListener);
		addMouseListener(selectionListener);

		selectionModel.addSelectionListener(this);
	}

	@Override
	public void selectionChanged() {
		showArrowDrag();

		bringSelectedObjectsToFront();
	}

	protected void bringSelectedObjectsToFront() {
		for (SelectableElement<?> se : selectionModel.getSelected()) {
			JComponent comp = (JComponent) se;
			setComponentZOrder(comp, 0);
		}
	}

	@Override
	public void selectionMoved(int dX, int dY) {
		showArrowDrag();
	}

	/**
	 * Loads all data from the model to the view
	 */
	protected abstract void loadDataFromModel();

	/**
	 * Adds a listener to the model
	 */
	protected abstract void addModellistener();

	/**
	 * The method is only used for arrow dragging
	 */
	protected void showArrowDrag() {
	}

	/**
	 * Register a special action for as key shortcut
	 * 
	 * @param key
	 *            The key char (e.g. 'c')
	 * @param action
	 *            The action to perform if the key is pressed
	 */
	public void registerKeyShortcut(char key, ActionListener action) {
		keyShortcuts.put(key, action);
	}

	/**
	 * @return The controller of this editor
	 */
	public C getControl() {
		return control;
	}

	/**
	 * @return The Simulation Type supported by this view
	 */
	public abstract SimulationType getSimulationType();

	public Rectangle getSelectionRange() {
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

	/**
	 * Draws the selection rectangle
	 * 
	 * @param g
	 *            The graphics to draw on
	 */
	protected void paintSelection(Graphics2D g) {
		// Selektion zeichnen
		GuiConfig cfg = control.getSysintegration().getGuiConfig();

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

	@Override
	public final void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		DrawHelper.antialisingOn(g);

		paintEditor(g);
	}

	protected abstract void paintEditor(Graphics2D g);

	private void updateTempSelection() {
		Vector<SelectableElement<?>> tmp = new Vector<SelectableElement<?>>();

		for (Component c : getComponents()) {
			if (c instanceof SelectableElement) {
				if (isInSelection(c)) {
					tmp.add((SelectableElement<?>) c);
				}
			}
		}

		selectionModel.setTmpSelection(tmp);
	}

	protected void paintElements(Graphics2D g) {
		for (int i = getComponentCount() - 1; i >= 0; i--) {
			Component c = getComponent(i);
			if (c instanceof SelectableElement) {
				paintSubComponent(g, c);
			} else {
				if (c.isVisible()) {
					Graphics cg = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());
					c.paint(cg);
				}
			}
		}
	}

	protected abstract void paintSubComponent(Graphics2D g, Component c);

	/**
	 * Checks if a component is within the selected range
	 * 
	 * @return true if within
	 */
	private boolean isInSelection(Component c) {
		return isInSelection(c.getX(), c.getY(), c.getWidth(), c.getHeight());
	}

	/**
	 * Checks if a rectangle is within the selected range
	 * 
	 * @return true if within
	 */
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

	/**
	 * Searches a gui representation of a model element
	 * 
	 * @param b
	 *            The Model element
	 * @return The GuiElement or <code>null</code> if not found
	 */
	public AbstractDataView<?> findGuiComponent(AbstractSimulationData b) {
		for (Component c : getComponents()) {
			if (c instanceof AbstractDataView<?>) {
				AbstractDataView<?> e = (AbstractDataView<?>) c;

				if (e.getData() == b) {
					return e;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the selectable element for Data Objects, this method is
	 * overwritten and works also with connectors
	 * 
	 * @param o
	 *            The data object
	 * @return The SelectableElement
	 */
	public SelectableElement<?> findGuiComponentForObj(Object o) {
		if (o == null) {
			return null;
		}
		if (!(o instanceof AbstractSimulationData)) {
			throw new RuntimeException("o is not instanceof AbstractSimulationData: " + o.getClass());
		}
		return findGuiComponent((AbstractSimulationData) o);
	}

	public UndoHandler getUndoHandler() {
		return undoHandler;
	}

	public ClipboardHandler<C> getClipboard() {
		return clipboard;
	}

	/**
	 * Check if there is something in the view to select
	 * 
	 * @param e
	 *            The mouse event
	 * @return true if something is selected, false otherwise
	 */
	protected boolean checkSelection(MouseEvent e) {
		return false;
	}

	public AbstractDataView<?> getElementAt(int x, int y) {
		for (Component comp : getComponents()) {
			if (comp instanceof AbstractDataView<?> && comp.getBounds().contains(x, y)) {
				return (AbstractDataView<?>) comp;
			}
		}
		return null;
	}

	public void selectElement(AbstractSimulationData o) {
		selectionModel.clearSelection();

		for (Component c : getComponents()) {
			if (c instanceof AbstractDataView<?>) {
				AbstractDataView<?> e = ((AbstractDataView<?>) c);
				AbstractSimulationData d = e.getData();
				if (d.equals(o)) {
					selectionModel.setSelected(e);
					break;
				}
			}
		}

	}

	/**
	 * Overwrite this method to handle dataAdded events, @see dataAdded
	 */
	protected boolean dataAddedImpl(AbstractSimulationData o) {
		return false;
	}

	@Override
	public final void dataAdded(AbstractSimulationData o) {
		if (dataAddedImpl(o)) {
			// nothing to do here
		} else if (o instanceof SimulationGlobalData) {
			add(new GlobalView(o.getWidth(), control, (SimulationGlobalData) o));
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
	public void dataRemoved(AbstractSimulationData o) {
		AbstractDataView<?> c = findGuiComponent(o);
		if (c != null) {
			remove(c);
			repaint();
			c.dispose();
		}
	}

	@Override
	public void dataChanged(AbstractSimulationData o) {
		revalidate();

		AbstractDataView<?> c = findGuiComponent(o);
		if (c == null) {
			repaint();
			return;
		}

		AbstractSimulationData d = c.getData();
		if (d instanceof AbstractNamedSimulationData && c instanceof GuiDataTextElement<?>) {
			String text = ((AbstractNamedSimulationData) d).getStatusText();
			((GuiDataTextElement<?>) c).setStatus(text);
		}
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
		removeAll();
	}

	public void dispose() {
		if (getControl() != null && getControl().getModel() != null) {
			getControl().getModel().removeListener(this);
		}
		selectionModel.removeSelectionListener(this);
	}

	public static boolean isBezierHelperPoint(Object c) {
		return "BezierHelperPoint".equals(c.getClass().getSimpleName());
	}

	public void visitElements(ImageExport export, boolean exportHelperPoints) {
		for (int i = 0; i < getComponentCount(); i++) {
			Component c = getComponent(i);
			if (!exportHelperPoints && isBezierHelperPoint(c)) {
				continue;
			}

			if ("ArrowDragView".equals(c.getClass().getSimpleName())) {
				continue;
			}

			export.draw(c);
		}
	}

	/**
	 * Calculates the size of the selection, should my be overridde to handle
	 * the bezier splines better
	 */
	public Rectangle calcSizeSelection(boolean exportBezierHelperPoint) {
		ExtendableRange r = new ExtendableRange();

		SelectionModel selection = control.getSelectionModel();
		for (SelectableElement<?> s : selection.getSelected()) {
			/*
			 * Add the helper points always to the range, because the bounds of
			 * the bezier splines are anyway to large, so there is no difference
			 * to adding all helper points, except we don't have redundant code
			 */

			// if (!exportBezierHelperPoint &&
			// AbstractEditorView.isBezierHelperPoint(s)) {
			// continue;
			// }

			r.addRect(new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight()));
		}

		return r.getRect();
	}
}
