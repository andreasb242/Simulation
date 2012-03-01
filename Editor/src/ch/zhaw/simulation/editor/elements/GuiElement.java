package ch.zhaw.simulation.editor.elements;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.undo.action.MoveUndoAction;

public abstract class GuiElement extends JComponent implements SelectionListener, SelectableElement, ViewComponent {
	private static final long serialVersionUID = 1L;

	private boolean selected = false;
	private SelectionModel selectionModel;

	private MouseAdapter listener;

	private AbstractEditorControl<?> control;
	private AbstractSimulationModel<?> model;

	protected int lastX;
	protected int lastY;

	private boolean dependent;

	public GuiElement(final AbstractEditorControl<?> control) {
		this.selectionModel = control.getSelectionModel();
		this.control = control;
		this.model = control.getModel();

		listener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doubleClicked(e);
					return;
				}

				if (e.isShiftDown()) {
					selectionModel.addSelected(GuiElement.this);
				} else if (e.isControlDown()) {
					if (selectionModel.isSelected(GuiElement.this)) {
						selectionModel.removeSelected(GuiElement.this);
					} else {
						selectionModel.addSelected(GuiElement.this);
					}
				} else {
					selectionModel.setSelected(GuiElement.this);
				}

				getParent().requestFocus();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				GuiElement.this.mouseReleased(e.getPoint());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				lastX = (int) e.getPoint().getX();
				lastY = (int) e.getPoint().getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();

				dragged(p);
			}
		};

		addMouseListener(listener);
		addMouseMotionListener(listener);

		selectionModel.addSelectionListener(this);
	}

	/**
	 * TODO docu
	 * 
	 * @param p
	 */
	protected void mouseReleased(Point p) {
	}

	/**
	 * TODO docu
	 * 
	 * @param p
	 */
	protected void dragged(Point p) {
		// Wenn noch nicht gewählt beim verschieben wählen
		if (!selectionModel.isSelected(this)) {
			selectionModel.setSelected(this);
		}

		int dX = (int) p.getX() - lastX;
		int dY = (int) p.getY() - lastY;
		selectionModel.move(dX, dY);

		control.getUndoManager().addEdit(new MoveUndoAction(selectionModel.getSelected(), dX, dY));
	}

	protected void doubleClicked(MouseEvent e) {
	}

	@Override
	public void paintShadow(Graphics2D g) {
	}

	public AbstractSimulationModel<?> getModel() {
		return model;
	}

	public AbstractEditorControl<?> getControl() {
		return control;
	}

	public void dispose() {
		removeMouseListener(listener);
		removeMouseMotionListener(listener);
		selectionModel.removeSelected(this);
		control = null;
		model = null;
	}

	@Override
	public void selectionChanged() {
		boolean s = selectionModel.isSelected(this);
		boolean d = selectionModel.isDependentElement(this);

		if (s != selected || d != dependent) {
			selected = s;
			dependent = d;

			repaint();
		}
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isDependent() {
		return dependent;
	}
}
