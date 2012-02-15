package ch.zhaw.simulation.editor.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.htmleditor.HTMLEditor;
import ch.zhaw.simulation.model.TextData;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.undo.action.MoveUndoAction;
import ch.zhaw.simulation.undo.action.ResizeUndoAction;

public class TextView extends GuiDataElement<TextData> {
	private static final long serialVersionUID = 1L;

	private GuiConfig config;

	private boolean resizing = false;

	private HTMLEditor editor;

	private BufferedImage textImage;

	private JLabel lbConents = new JLabel();

	public TextView(SimulationControl control, TextData data) {
		super(data, control);
		config = control.getConfig();

		editor = new HTMLEditor(control.getParent(), control.getSysintegration(), control.getSettings());
		editor.setContents(data.getText());

		add(lbConents);
		setContents(data.getText());
	}

	@Override
	protected void doubleClicked(MouseEvent e) {
		showTextEditor();
	}

	protected void dragged(Point p) {
		SelectionModel selectionModel = getSelectionModel();

		// Wenn noch nicht gewählt beim verschieben wählen
		if (!selectionModel.isSelected(this)) {
			selectionModel.setSelected(this);
		}

		int x = getWidth() - 9;
		int y = getHeight() - 9;

		int dX = (int) p.getX() - lastX;
		int dY = (int) p.getY() - lastY;

		if (resizing || lastX >= x && lastY >= y && lastX <= x + 8 && lastY <= y + 8) {
			resizing = true;
			lastX = (int) p.getX();
			lastY = (int) p.getY();

			getControl().getUndoManager().addEdit(new ResizeUndoAction(this, dX, dY));
			getControl().getModel().setChanged();
			paintText();
		} else {
			selectionModel.move(dX, dY);
			getControl().getUndoManager().addEdit(new MoveUndoAction(selectionModel.getSelected(), dX, dY));
		}
	}

	@Override
	protected void mouseReleased(Point p) {
		resizing = false;
	}

	public void changeSize(int dX, int dY) {
		TextData d = getData();
		d.setWidth(d.getWidth() + dX);
		d.setHeight(d.getHeight() + dY);

		setPreferredSize(new Dimension(d.getWidth(), d.getHeight()));
		setSize(getPreferredSize());
	}

	public void showTextEditor() {
		editor.setVisible(true);

		if (editor.isSaved()) {
			getData().setText(editor.getContents());
			setContents(getData().getText());
			getControl().getModel().fireObjectChanged(getData(), false);

			repaint();
		}
	}

	public void paintText() {
		int width = getData().getWidth() - 6;
		int height = getData().getHeight() - 6;
		textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics g = textImage.getGraphics();

		lbConents.setBounds(0, 0, width, height);

		lbConents.paint(g);
	}

	public void setContents(String html) {
		try {
			lbConents.setText(html);
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
		
		paintText();
		repaint();
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = DrawHelper.antialisingOff(g1);

		g.setPaint(config.getTextBackgroundPaint(getWidth(), getHeight(), isSelected()));
		g.fillRect(0, 0, getWidth() - 5, getHeight() - 5);

		g.setColor(config.getTextLineColor(isSelected()));
		g.drawRect(0, 0, getWidth() - 5, getHeight() - 5);

		g.drawImage(textImage, 1, 1, this);

		// Ecke
		int x = getWidth() - 9;
		int y = getHeight() - 9;
		g.drawRect(x, y, 8, 8);

		g.setColor(Color.WHITE);
		g.fillRect(x + 1, y + 1, 7, 7);
	}
}
