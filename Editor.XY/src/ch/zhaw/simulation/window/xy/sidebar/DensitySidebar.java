package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.editor.xy.density.DensityDraw;
import ch.zhaw.simulation.editor.xy.density.DensityEditorDialog;
import ch.zhaw.simulation.editor.xy.density.DensityListModel;
import ch.zhaw.simulation.editor.xy.density.DensityRenderer;
import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.model.xy.XYModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class DensitySidebar extends JXTaskPane implements SidebarPosition {
	private static final long serialVersionUID = 1L;

	private DensityListModel listModel;
	private JList list;

	private DensityEditorDialog densityEditor;

	public DensitySidebar(JFrame parent, DensityDraw draw, XYModel model, JComponent comp, Sysintegration sys, FunctionHelp help) {
		setTitle("Dichte");

		listModel = new DensityListModel(model);
		list = new JList(listModel);
		list.setCellRenderer(new DensityRenderer());

		densityEditor = new DensityEditorDialog(parent, model, sys, help);

		add(new JScrollPane(list));

		JButton btEditDensity = new JButton("Bearbeiten");
		btEditDensity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				densityEditor.setVisible(true);
			}
		});

		add(btEditDensity);
	}

	public void dispose() {
		listModel.dispose();
	}

	@Override
	public int getSidebarPosition() {
		return 100;
	}
}
