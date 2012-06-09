package ch.zhaw.simulation.sysintegration.mac;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ch.zhaw.simulation.sysintegration.gui.AbstractToolbar;

public class MacOSXToolbar extends AbstractToolbar {
	protected JPanel toolbar;

	public MacOSXToolbar(int defaultIconSize) {
		super(defaultIconSize);
		toolbar = new JPanel();
		toolbar.setLayout(new MacToolbarLayoutManager());
	}

	@Override
	public JComponent getComponent() {
		return toolbar;
	}

	public void addSeparator() {
		toolbar.add(new MacToolbarSeparator());
	}

	@Override
	public void add(JComponent component) {
		toolbar.add(component);
	}

}
