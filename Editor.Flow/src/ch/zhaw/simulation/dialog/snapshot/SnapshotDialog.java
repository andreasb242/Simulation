package ch.zhaw.simulation.dialog.snapshot;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.bookmarks.ComboSeparatorsRenderer;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.GridBagManager;

public class SnapshotDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private JTextField txtName = new JTextField(20);
	private JComboBox cbSavePath;

	private JCheckBox cbSelection = new JCheckBox("Nur Selektion");

	private JRadioButton rPng = new JRadioButton("PNG");
	private JRadioButton rEps = new JRadioButton("EPS");
	private ButtonGroup format = new ButtonGroup();

	public SnapshotDialog(JFrame parent, Sysintegration sys, JComponent c, Rectangle size) {
		super(parent);
		setTitle("Als Bild speichern...");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		gbm = new GridBagManager(this);

		cbSavePath = new JComboBox(sys.getBookmarks());

		ImageIcon img = IconSVG.getIcon("photos", 64);
		gbm.setX(0).setY(0).setHeight(5).setWeightX(0).setWeightY(0).setComp(new JLabel(img));

		gbm.setX(1).setY(0).setWeightY(0).setComp(new JLabel("Name"));
		gbm.setX(2).setY(0).setWeightY(0).setComp(txtName);

		cbSavePath.setRenderer(new ComboSeparatorsRenderer(cbSavePath.getRenderer()) {

			@Override
			protected boolean addSeparatorAfter(JList list, Object value, int index) {
				return index % 2 == 0;
			}
		});

		gbm.setX(1).setY(1).setWeightY(0).setComp(new JLabel("Im Ordner speichern"));
		gbm.setX(2).setY(1).setWeightY(0).setComp(cbSavePath);

		gbm.setX(2).setY(3).setComp(cbSelection);

		// TODO: implementieren

		JPanel pFormat = new JPanel();
		pFormat.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pFormat.add(rPng);
		pFormat.add(rEps);
		format.add(rPng);
		format.add(rEps);

		rPng.setSelected(true);

		gbm.setX(1).setY(2).setWeightY(0).setComp(new JLabel("Format"));
		gbm.setX(2).setY(2).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_START).setComp(pFormat);

		JButton btSave = ButtonFactory.createButton("Speichern", true);
		JButton btCopy = ButtonFactory.createButton("In die Zwischenablage kopieren", false);
		JButton btCancel = ButtonFactory.createButton("Abbrechen", false);

		btCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel pButton = new JPanel();
		pButton.setLayout(new FlowLayout());
		pButton.add(btCopy);
		pButton.add(btCancel);
		pButton.add(btSave);

		gbm.setX(0).setWidth(3).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END).setY(6).setWeightY(0).setComp(pButton);

		setModal(true);
		pack();
		setLocationRelativeTo(parent);
	}
}
