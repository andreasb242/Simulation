package ch.zhaw.simulation.diagram.tableview;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.gui.BDialog;
import butti.javalibs.util.JTableCopyAdapter;
import ch.zhaw.simulation.diagram.csv.CSVSaver;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarButton;

public class TableDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private SimulationTableModel model;

	private TableRenderer renderer = new TableRenderer();

	private JTable table;

	private JTableCopyAdapter copyAdapter;

	private JSpinner precision = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));

	private ToolbarButton tbCopy;

	private Settings settings;

	private Window parent;

	private SimulationCollection series;

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

	public TableDialog(Window parent, final Settings settings, SimulationCollection series) {
		super(parent);

		this.parent = parent;
		this.settings = settings;
		this.series = series;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setTitle("Simulationsdaten");

		model = new SimulationTableModel(series);
		table = new JTable(model);
		table.setColumnSelectionAllowed(true);
		table.getSelectionModel().addListSelectionListener(listener);
		table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		updateRenderer();

		setLayout(new BorderLayout());

		add(new JScrollPane(table), BorderLayout.CENTER);

		copyAdapter = new JTableCopyAdapter(table);

		initToolbar();

		precision.setValue((int) settings.getSetting("table.precision", 2.0));

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				settings.setSetting("table.precision", (Integer) precision.getValue());
			}

		});

		new WindowPositionSaver(this);
	}

	private void initToolbar() {
		Toolbar toolbar = SysintegrationFactory.getSysintegration().createToolbar(32);
		tbCopy = toolbar.add(new ToolbarAction("Kopieren", "diagram/edit-copy") {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyAdapter.copy();
			}
		});

		tbCopy.setEnabled(false);

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Speichern", "diagram/save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				CSVSaver saver = new CSVSaver(TableDialog.this.parent, settings, series);
				saver.save();
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
