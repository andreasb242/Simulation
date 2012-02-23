package ch.zhaw.simulation.gui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ParameterConnectorUi;
import ch.zhaw.simulation.editor.flow.elements.GuiDataElement;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.global.GlobalView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.flow.InfiniteData;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;

public class AddConnectorUi {

	public enum ArcType {
		FLOW, PARAMETER, SELECATABLE
	};

	private DocumentView view;
	private SimulationControl control;
	private SelectionModel selectionModel;
	private ArcType addArcType = ArcType.PARAMETER;

	private GuiDataElement<?> start;

	private Point target;

	private MouseAdapter selectionMouseListener = new MouseAdapter() {
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		};

		@Override
		public void mouseMoved(MouseEvent e) {
			GuiDataElement<?> elem = view.getElementAt(e.getX(), e.getY());

			if (elem != null && isSelectElementAllowed(elem)) {
				selectionModel.setTmpSelection(elem);
			} else {
				selectionModel.clearTmpSelection();
			}

			if (start != null) {
				if (!e.getPoint().equals(target)) {
					target = e.getPoint();
					view.repaint();
				}
			}
		}

		private boolean isSelectElementAllowed(GuiDataElement<?> elem) {
			if (addArcType == ArcType.FLOW) {
				return elem instanceof ContainerView;
			} else {
				if (elem instanceof FlowConnectorParameter) {
					return targetNotAlreadyConnected(elem);
				}

				if (elem instanceof ContainerView) {
					return targetNotAlreadyConnected(elem);
				}

				if (elem instanceof ParameterView) {
					return targetNotAlreadyConnected(elem);
				}

				if (elem instanceof GlobalView && start != null) {
					return targetNotAlreadyConnected(elem);
				}

				return false;
			}
		}

		private boolean targetNotAlreadyConnected(GuiDataElement<?> elem) {
			if (start == null) {
				return true;
			}
			for (Connector<?> c : control.getModel().getConnectors()) {
				if (c.getSource() == start.getData() || c.getTarget() == start.getData()) {
					if (c.getSource() == elem.getData() || c.getTarget() == elem.getData()) {
						return false;
					}
				}
			}
			return true;
		}

		public void mouseReleased(MouseEvent e) {
			mousePressed(e);
		};

		@Override
		public void mousePressed(MouseEvent e) {
			view.requestFocus();

			SelectableElement[] selected = selectionModel.getSelected();

			if (addArcType == ArcType.FLOW) {
				if (start != null) {
					if (start instanceof InfiniteSymbol && selected.length == 1) {
						if (!checkType(selected[0])) {
							System.err.println("check type failed: 0");
							return; // Sollte nicht auftreten, error Handling
						}

						// Unendlich nach Container
						setEndElement((GuiDataElement<?>) selected[0]);
						return;
					}

					if (selected.length == 2) {
						if (!checkType(selected[1])) {
							System.err.println("check type failed: 1");
							return; // Sollte nicht auftreten, error Handling
						}
						setEndElement((GuiDataElement<?>) selected[1]);
					} else if (!(start instanceof InfiniteSymbol)) {
						addInfiniteEnd(e);
					} else {
						control.setStatusTextInfo("Auf Container klicken, ∞, oder mit <ESC> abbrechen");
					}
				} else if (selected.length == 1) {
					if (!checkType(selected[0])) {
						System.err.println("check type failed: 2");
						return; // Sollte nicht auftreten, error Handling
					}
					setStartElement((GuiDataElement<?>) selected[0]);
					selectionModel.acceptTmpSelection();
				} else {
					InfiniteData data = new InfiniteData(e.getX(), e.getY());
					data.calcCenter();
					InfiniteSymbol infinite = new InfiniteSymbol(data, control);
					infinite.setVisible(true);
					view.add(infinite);
					view.revalidate();

					control.setStatusTextInfo("Auf Container klicken, oder mit <ESC> abbrechen");
					start = infinite;

				}
			} else {
				if (start != null) {
					if (selected.length == 2) {
						if (!checkType(selected[1])) {
							System.err.println("check type failed: 3");
							return; // Sollte nicht auftreten, error Handling
						}
						setEndElement((GuiDataElement<?>) selected[1]);
					} else {
						control.setStatusTextInfo("Auf Ziel klicken oder mit <ESC> abbrechen");
					}
				} else if (selected.length == 1) {
					if (!checkType(selected[0])) {
						System.err.println("check type failed: 4");
						return; // Sollte nicht auftreten, error Handling
					}
					setStartElement((GuiDataElement<?>) selected[0]);
					selectionModel.acceptTmpSelection();
				}
			}
		};
	};

	public void startWith(GuiDataElement<?> data) {
		addArcType = ArcType.SELECATABLE;
		enableDrawModus();
		setStartElement(data);
		selectionModel.addSelected(data);
	}

	private boolean checkType(SelectableElement selected) {
		if (!(selected instanceof GuiDataElement<?>)) {
			Messagebox.showError(null, "Das gewählte Element ist vom falschen Typ.", "Type: " + selected.getClass().getName());
			return false;
		}
		return true;
	}

	private KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				cancelAddArrow();
			}
		}

	};

	public AddConnectorUi(DocumentView view, SimulationControl control) {
		this.view = view;
		this.control = control;
		this.selectionModel = control.getSelectionModel();
	}

	private void enableDrawModus() {
		if (view.isDrawModus()) {
			return;
		}

		for (Component c : view.getComponents()) {
			c.setVisible(false);
		}
		view.addMouseMotionListener(selectionMouseListener);
		view.addMouseListener(selectionMouseListener);

		view.addKeyListener(keyListener);
		view.requestFocus();
		control.fireDrawModusEnabled();
	}

	private void disableDrawModus() {
		if (!view.isDrawModus()) {
			return;
		}

		for (Component c : view.getComponents()) {
			if (c instanceof ViewComponent) {
				c.setVisible(true);
			}
		}
		view.removeMouseMotionListener(selectionMouseListener);
		view.removeMouseListener(selectionMouseListener);

		view.removeKeyListener(keyListener);
		control.fireDrawModusFinished();
	}

	public void addConnectArrow() {
		addArcType = ArcType.PARAMETER;
		control.setStatusTextInfo("Startelement wählen");
		start = null;
		target = null;
		enableDrawModus();
	}

	private void setStartElement(GuiDataElement<?> start) {
		this.start = start;
		control.setStatusTextInfo("Endelement wählen");
	}

	protected void setEndElement(GuiDataElement<?> end) {
		if (addArcType == ArcType.SELECATABLE) {
			if (start instanceof ContainerView || end instanceof ContainerView) {
				ConnectorSelectDialog dlg = new ConnectorSelectDialog(control);
				dlg.setVisible(true);
				if (dlg.isFlow()) {
					addArcType = ArcType.FLOW;
				}
			}
		}

		if (addArcType == ArcType.FLOW) {
			control.addConnector(new FlowConnector((SimulationObject) start.getData(), (SimulationObject) end.getData()));
		} else {
			control.addConnector(new ParameterConnector((NamedSimulationObject) start.getData(), (NamedSimulationObject) end.getData()));
		}
		control.clearStatus();
		selectionModel.clearSelection();
		disableDrawModus();
		start = null;
		target = null;
	}

	private void addInfiniteEnd(MouseEvent e) {
		InfiniteData data = new InfiniteData(e.getX(), e.getY());
		data.calcCenter();
		InfiniteSymbol infinite = new InfiniteSymbol(data, control);
		infinite.setVisible(true);
		view.add(infinite);
		view.revalidate();

		control.addConnector(new FlowConnector((SimulationObject) start.getData(), data));

		control.clearStatus();
		selectionModel.clearSelection();
		disableDrawModus();
		start = null;
		target = null;
	}

	public void addFlowArrow() {
		addArcType = ArcType.FLOW;
		control.setStatusTextInfo("Startcontainer wählen oder in Dokument klicken für ∞");
		start = null;
		target = null;
		enableDrawModus();
	}

	public void paint(Graphics2D g) {
		g.setColor(Color.RED);

		if (target != null) {
			int x1 = start.getX() + start.getWidth() / 2;
			int y1 = start.getY() + start.getHeight() / 2;

			g.drawLine(x1, y1, target.x, target.y);
			ParameterConnectorUi.drawArrow(g, x1, y1, target.x, target.y);
		}
	}

	public void cancelAddArrow() {
		if (start instanceof InfiniteSymbol) {
			view.remove(start);
			start.dispose();
		}

		start = null;
		target = null;
		disableDrawModus();
		control.setStatusText("Abgebroche");
		view.repaint();
	}
}
