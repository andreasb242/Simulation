package ch.zhaw.simulation.simplecharteditor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.VerticalLayout;
import org.jfree.chart.JFreeChart;

import butti.fontchooser.FontChooserPanel;
import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.diagram.persist.DiagramConfiguration;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.simplecharteditor.ChartConfigurationModel.ColorState;

public class SimpleChartEditor extends JDialog {
	private static final long serialVersionUID = 1L;
	private ChartConfigurationModel config;

	public SimpleChartEditor(Window parent, JFreeChart chart, DiagramConfiguration diagramConfig) {
		super(parent);

		this.config = new ChartConfigurationModel(chart, diagramConfig);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Diagramm Konfiguration");

		JTabbedPane tabs = new JTabbedPane();
		add(tabs);

		JPanel pStyle = new JPanel();
		JPanel pText = new JPanel();

		tabs.add("Aussehen", pStyle);
		tabs.add("Beschriftung", pText);

		pStyle.setLayout(new VerticalLayout());

		createStylePanel(pStyle);
		createFontPanel(pStyle);

		initTextPanel(pText);

		pack();
		setLocationRelativeTo(parent);
	}

	private void initTextPanel(JPanel pText) {
		pText.setLayout(new BorderLayout());
		pText.add(new JLabel(), BorderLayout.CENTER);

		JPanel panel = new JPanel();

		pText.add(panel, BorderLayout.NORTH);

		GridBagManager gbm = new GridBagManager(panel);

		gbm.setX(0).setY(0).setAnchor(GridBagConstraints.LINE_START).setFill(GridBagConstraints.VERTICAL).setWeightY(0)
				.setComp(new TitleLabel("Diagramm Titel"));

		final JTextField txtDiagramTitle = new JTextField(config.getTitleText());
		txtDiagramTitle.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				config.setTitleText(txtDiagramTitle.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				config.setTitleText(txtDiagramTitle.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				config.setTitleText(txtDiagramTitle.getText());
			}
		});

		gbm.setX(0).setY(1).setWeightY(0).setComp(txtDiagramTitle);

		gbm.setX(0).setY(10).setAnchor(GridBagConstraints.LINE_START).setFill(GridBagConstraints.VERTICAL).setWeightY(0).setComp(new TitleLabel("x-Achse"));

		final JTextField txtXAxis = new JTextField(config.getXAxisText());
		txtXAxis.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				config.setXAxisText(txtXAxis.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				config.setXAxisText(txtXAxis.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				config.setXAxisText(txtXAxis.getText());
			}
		});

		gbm.setX(0).setY(11).setWeightY(0).setComp(txtXAxis);

		gbm.setX(0).setY(20).setAnchor(GridBagConstraints.LINE_START).setFill(GridBagConstraints.VERTICAL).setWeightY(0).setComp(new TitleLabel("y-Achse"));

		final JTextField txtYAxis = new JTextField(config.getYAxisText());
		txtYAxis.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				config.setYAxisText(txtYAxis.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				config.setYAxisText(txtYAxis.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				config.setYAxisText(txtYAxis.getText());
			}
		});

		gbm.setX(0).setY(21).setWeightY(0).setComp(txtYAxis);

	}

	private void createFontPanel(JPanel panel) {
		final FontChooserPanel fPanel = new FontChooserPanel(this.config.getFont());
		fPanel.setPreviewVisible(false);

		fPanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SimpleChartEditor.this.config.setFont(fPanel.getSelectedFont());
			}
		});

		panel.add(fPanel);
	}

	private void createStylePanel(JPanel panel) {
		JPanel pStyle = new JPanel();
		pStyle.setBorder(new TitledBorder("Stil"));

		GridBagManager gbm = new GridBagManager(pStyle);

		ButtonGroup group = new ButtonGroup();

		JPanel pColor = new JPanel();
		pColor.setLayout(new BorderLayout());

		JToggleButton btColor = new JToggleButton(IconLoader.getIcon("simplediagramconf/color", 64));

		pColor.add(btColor, BorderLayout.CENTER);
		pColor.add(new JLabel("Farbig", JLabel.CENTER), BorderLayout.SOUTH);

		group.add(btColor);

		gbm.setX(0).setY(0).setComp(pColor);

		JPanel pSW = new JPanel();
		pSW.setLayout(new BorderLayout());

		JToggleButton btBW = new JToggleButton(IconLoader.getIcon("simplediagramconf/sw", 64));
		group.add(btBW);

		pSW.add(btBW, BorderLayout.CENTER);
		pSW.add(new JLabel("Schwarz / Weiss", JLabel.CENTER), BorderLayout.SOUTH);

		gbm.setX(1).setY(0).setComp(pSW);

		panel.add(pStyle);

		ColorState state = this.config.getColorState();
		if (state == ColorState.NOT_SET) {
			group.clearSelection();
		} else if (state == ColorState.COLORED) {
			btColor.setSelected(true);
		} else if (state == ColorState.BLACK_WHITE) {
			btBW.setSelected(true);
		}

		btColor.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					config.setColorState(ColorState.COLORED);
				}

			}
		});

		btBW.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					config.setColorState(ColorState.BLACK_WHITE);
				}

			}
		});
	}
}
