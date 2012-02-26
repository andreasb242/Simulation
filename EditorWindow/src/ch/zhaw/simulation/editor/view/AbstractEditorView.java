package ch.zhaw.simulation.editor.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JLayeredPane;

import ch.zhaw.simulation.clipboard.ClipboardHandler;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.model.flow.SimulationObject;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.undo.UndoHandler;

public abstract class AbstractEditorView<C extends AbstractEditorControl> extends JLayeredPane {
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
	// TODO: !! private
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
	// TODO: !! private
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
	// TODO: !! private
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

	public AbstractEditorView(C control) {
		this.control = control;

		if (control == null) {
			throw new NullPointerException("control == null");
		}

		clipboard = new ClipboardHandler<C>(control);
		selectionModel = control.getSelectionModel();

		setBackground(Color.WHITE);
		setFocusable(true);

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
	 * @return The current selected rectangle
	 */
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
	 * Searches a rgui representation of a model element
	 * 
	 * @param b
	 *            The Model element
	 * @return The GuiElement or <code>null</code> if not found
	 */
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
}
