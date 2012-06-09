package ch.zhaw.simulation.sysintegration.gui;

import javax.swing.JComponent;
import javax.swing.JToolBar;

public class DefaultToolbar extends AbstractToolbar {
	protected JToolBar toolbar;

	public DefaultToolbar(int defaultIconSize) {
		super(defaultIconSize);
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
	}

	@Override
	public JComponent getComponent() {
		return toolbar;
	}

	@Override
	public void addSeparator() {
		toolbar.add(new ToolbarSeparator());
	}

	@Override
	public void add(JComponent component) {
		toolbar.add(component);
	}

}
