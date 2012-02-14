package ch.zhaw.simulation.sysintegration.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jdesktop.swingx.action.TargetableAction;

import ch.zhaw.simulation.sysintegration.Toolbar;

public class DefaultToolbar implements Toolbar {
	protected JToolBar toolbar;

	public DefaultToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
	}

	@Override
	public JComponent getComponent() {
		return toolbar;
	}

	@Override
	public ToolbarButton add(final ToolbarAction action) {
		final JButton b = new JButton(action.getToolbarIcon());
		b.setToolTipText(action.getName());
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				action.run(e);
			}
		});
		toolbar.add(b);
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
	public void addSeparator() {
		toolbar.add(new ToolbarSeparator());
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
