package ch.zhaw.simulation.xyviewer;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.action.TargetableAction;

import butti.javalibs.config.Settings;

import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;

public class ResultViewerDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private PositionModel model;

	private XYViewerControlPanel positionControl;

	private XYViewer view;

	private Sysintegration sysintegration;

	private Toolbar toolbar;

	private TargetableAction logButton;

	public ResultViewerDialog(Window parent, Settings settings, Sysintegration sysintegration, XYResultList resultList, Vector<XYDensityRaw> rawList) {
		super(parent);

		this.model = new PositionModel(resultList.getStepCount());
		this.sysintegration = sysintegration;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		this.view = new XYViewer(resultList, rawList, this.model);

		this.positionControl = new XYViewerControlPanel(this.model, settings);

		XYViewerSidebar sidebar = new XYViewerSidebar(this, rawList);

		add(new JScrollPane(view), BorderLayout.CENTER);
		add(positionControl, BorderLayout.SOUTH);
		add(sidebar, BorderLayout.WEST);

		initToolbar();

		model.firePosition(0);

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
				System.out.println("bild");
			}
		});

		this.toolbar.add(new ToolbarAction("Speichern als Film", "diagram/video") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				System.out.println("Film");
			}
		});

		this.toolbar.addSeparator();

		this.toolbar.add(new ToolbarAction("Dichte als XY Diagramm anzeigen, horizontal", "diagram/diagram_horizontal") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				System.out.println("horizontal");
			}
		});
		this.toolbar.add(new ToolbarAction("Dichte als XY Diagramm anzeigen, vertikal", "diagram/diagram_vertical") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				System.out.println("vertikal");
			}
		});

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
			System.out.println(raw.isLogView());
			this.logButton.setSelected(raw.isLogView());
		}
	}
}
