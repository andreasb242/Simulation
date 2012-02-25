package ch.zhaw.simulation.gui.configuration.codeditor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXStatusBar;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.help.model.FunctionInformation;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.SimulationGlobal;
import ch.zhaw.simulation.model.flow.NamedSimulationObject.Status;
import ch.zhaw.simulation.sysintegration.Toolbar;

import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;

import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.errorhandler.Errorhandler;

public class FormulaEditor extends BDialog {
	private static final long serialVersionUID = 1L;

	private FormulaTextPane text;
	private Toolbar tb;

	private Functionlist constants = new Functionlist("Konstanten", "constants", this);
	private Functionlist variables = new Functionlist("Variablen", "variables", this);
	private Functionlist globals = new Functionlist("Globale", "globals", this);
	private Functionlist functions = new Functionlist("Funktionen", "functions", this);

	private JXStatusBar sBar = new JXStatusBar();
	private JLabel statusLabel = new JLabel("");
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private String value;

	private FlowEditorControl control;

	private Parser parser = new Parser();

	private NamedSimulationObject data;

	private FormularKeyboard keyboard;

	private FunctionHelp help;

	private GridBagManager gbm;

	private Timer checkTimer = null;

	private static final int CHECK_DELAY = 1000;

	public FormulaEditor(JFrame parent, FlowEditorControl control) {
		super(parent);
		this.control = control;
		help = control.getFunctionHelp();
		tb = control.getSysintegration().createToolbar();

		text = new FormulaTextPane(help);

		gbm = new GridBagManager(this);

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(10).setScrollPanel().setComp(text);

		sBar.add(statusLabel);
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setWidth(2).setY(20).setWeightY(0).setComp(sBar);

		setSize(500, 400);
		new WindowPositionSaver(this);

		initToolbar();

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(0).setWidth(2).setWeightY(0).setComp(tb.getComponent());

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (data != null) {
					checkFormula();
					data = null;
				}
			}

		});

		keyboard = new FormularKeyboard(text);
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(1).setY(10).setWeightX(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.PAGE_START).setComp(keyboard);

		text.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				documentChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				documentChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				documentChanged();
			}

		});

		setLocationRelativeTo(parent);
	}

	public void setTitle(String title) {
		super.setTitle(title + " - Formeleditor");
	}

	private void documentChanged() {
		if (checkTimer != null) {
			checkTimer.cancel();
		}
		checkTimer = new Timer();
		checkTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println("check timer activated");
				checkFormula();
			}
		}, CHECK_DELAY);
	}

	private void initToolbar() {
		JComponent component = tb.add(constants).getComponent();
		constants.setComponent(component);

		component = tb.add(variables).getComponent();
		variables.setComponent(component);

		component = tb.add(globals).getComponent();
		globals.setComponent(component);

		component = tb.add(functions).getComponent();
		functions.setComponent(component);

		// JButton btCompile = new JButton("Prüfen", IconSVG.getIcon("refresh",
		// 24));
		// tb.add(btCompile);
		// btCompile.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// checkFormula();
		// }
		// });
		//
		// tb.add(cbConstants);
		//
		// cbConstants.setRenderer(new ListRenderer());
		//
		// cbConstants.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// if (editInitialized) {
		// Object elem = cbConstants.getSelectedItem();
		// if (elem == null) {
		// return;
		// }
		//
		// if (elem instanceof Function) {
		// inserEditor(((Function) elem).getName() + "()", -1);
		// } else if (elem instanceof Constant) {
		// inserEditor(((Constant) elem).name, 0);
		// } else if (elem instanceof NamedSimulationObject) {
		// inserEditor(((NamedSimulationObject) elem).getName(), 0);
		// } else {
		// System.out.println("class " + elem.getClass().getName() +
		// " not handled");
		// }
		// }
		// }
		// });
	}

	protected void checkFormula() {
		if (value == null || data == null) {
			System.out.println("NULL detected! value: " + value + "; data = " + data);
			return;
		}

		Status status = NamedSimulationObject.Status.SYNTAX_ERROR;

		Calendar cal = Calendar.getInstance();
		String statusText = null;

		try {
			Vector<NamedSimulationObject> sources = control.getModel().getSource(data);

			parser.checkCode(text.getText(), data, control.getModel(), sources, data.getName());
			status = NamedSimulationObject.Status.SYNTAX_OK;

			text.setError(0, 0);
			statusLabel.setText(sdf.format(cal.getTime()) + ": Formel OK");
			statusLabel.setIcon(IconSVG.getIcon("ok"));
		} catch (CompilerError error) {
			text.setError(error.getLine(), error.getWidth());
			statusLabel.setText(sdf.format(cal.getTime()) + ": " + error.getMessage());
			statusLabel.setIcon(IconSVG.getIcon("warning"));
			statusText = error.getMessage();
		} catch (SimulationModelException error) {
			statusLabel.setText(sdf.format(cal.getTime()) + ": " + error.getMessage());
			statusLabel.setIcon(IconSVG.getIcon("warning"));
			statusText = error.getMessage();
		} catch (Exception e) {
			statusLabel.setText(sdf.format(cal.getTime()) + ": " + e.getMessage());
			statusLabel.setIcon(IconSVG.getIcon("warning"));
			statusText = e.getMessage();
			Errorhandler.logError(e);
		}
		data.setFormula(text.getText(), status, statusText);
		control.getModel().fireObjectChanged(data, false);
	}

	public void inserEditor(String text, int relCursor) {
		this.text.inserEditor(text, relCursor);
		this.text.requestFocus();
	}

	public void setData(NamedSimulationObject data) {
		if (this.data != null) {
			// Save Data
			checkFormula();
		}

		value = data.getFormula();
		this.data = data;

		text.setText(data.getFormula());

		setTitle(data.getName());

		text.clearAutocompletet();

		Vector<NamedSimulationObject> parameter = control.getModel().getSource(data);

		constants.clear();
		for (Constant c : getConst()) {
			constants.addElement(c);
			text.addAutocomplete(new Autocomplete.AutocompleteWord(c.name, 0));
		}

		variables.clear();
		for (NamedSimulationObject p : parameter) {
			variables.addElement(p);
			text.addAutocomplete(new Autocomplete.AutocompleteWord(p.getName(), 0));
		}

		variables.addElement(new Constant("time", 0.1));
		variables.addElement(new Constant("dt", 0.1));
		text.addAutocomplete(new Autocomplete.AutocompleteWord("time", 0));
		text.addAutocomplete(new Autocomplete.AutocompleteWord("dt", 0));

		functions.clear();

		for (Entry<String, Vector<FunctionInformation>> e : help.getData().entrySet()) {
			functions.addElement(e.getKey(), e.getValue());
		}

		for (Function f : getFunctions()) {
			text.addAutocomplete(new Autocomplete.AutocompleteWord(f.getDescription(), -1));
		}

		SimulationFlowModel model = control.getModel();

		Vector<SimulationGlobal> globalData = model.getGlobalsFor(data);
		text.setConsts(getConst(), getFunctions(), parameter, globalData);
		updateGlobals(globalData);

		checkFormula();
	}

	private void updateGlobals(Vector<SimulationGlobal> global) {
		globals.clear();

		for (SimulationGlobal g : global) {
			globals.addElement(g);
		}
	}

	private Function[] getFunctions() {
		return parser.getFunctionlist();
	}

	private Constant[] getConst() {
		return parser.getConst();
	}
}
