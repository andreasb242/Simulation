package ch.zhaw.simulation.gui.codeditor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXStatusBar;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.help.model.FunctionInformation;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.NamedFormulaData.Status;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class FormulaEditorPanel extends JPanel {
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

	private Parser parser = new Parser();

	private NamedFormulaData data;

	private FormularKeyboard keyboard;

	private FunctionHelp help;

	private GridBagManager gbm;

	private Timer checkTimer = null;

	private AbstractSimulationModel<?> model;

	private static final int CHECK_DELAY = 1000;

	private Vector<String> additionalVars = new Vector<String>();

	public FormulaEditorPanel(Sysintegration sys, FunctionHelp help, AbstractSimulationModel<?> model, Vector<String> addiditonalVars) {
		this.help = help;
		this.model = model;
		this.additionalVars = addiditonalVars;
		tb = sys.createToolbar();

		text = new FormulaTextPane(help);

		gbm = new GridBagManager(this);

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(10).setScrollPanel().setPreferedSize(300, 2).setComp(text);

		sBar.add(statusLabel);
		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setWidth(2).setY(20).setWeightY(0).setComp(sBar);

		initToolbar();

		gbm.setInsets(new Insets(0, 0, 0, 0));
		gbm.setX(0).setY(0).setWidth(2).setWeightY(0).setComp(tb.getComponent());

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

		JButton btCompile = new JButton("Prüfen", IconLoader.getIcon("refresh", 24));
		tb.add(btCompile);
		btCompile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkFormula();
			}
		});

	}

	protected void checkFormula() {
		if (value == null || data == null) {
			return;
		}

		Status status = AbstractNamedSimulationData.Status.SYNTAX_ERROR;

		Calendar cal = Calendar.getInstance();
		String statusText = null;

		try {
			Vector<AbstractNamedSimulationData> sources = model.getSource(data);

			parser.checkCode(text.getText(), data, model, sources, this.additionalVars, data.getName());
			status = AbstractNamedSimulationData.Status.SYNTAX_OK;

			text.setError(0, 0);
			statusLabel.setText(sdf.format(cal.getTime()) + ": Formel OK");
			statusLabel.setIcon(IconLoader.getIcon("ok"));
		} catch (CompilerError error) {
			text.setError(error.getLine(), error.getWidth());
			statusLabel.setText(sdf.format(cal.getTime()) + ": " + error.getMessage());
			statusLabel.setIcon(IconLoader.getIcon("warning"));
			statusText = error.getMessage();
		} catch (SimulationModelException error) {
			statusLabel.setText(sdf.format(cal.getTime()) + ": " + error.getMessage());
			statusLabel.setIcon(IconLoader.getIcon("warning"));
			statusText = error.getMessage();
		} catch (Exception e) {
			statusLabel.setText(sdf.format(cal.getTime()) + ": " + e.getMessage());
			statusLabel.setIcon(IconLoader.getIcon("warning"));
			statusText = e.getMessage();
			Errorhandler.logError(e);
		}
		data.setFormula(text.getText(), status, statusText);
		model.fireObjectChangedAutoparser(data);
	}

	public void inserEditor(String text, int relCursor) {
		this.text.inserEditor(text, relCursor);
		this.text.requestFocus();
	}

	private void addVarParser(String var) {
		variables.addElement(new Constant(var, 0.1));
		text.addAutocomplete(new Autocomplete.AutocompleteWord(var, 0));
	}

	public void setData(NamedFormulaData data) {
		if (this.data != null) {
			// Save Data
			checkFormula();
		}

		value = data.getFormula();
		this.data = data;

		text.setText(data.getFormula());

		text.clearAutocompletet();

		Vector<AbstractNamedSimulationData> parameter = model.getSource(data);

		constants.clear();

		for (Constant c : getConst()) {
			constants.addElement(c);
			text.addAutocomplete(new Autocomplete.AutocompleteWord(c.name, 0));
		}

		variables.clear();
		for (AbstractNamedSimulationData p : parameter) {
			variables.addElement(p);
			text.addAutocomplete(new Autocomplete.AutocompleteWord(p.getName(), 0));
		}

		addVarParser("time");
		addVarParser("dt");
		for (String v : additionalVars) {
			addVarParser(v);
		}

		functions.clear();

		for (Entry<String, Vector<FunctionInformation>> e : help.getData().entrySet()) {
			functions.addElement(e.getKey(), e.getValue());
		}
		for (Function f : getFunctions()) {
			text.addAutocomplete(new Autocomplete.AutocompleteWord(f.getDescription(), -1));
		}

		Vector<SimulationGlobalData> globalData = model.getGlobalsFor(data);
		text.setConsts(getConst(), getFunctions(), parameter, globalData);
		updateGlobals(globalData);

		checkFormula();
	}

	private void updateGlobals(Vector<SimulationGlobalData> global) {
		globals.clear();

		for (SimulationGlobalData g : global) {
			globals.addElement(g);
		}
	}

	private Function[] getFunctions() {
		return parser.getFunctionlist();
	}

	private Constant[] getConst() {
		return parser.getConst();
	}

	public Parser getParser() {
		return parser;
	}
}
