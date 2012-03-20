package ch.zhaw.simulation.sysintegration.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarButton;

public class ImageButton extends JComponent implements ToolbarButton {
	private static final long serialVersionUID = 1L;

	private JLabel lbIcon;

	private Icon iconDown;
	private Icon icon;

	public ImageButton(final ToolbarAction action) {
		setLayout(null);

		this.icon = (Icon) action.getToolbarIcon();
		this.iconDown = darkerIcon(icon);
		lbIcon = new JLabel(icon);
		add(lbIcon);

		lbIcon.setToolTipText(action.getName());

		lbIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!isEnabled()) {
					return;
				}
				lbIcon.setIcon(iconDown);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!isEnabled()) {
					return;
				}
				action.run(new ActionEvent(ImageButton.this, 0, "clicked"));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!isEnabled()) {
					return;
				}
				lbIcon.setIcon(icon);
			}
		});

		Dimension size = lbIcon.getPreferredSize();

		lbIcon.setBounds(0, 0, size.width, size.height);
		setPreferredSize(size);
	}

	@Override
	public void setEnabled(boolean enabled) {
		lbIcon.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public void setIcon(Icon icon) {
		this.icon = icon;
		this.lbIcon.setIcon(icon);
		this.iconDown = darkerIcon(icon);
	}

	private Icon darkerIcon(Icon icon) {
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(lbIcon, img.getGraphics(), 0, 0);

		BufferedImage buff = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

		Kernel kernel = new Kernel(1, 1, new float[] { 0.7f });

		ConvolveOp op = new ConvolveOp(kernel);
		op.filter(img, buff);

		return new ImageIcon(buff);
	}

	@Override
	public void setText(String text) {
		lbIcon.setToolTipText(text);
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

}
