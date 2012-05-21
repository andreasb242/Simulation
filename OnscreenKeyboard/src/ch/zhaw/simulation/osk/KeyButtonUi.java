package ch.zhaw.simulation.osk;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

import butti.javalibs.config.Config;
import butti.javalibs.util.DrawHelper;

public class KeyButtonUi extends BasicButtonUI implements MouseListener, KeyListener {
	private final static KeyButtonUi uiInstance = new KeyButtonUi();

	private Color backgroundNormal;
	private Color backgroundActive;
	private Color backgroundPressed;

	private Color foregroundNormal;
	private Color foregroundActive;
	private Color foregroundPressed;

	private Border borderNormal;
	private Border borderActive;
	private Border borderPressed;

	public KeyButtonUi() {
		backgroundNormal = Config.get("keyboard.color.button.normal.background", new Color(0x484848));
		backgroundActive = Config.get("keyboard.color.button.active.background", new Color(0x737373));
		backgroundPressed = Config.get("keyboard.color.button.pressed.background", Color.WHITE);

		foregroundNormal = Config.get("keyboard.color.button.normal.foreground", Color.WHITE);
		foregroundActive = Config.get("keyboard.color.button.active.foreground", Color.WHITE);
		foregroundPressed = Config.get("keyboard.color.button.pressed.foreground", Color.BLACK);

		borderNormal = createBorder(Config.get("keyboard.color.button.normal.border", Color.WHITE));
		borderActive = createBorder(Config.get("keyboard.color.button.active.border", Color.WHITE));
		borderPressed = createBorder(Config.get("keyboard.color.button.pressed.border", Color.RED));
	}

	private Border createBorder(Color color) {
		int gap = Config.get("keyboard.border.gap", 10);
		Border inside = new EmptyBorder(gap, gap, gap, gap);
		return new CompoundBorder(new LineBorder(color), inside);
	}

	public static ComponentUI createUI(JComponent c) {
		return uiInstance;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		c.addMouseListener(this);
		c.addKeyListener(this);
		c.setBackground(backgroundNormal);
		c.setForeground(foregroundNormal);
		c.setBorder(borderNormal);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeMouseListener(this);
		c.removeKeyListener(this);
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		Graphics2D g = DrawHelper.antialisingOn(g1);

		AbstractButton b = (AbstractButton) c;
		Dimension d = b.getSize();

		g.setFont(c.getFont());
		FontMetrics fm = g.getFontMetrics();

		g.setColor(b.getForeground());
		String caption = b.getText();
		int x = (d.width - fm.stringWidth(caption)) / 2;
		int y = (d.height + fm.getAscent()) / 2;
		g.drawString(caption, x, y);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		if (borderNormal != null) {
			Insets ins = borderNormal.getBorderInsets(c);
			d.setSize(d.width + ins.left + ins.right, d.height + ins.top + ins.bottom);
		}
		return d;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setBorder(borderPressed);
		c.setBackground(backgroundPressed);
		c.setForeground(foregroundPressed);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setBorder(borderActive);
		c.setBackground(backgroundNormal);
		c.setForeground(foregroundNormal);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setForeground(foregroundActive);
		c.setBackground(backgroundActive);
		c.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setForeground(foregroundNormal);
		c.setBackground(backgroundNormal);
		c.setBorder(borderNormal);
		c.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
			JComponent c = (JComponent) e.getComponent();
			c.setBorder(borderPressed);
			c.setBackground(backgroundPressed);
			c.setForeground(foregroundPressed);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
			JComponent c = (JComponent) e.getComponent();
			c.setBorder(borderActive);
			c.setBackground(backgroundNormal);
			c.setForeground(foregroundNormal);
		}
	}

}
