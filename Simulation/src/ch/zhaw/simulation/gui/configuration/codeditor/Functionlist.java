package ch.zhaw.simulation.gui.configuration.codeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ch.zhaw.simulation.help.model.FunctionInformation;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.model.NamedSimulationObject;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationGlobal;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;

public class Functionlist extends ToolbarAction {
	private JPopupMenu menu = new JPopupMenu();
	private FormulaEditor editor;
	private JComponent component;

	public Functionlist(String name, String iconName, FormulaEditor editor) {
		super(name, iconName);
		this.editor = editor;
	}

	public void setComponent(JComponent component) {
		this.component = component;
	}

	private void showMenu() {
		menu.show(component, 0, component.getHeight());
	}

	public void clear() {
		menu.removeAll();
		component.setEnabled(false);
	}

	public void addElement(final Constant c) {
		JMenuItem mi = new JMenuItem("<html><b>" + c.name + "</b> " + c.value
				+ "</html>");
		menu.add(mi);
		mi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.inserEditor(c.name, 0);
			}
		});
		component.setEnabled(true);
	}

	public void addElement(final NamedSimulationObject p) {
		String type;
		if (p instanceof SimulationContainer) {
			type = "Container";
		} else if (p instanceof SimulationGlobal) {
			type = "Globale";
		} else {
			type = "Parameter";
		}
		JMenuItem mi = new JMenuItem("<html><b>" + p.getName() + "</b> " + type
				+ "</html>");
		menu.add(mi);
		mi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.inserEditor(p.getName(), 0);
			}
		});
		component.setEnabled(true);
	}

	public void addElement(String cateogry, Vector<FunctionInformation> data) {
		JMenu menu = new JMenu(cateogry);

		for (FunctionInformation v : data) {
			addElement(menu, v);
		}

		this.menu.add(menu);
		component.setEnabled(true);
	}

	private void addElement(JMenu menu, final FunctionInformation info) {
		JMenuItem mi = new JMenuItem("<html><b>" + info.getName() + "</b>("
				+ info.getParameter() + ")<br>" + info.getDescription()
				+ "</html>");
		menu.add(mi);
		mi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.inserEditor(info.getName() + "()", -1);
			}
		});
		component.setEnabled(true);
	}

	@Override
	protected void actionPerformed(ActionEvent e) {
		showMenu();
	}
}
