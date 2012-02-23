package ch.zhaw.simulation.gui.configuration.codeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.HorizontalLayout;

import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;

public class ListRenderer implements ListCellRenderer {

	private JLabel lbTitle = new JLabel(" ") {
		private static final long serialVersionUID = 1L;
		private Color color1 = new Color(0x5591ec);
		private Color color2 = new Color(0x202aef);

		@Override
		public void paint(Graphics g) {
			((Graphics2D) g).setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
			g.fillRect(0, 0, getWidth(), getHeight());
			super.paint(g);
		}
	};

	private JPanel pConstant = new JPanel();

	private JLabel lbConstHeader = new JLabel();
	private JLabel lbConstValue = new JLabel();

	private JLabel lbFunction = new JLabel();

	private JLabel lbParam = new JLabel();

	public ListRenderer() {
		lbTitle.setOpaque(false);
		lbTitle.setForeground(Color.WHITE);

		pConstant.setOpaque(true);
		pConstant.setBackground(Color.WHITE);

		pConstant.setLayout(new HorizontalLayout(4));
		pConstant.add(lbConstHeader, BorderLayout.WEST);
		pConstant.add(lbConstValue, BorderLayout.CENTER);

		lbConstHeader.setFont(lbConstHeader.getFont().deriveFont(Font.BOLD));
		lbConstHeader.setIcon(IconSVG.getIcon("constants"));

		lbParam.setIcon(IconSVG.getIcon("param"));
		lbParam.setOpaque(true);
		lbFunction.setIcon(IconSVG.getIcon("function"));
		lbFunction.setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		if (value instanceof Constant) {
			Constant constant = (Constant) value;
			lbConstHeader.setText(constant.name);
			lbConstValue.setText(constant.value.toString());

			if (isSelected) {
				pConstant.setBackground(new Color(0xfffbbd));
			} else {
				pConstant.setBackground(Color.WHITE);
			}

			return pConstant;
		} else if (value instanceof Function) {
			lbFunction.setText(((Function) value).getName());

			if (isSelected) {
				lbFunction.setBackground(new Color(0xfffbbd));
			} else {
				lbFunction.setBackground(Color.WHITE);
			}

			return lbFunction;
		} else if (value instanceof NamedSimulationObject) {
			lbParam.setText(((NamedSimulationObject) value).getName());

			if (isSelected) {
				lbParam.setBackground(new Color(0xfffbbd));
			} else {
				lbParam.setBackground(Color.WHITE);
			}

			return lbParam;
		}

		lbTitle.setFont(list.getFont().deriveFont(Font.BOLD));
		lbTitle.setText(value.toString());
		return lbTitle;
	}

}
