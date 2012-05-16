package ch.zhaw.simulation.simplecharteditor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;

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
import ch.zhaw.simulation.icon.IconLoader;

public class SimpleChartEditor extends JDialog {
	private static final long serialVersionUID = 1L;
	private ChartConfigurationModel config;

	public SimpleChartEditor(Window parent, JFreeChart chart) {
		super(parent);

		this.config = new ChartConfigurationModel(chart);

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
		FontChooserPanel fPanel = new FontChooserPanel(null);
		fPanel.setPreviewVisible(false);
		panel.add(fPanel);
	}

	private void createStylePanel(JPanel panel) {
		JPanel pStyle = new JPanel();
		pStyle.setBorder(new TitledBorder("Stil"));

		GridBagManager gbm = new GridBagManager(pStyle);

		JPanel pColor = new JPanel();
		pColor.setLayout(new BorderLayout());
		pColor.add(new JToggleButton(IconLoader.getIcon("simplediagramconf/color", 64)), BorderLayout.CENTER);
		pColor.add(new JLabel("Farbig", JLabel.CENTER), BorderLayout.SOUTH);

		gbm.setX(0).setY(0).setComp(pColor);

		JPanel pSW = new JPanel();
		pSW.setLayout(new BorderLayout());
		pSW.add(new JToggleButton(IconLoader.getIcon("simplediagramconf/sw", 64)), BorderLayout.CENTER);
		pSW.add(new JLabel("Schwarz / Weiss", JLabel.CENTER), BorderLayout.SOUTH);

		gbm.setX(1).setY(0).setComp(pSW);

		panel.add(pStyle);

	}
}
