package ch.zhaw.simulation.sysintegration.mac;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import ch.zhaw.simulation.sysintegration.gui.AbstractToolbar;
import ch.zhaw.simulation.sysintegration.mac.ui.FlatToolbarButtonUI;

public class MacOSXToolbar extends AbstractToolbar {
	protected JToolBar toolbar;

	private FlatToolbarButtonUI flatButtonUi = new FlatToolbarButtonUI();

	public MacOSXToolbar(int defaultIconSize) {
		super(defaultIconSize);
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new MacToolbarLayoutManager());
		toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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

		if (component instanceof JButton) {
			JButton bt = (JButton) component;
			bt.setUI(flatButtonUi);
		}
	}

}
