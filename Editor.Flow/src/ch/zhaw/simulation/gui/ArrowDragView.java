package ch.zhaw.simulation.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import ch.zhaw.simulation.editor.elements.GuiDataElement;


public class ArrowDragView extends JComponent {
	private static final long serialVersionUID = 1L;
	private GuiDataElement<?> data;

	public ArrowDragView(final AddConnectorUi addConnectorUi, final FlowEditorView view) {
		setSize(14, 20);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (data != null) {
					addConnectorUi.startWith(data);
					setVisible(false);
				}
			}

		});
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		g.setColor(Color.BLUE);

		Point end = new Point(7, 2);

		g.drawLine(end.x, getHeight(), end.x, end.y);
		g.drawLine(1, 10, end.x, end.y);
		g.drawLine(13, 10, end.x, end.y);
	}

	public void setElement(GuiDataElement<?> e) {
		this.data = e;
	}
}
