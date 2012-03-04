package ch.zhaw.simulation.sim.intern.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.plugin.PluginDataProvider;
import ch.zhaw.simulation.sim.intern.data.SimulationCollection;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarButton;

public class TableDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private SimulationTableModel model;

	private TableRenderer renderer = new TableRenderer();

	private JTable table;

	private Toolbar toolbar;

	private JSpinner precision = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));

	private SimFileFilter CSVSave = new SimFileFilter() {
		@Override
		public boolean accept(File f) {
			return (f.isDirectory() || f.getName().endsWith(".csv"));
		}

		@Override
		public String getDescription() {
			return "Komprimierte Simulation";
		}

		@Override
		public String getExtension() {
			return ".csv";
		}
	};

	private ToolbarButton tbCopy;

	private ListSelectionListener listener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int[] rowsselected = table.getSelectedRows();
			int[] colsselected = table.getSelectedColumns();

			if (rowsselected.length < 1 || colsselected.length < 1) {
				tbCopy.setEnabled(false);
			} else {
				tbCopy.setEnabled(true);
			}
		}
	};

	private Settings settings;

	public TableDialog(PluginDataProvider provider, final Settings settings, SimulationCollection series) {
		super(provider.getParent());

		this.settings = settings;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setTitle("Simulationsdaten");

		model = new SimulationTableModel(series);
		table = new JTable(model);
		table.setColumnSelectionAllowed(true);
		table.getSelectionModel().addListSelectionListener(listener);
		table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		updateRenderer();

		setLayout(new BorderLayout());

		add(new JScrollPane(table), BorderLayout.CENTER);

		initToolbar();

		precision.setValue((int) settings.getSetting("table.precision", 2.0));

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				settings.setSetting("table.precision", (Integer) precision.getValue());
			}

		});
		
		pack();
		setLocationRelativeTo(provider.getParent());
	}

	private void initToolbar() {
		toolbar = SysintegrationFactory.createSysintegration().createToolbar();
		tbCopy = toolbar.add(new ToolbarAction("Kopieren", IconSVG.getIconShadow("editcopy", 24)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO !!!!
				new Exception().printStackTrace();
			}
		});

		tbCopy.setEnabled(false);

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Speichern", "save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		toolbar.addSeparator();

		toolbar.add(new JLabel("Dezimalstellen:"));
		toolbar.add(precision);

		precision.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int v = (Integer) precision.getValue();
				renderer.setPrecision(v);
				table.repaint();
			}
		});

		add(toolbar.getComponent(), BorderLayout.NORTH);
	}

	protected void save() {
		String lastSavePath = settings.getSetting("table.lastSavePath");
		// TODO !!! SysintegrationFactory.createSysintegration()
		File file = SysintegrationFactory.createSysintegration().showSaveDialog(this, CSVSave, lastSavePath);

		if (file == null) {
			return;
		}

		settings.setSetting("table.lastSavePath", file.getParent());

		try {
			save(file);
		} catch (IOException e) {
			Errorhandler.showError(e, "Speichern ist fehlgeschlagen!");
		}
	}

	private void save(File file) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(file));

		DecimalFormat format = new DecimalFormat("0.############");

		int cc = model.getColumnCount();

		for (int x = 0; x < cc; x++) {
			out.write(model.getColumnName(x));
			if (x < cc - 1) {
				out.write(";");
			}
		}
		out.write("\n");

		for (int y = 0; y < model.getRowCount(); y++) {
			for (int x = 0; x < cc; x++) {
				Object value = model.getValueAt(y, x);

				if (value instanceof Double) {
					out.write(format.format(value));
				} else {
					out.write(value.toString());
				}
				if (x < cc - 1) {
					out.write(";");
				}
			}
			out.write("\n");
		}

		out.flush();
		out.close();
	}

	private void updateRenderer() {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn c = table.getColumnModel().getColumn(i);
			c.setCellRenderer(renderer);
		}
	}

	public void setData(SimulationCollection series) {
		model.setSeries(series);
		updateRenderer();
	}

}
