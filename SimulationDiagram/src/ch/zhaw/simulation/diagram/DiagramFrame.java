package ch.zhaw.simulation.diagram;

import butti.javalibs.config.WindowPositionSaver;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jdesktop.swingx.action.ActionManager;
import org.jdesktop.swingx.action.TargetManager;
import org.jdesktop.swingx.action.TargetableAction;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramConfigModel model;

	private DiagramSidebar sidebar;

	private DiagramPlot plot;

	private Toolbar toolbar;

	public DiagramFrame(SimulationCollection collection, String name, Sysintegration sys) {
		this.model = new DiagramConfigModel(collection);
		this.sidebar = new DiagramSidebar(this.model);
		this.plot = new DiagramPlot(this.model);
		toolbar = sys.createToolbar();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(name + " - (AB)² Simulation");
		setLayout(new BorderLayout());

		add(BorderLayout.NORTH, toolbar.getComponent());
		add(BorderLayout.WEST, sidebar);
		add(BorderLayout.CENTER, new JScrollPane(plot));

		initToolbar();

		setSize(640, 480);
		setLocationRelativeTo(null);// center

		// restore old position
		new WindowPositionSaver(this);
	}

	private void initToolbar() {
		final int ICON_SIZE = 32;
		
		toolbar.add(new AbstractAction("Anzeigen als Tabelle",IconLoader.getIcon("spreadsheet", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		toolbar.addSeparator();
		
		toolbar.add(new AbstractAction("Speichern als CSV", IconLoader.getIcon("save", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		toolbar.add(new AbstractAction("Speichern als Bild", IconLoader.getIcon("photos", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		toolbar.addSeparator();
		
		toolbar.add(new AbstractAction("Vergrössern", IconLoader.getIcon("zoom-in", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		toolbar.add(new AbstractAction("Verkleinern", IconLoader.getIcon("zoom-out", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		toolbar.add(new AbstractAction("Passend", IconLoader.getIcon("zoom-fit-best", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		toolbar.addSeparator();
		
		TargetableAction action = new TargetableAction("Logarithmisch", "log", IconLoader.getIcon("log", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
		    public void actionPerformed(ActionEvent evt) {
		    }

			@Override
		    public void itemStateChanged(ItemEvent evt) {
		        boolean newValue;
		        newValue = evt.getStateChange() == ItemEvent.SELECTED;
		        
		        System.out.println("log="+newValue);
		    }
		};
		action.setStateAction(true);
		
		toolbar.addToogleAction(action);

	}

	@Override
	public void dispose() {
		plot.dispose();
		sidebar.dispose();
		super.dispose();
	}
}
