package ch.zhaw.simulation.sysintegration.mac;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jdesktop.swingx.action.TargetableAction;

import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.gui.ImageButton;


public class MacOSXToolbar implements Toolbar {
	protected JPanel toolbar;

	public MacOSXToolbar() {
		toolbar = new JPanel();
		toolbar.setLayout(new MacToolbarLayoutManager());
	}

	@Override
	public JComponent getComponent() {
		return toolbar;
	}

	@Override
	public ToolbarButton add(ToolbarAction action) {
		ImageButton tb = new ImageButton(action);
		toolbar.add(tb);
		return tb;
	}

	public void addSeparator() {
		toolbar.add(new MacToolbarSeparator());
	}

	@Override
	public void add(JComponent component) {
		toolbar.add(component);
	}

	@Override
	public ToolbarButton add(Action action) {
		final JButton b = new JButton((Icon) action.getValue(Action.SMALL_ICON));
		b.setToolTipText(action.getValue(Action.NAME).toString());
		toolbar.add(b);

		action.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("selected".equals(evt.getPropertyName())) {
					b.setSelected((Boolean) evt.getNewValue());
				} else if ("enabled".equals(evt.getPropertyName())) {
					b.setEnabled((Boolean) evt.getNewValue());
				}
			}
		});

		b.addActionListener(action);

		return new ToolbarButton() {

			@Override
			public void setText(String text) {
				b.setToolTipText(text);
			}

			@Override
			public void setEnabled(boolean enabled) {
				b.setEnabled(enabled);
			}

			@Override
			public JComponent getComponent() {
				return b;
			}
		};
	}

	@Override
	public ToolbarButton addToogleAction(TargetableAction action) {

		final JToggleButton b = new JToggleButton((Icon) action.getValue(Action.SMALL_ICON));
		b.setToolTipText(action.getValue(Action.NAME).toString());
		toolbar.add(b);

		action.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("selected".equals(evt.getPropertyName())) {
					b.setSelected((Boolean) evt.getNewValue());
				} else if ("enabled".equals(evt.getPropertyName())) {
					b.setEnabled((Boolean) evt.getNewValue());
				}
			}
		});

		b.addItemListener(action);

		return new ToolbarButton() {

			@Override
			public void setText(String text) {
				b.setToolTipText(text);
			}

			@Override
			public void setEnabled(boolean enabled) {
				b.setEnabled(enabled);
			}

			@Override
			public JComponent getComponent() {
				return b;
			}
		};
	}
}
