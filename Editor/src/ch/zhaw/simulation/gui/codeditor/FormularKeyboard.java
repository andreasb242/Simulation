package ch.zhaw.simulation.gui.codeditor;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import butti.javalibs.gui.GridBagManager;

public class FormularKeyboard extends JPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private FormulaTextPane text;

	public FormularKeyboard(FormulaTextPane text) {
		gbm = new GridBagManager(this);
		this.text = text;
		initNumPad();
		initConstants();
		initOperations();
	}

	private void initConstants() {
		JPanel p = new JPanel();
		p.setBorder(new TitledBorder("Konstanten"));
		GridBagManager gbm = new GridBagManager(p);

		gbm.setInsets(new Insets(0, 0, 0, 0));

		Key ki = new Key("i");
		ki.setFont(ki.getFont().deriveFont(Font.ITALIC));
		gbm.setX(0).setY(0).setComp(ki);

		Key ke = new Key("e");
		ke.setFont(ke.getFont().deriveFont(Font.ITALIC));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(0).setComp(ke);

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(0).setComp(new Key("π", "pi"));

		this.gbm.setX(0).setY(1).setWeightY(0).setFill(GridBagConstraints.HORIZONTAL).setComp(p);
	}

	private void initOperations() {
		JPanel p = new JPanel();
		p.setBorder(new TitledBorder("Operationen"));

		GridBagManager gbm = new GridBagManager(p);

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(0).setComp(new Key("+"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(0).setComp(new Key("-"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(0).setComp(new Key("*"));

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(1).setComp(new Key("/"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(1).setComp(new Key("^"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(1).setComp(new Key("log", "log()", -1));

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(2).setComp(new Key("sin", "sin()", -1));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(2).setComp(new Key("cos", "cos()", -1));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(2).setComp(new Key("tan", "tan()", -1));

		this.gbm.setX(0).setY(2).setWeightY(0).setFill(GridBagConstraints.HORIZONTAL).setComp(p);
	}

	private void initNumPad() {
		JPanel p = new JPanel();
		p.setBorder(new TitledBorder("Zahlen"));

		GridBagManager gbm = new GridBagManager(p);

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(0).setComp(new Key("7"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(0).setComp(new Key("8"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(0).setComp(new Key("9"));

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(1).setComp(new Key("4"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(1).setComp(new Key("5"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(1).setComp(new Key("6"));

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(2).setComp(new Key("1"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(2).setComp(new Key("2"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(2).setComp(new Key("3"));

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(3).setWidth(2).setComp(new Key("0"));
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(2).setY(3).setComp(new Key("."));

		this.gbm.setX(0).setY(0).setWeightY(0).setFill(GridBagConstraints.HORIZONTAL).setComp(p);
	}

	class Key extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private String representation;
		private int relpos;

		public Key(String text) {
			this(text, text);
		}

		public Key(String text, String representation, int relpos) {
			this(text, representation);
			this.relpos = relpos;
		}

		public Key(String text, String representation) {
			super(text);
			this.representation = representation;

			// Mac OS X
			putClientProperty("JButton.buttonType", "textured");

			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			text.inserEditor(representation, relpos);
		}
	}
}
