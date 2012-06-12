package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.action.TargetableAction;
import org.jdesktop.swingx.util.OS;

import butti.javalibs.clipboard.ImageSelection;
import butti.javalibs.config.Settings;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.diagram.DiagramFrame;
import ch.zhaw.simulation.diagram.SerieCbRenderer;
import ch.zhaw.simulation.diagram.persist.DiagramConfiguration;
import ch.zhaw.simulation.dialog.snapshot.ImageExportable;
import ch.zhaw.simulation.dialog.snapshot.MovieExportDialog;
import ch.zhaw.simulation.dialog.snapshot.MovieExportable;
import ch.zhaw.simulation.dialog.snapshot.SnapshotDialog;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.vector.VectorExport;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private PositionModel model;

	private XYViewerControlPanel positionControl;

	private XYViewer view;

	private Sysintegration sysintegration;

	private Toolbar toolbar;

	private TargetableAction logButton;

	private JFrame parent;

	private Settings settings;

	private String name;

	private static final int MAX_LINE_COUNT = 20;
	private JSpinner spinnerLineCount = new JSpinner(new SpinnerNumberModel(5, 1, MAX_LINE_COUNT, 1));

	private XYResultList resultList;

	private TargetableAction densityColor;

	private TargetableAction densityArrow;

	public ResultViewerDialog(String name, JFrame parent, Settings settings, Sysintegration sysintegration, XYResultList resultList,
			Vector<XYDensityRaw> rawList) {
		super(parent);

		if (name != null) {
			setTitle(name + " - (AB)² Simulation");
		} else {
			setTitle("(AB)² Simulation");
		}

		// Mac OS X
		getRootPane().putClientProperty("apple.awt.brushMetalLook", true);

		this.parent = parent;
		this.settings = settings;
		this.sysintegration = sysintegration;
		this.name = name;
		this.resultList = resultList;

		this.model = new PositionModel(resultList.getStepCount());
		this.sysintegration = sysintegration;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		this.view = new XYViewer(resultList, rawList, this.model);

		this.positionControl = new XYViewerControlPanel(this.model, settings);

		XYViewerSidebar sidebar = new XYViewerSidebar(this, rawList);

		JScrollPane scrol = new JScrollPane(view);

		JPanel pConentents = new JPanel();
		pConentents.setLayout(new BorderLayout());

		pConentents.add(sidebar, BorderLayout.WEST);
		pConentents.add(scrol, BorderLayout.CENTER);

		add(pConentents, BorderLayout.CENTER);
		add(positionControl, BorderLayout.SOUTH);

		if (OS.isMacOSX()) {
			scrol.setBorder(null);
			pConentents.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x515151)));
		}

		initToolbar();

		if (rawList.size() > 0) {
			setSelectedDensity(rawList.get(0));
		}

		model.firePosition(0);

		int count = (int) settings.getSetting("xyviewer.diagramlinecount", 5);
		if (0 < count && count >= MAX_LINE_COUNT) {
			spinnerLineCount.setValue(count);
		}

		pack();
		setLocationRelativeTo(null);
	}

	private void initToolbar() {
		this.toolbar = this.sysintegration.createToolbar(32);

		add(this.toolbar.getComponent(), BorderLayout.NORTH);

		this.logButton = new TargetableAction("Dichte logarithmisch darstellen", "diagram/log-x", IconLoader.getIcon("diagram/log-x",
				this.toolbar.getDefaultIconSize())) {
			private static final long serialVersionUID = 1L;
			{
				setStateAction(true);
			}

			@Override
			public void itemStateChanged(ItemEvent evt) {
				boolean log = evt.getStateChange() == ItemEvent.SELECTED;

				XYDensityRaw density = ResultViewerDialog.this.view.getSelectedDensity();
				density.setLogView(log);
				ResultViewerDialog.this.view.repaintDensity();
			}

		};
		this.toolbar.addToogleAction(logButton);

		this.toolbar.addSeparator();

		this.toolbar.add(new ToolbarAction("Speichern als Bild", "diagram/screenshot") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				exportImage();
			}
		});

		this.toolbar.add(new ToolbarAction("Speichern als Film", "diagram/video") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				exportMovie();
			}
		});

		this.toolbar.addSeparator();

		toolbar.add(new ToolbarAction("In die Zwischenablage kopieren (Rastergrafik)", "diagram/edit-copy") {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard();
			}
		});
		this.toolbar.addSeparator();

		this.toolbar.add(new ToolbarAction("Dichte als XY Diagramm anzeigen, horizontal", "diagram/diagram_horizontal") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				showDiagramHorizontal();
			}
		});
		this.toolbar.add(new ToolbarAction("Dichte als XY Diagramm anzeigen, vertikal", "diagram/diagram_vertical") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				showDiagramVertical();
			}
		});

		this.toolbar.add(spinnerLineCount);
		this.toolbar.add(new JLabel("Linien"));

		this.toolbar.addSeparator();

		this.densityColor = new TargetableAction("Dichte farbig darstellen", "diagram/color", IconLoader.getIcon("diagram/color",
				this.toolbar.getDefaultIconSize())) {
			private static final long serialVersionUID = 1L;
			{
				setStateAction(true);
			}

			@Override
			public void itemStateChanged(ItemEvent evt) {
				boolean densityColor = evt.getStateChange() == ItemEvent.SELECTED;

				ResultViewerDialog.this.view.setShowDensityColor(densityColor);
				ResultViewerDialog.this.view.repaintDensity();

				settings.setSetting("resultviewer.density.showcolor", densityColor);

			}

		};
		this.toolbar.addToogleAction(densityColor);

		this.densityArrow = new TargetableAction("Dichte Gradientenpfeile", "diagram/arrow", IconLoader.getIcon("diagram/arrow",
				this.toolbar.getDefaultIconSize())) {
			private static final long serialVersionUID = 1L;
			{
				setStateAction(true);
			}

			@Override
			public void itemStateChanged(ItemEvent evt) {
				boolean densityArrow = evt.getStateChange() == ItemEvent.SELECTED;

				ResultViewerDialog.this.view.setShowDensityArrow(densityArrow);
				ResultViewerDialog.this.view.repaintDensity();

				settings.setSetting("resultviewer.density.showarrow", densityArrow);
			}

		};
		this.toolbar.addToogleAction(densityArrow);

		boolean showColor = settings.isSetting("resultviewer.density.showcolor", true);
		this.densityColor.setSelected(showColor);
		ResultViewerDialog.this.view.setShowDensityColor(showColor);

		boolean showArrow = settings.isSetting("resultviewer.density.showarrow", true);
		this.densityArrow.setSelected(showArrow);
		ResultViewerDialog.this.view.setShowDensityArrow(showArrow);
	}

	protected void exportMovie() {
		MovieExportable exportable = new MovieExportable() {

			private int fps;

			private Exception lastException;

			{
				this.fps = positionControl.getFps();
			}

			@Override
			public int getCount() {
				return model.getStepCount();
			}

			@Override
			public void export(int pos, final File file) throws Exception {
				model.firePosition(pos);

				lastException = null;
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						try {
							BufferedImage img = takeScreenshot();
							ImageIO.write(img, "PNG", file);
						} catch (Exception e) {
							lastException = e;
						}
					}
				});

				if (lastException != null) {
					throw lastException;
				}
			}

			@Override
			public int getFps() {
				return this.fps;
			}

		};

		MovieExportDialog dlg = new MovieExportDialog(this.parent, this.settings, exportable, this.sysintegration, name);
		dlg.setVisible(true);
	}

	private void saveLastSliderValue() {
		settings.setSetting("xyviewer.diagramlinecount", (Integer) spinnerLineCount.getValue());
	}

	protected void showDiagramVertical() {
		saveLastSliderValue();

		XYDensityRaw density = this.view.getSelectedDensity();

		if (density == null) {
			Messagebox.showError(this, "Keine Daten", "Keine Dichte gewählt. Bitte wählen Sie zuerst eine Dichte.");
			return;
		}

		int width = resultList.getModelSize().width;
		int height = resultList.getModelSize().height;

		SimulationCollection collection = new SimulationCollection(0, height);

		int count = (Integer) spinnerLineCount.getValue();

		int dt = width / count;
		int x = dt / 2;
		for (int i = 0; i < count; i++) {

			SimulationSerie s = new SimulationSerie("x = " + x, null);

			for (int y = 0; y < height; y++) {
				s.add(y, density.getMatrixValue(x, y));
			}

			collection.addSerie(s);

			x += dt;
			if (x >= width) {
				x = width - 1;
			}
		}

		String name;
		if (this.name != null) {
			name = this.name + " - vertikal";
		} else {
			name = "vertikal";
		}

		DiagramConfiguration config = new DiagramConfiguration();
		DiagramFrame diagram = new DiagramFrame(collection, settings, config, name, sysintegration);
		showConfigureDiagram(diagram, "Y");
	}

	protected void showDiagramHorizontal() {
		saveLastSliderValue();

		XYDensityRaw density = this.view.getSelectedDensity();

		if (density == null) {
			Messagebox.showError(this, "Keine Daten", "Keine Dichte gewählt. Bitte wählen Sie zuerst eine Dichte.");
			return;
		}

		int width = resultList.getModelSize().width;
		int height = resultList.getModelSize().height;

		SimulationCollection collection = new SimulationCollection(0, width);

		int count = (Integer) spinnerLineCount.getValue();

		int dt = height / count;
		int y = dt / 2;
		for (int i = 0; i < count; i++) {

			SimulationSerie s = new SimulationSerie("y = " + y, null);

			for (int x = 0; x < width; x++) {
				s.add(x, density.getMatrixValue(x, y));
			}

			collection.addSerie(s);

			y += dt;
			if (y >= height) {
				y = height - 1;
			}
		}

		String name;
		if (this.name != null) {
			name = this.name + " - horizontal";
		} else {
			name = "horizontal";
		}

		DiagramConfiguration config = new DiagramConfiguration();
		DiagramFrame diagram = new DiagramFrame(collection, settings, config, name, sysintegration);

		showConfigureDiagram(diagram, "X");
	}

	private void showConfigureDiagram(DiagramFrame diagram, String direction) {
		SerieCbRenderer renderer = diagram.getSeriesCbRenderer();
		renderer.setTimeText(direction + "-Richtung");
		renderer.setTimeIcon(null);

		diagram.setVisible(true);
	}

	private BufferedImage takeScreenshot() {
		int width = view.getWidth();
		int height = view.getHeight();

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = img.createGraphics();
		view.paint(g);
		g.dispose();

		return img;
	}

	protected void copyToClipboard() {
		ImageSelection.copyImageToClipboard(takeScreenshot());
	}

	protected void exportImage() {
		ImageExportable export = new ImageExportable() {

			@Override
			public boolean supportsSelection() {
				return false;
			}

			@Override
			public void exportToClipboard(boolean onlySelection) {
				copyToClipboard();
			}

			@Override
			public void export(boolean onlySelection, String format, File file) throws IOException {
				int width = view.getWidth();
				int height = view.getHeight();

				if ("PNG".equals(format)) {
					BufferedImage img = takeScreenshot();
					ImageIO.write(img, "PNG", file);
				} else {
					VectorExport ex = new VectorExport(new FileOutputStream(file), new Dimension(width, height), format);

					Graphics2D g = ex.getGraphics();
					view.paint(g);
					ex.close();
				}
			}
		};

		SnapshotDialog dlg = new SnapshotDialog(this.parent, this.settings, this.sysintegration, export, name);
		dlg.setVisible(true);
	}

	@Override
	public void dispose() {
		this.positionControl.dispose();
		this.view.dispose();

		super.dispose();
	}

	public void setSelectedDensity(XYDensityRaw raw) {
		this.view.setSelectedDensity(raw);
		if (raw != null) {
			this.logButton.setSelected(raw.isLogView());
		}
	}
}
