package ch.zhaw.simulation.sysintegration.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
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
	private int defaultIconSize;

	public DefaultToolbar(int defaultIconSize) {
		this.defaultIconSize = defaultIconSize;
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
	}

	@Override
	public JComponent getComponent() {
		return toolbar;
	}

	@Override
	public ToolbarButton add(final ToolbarAction action) {
		final JButton b = new JButton(action.getToolbarIcon(this.defaultIconSize));
		b.setToolTipText(action.getName());
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				action.run(e);
			}
		});
		toolbar.add(b);

		return new ToolbarButtonImpl(b);
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
	public int getDefaultIconSize() {
		return defaultIconSize;
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

		return new ToolbarButtonImpl(b);
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

		return new ToolbarButtonImpl(b);
	}

	public static class ToolbarButtonImpl implements ToolbarButton {
		private AbstractButton button;

		public ToolbarButtonImpl(AbstractButton button) {
			this.button = button;
		}

		@Override
		public void setText(String text) {
			button.setToolTipText(text);
		}

		@Override
		public void setEnabled(boolean enabled) {
			button.setEnabled(enabled);
		}

		@Override
		public void setIcon(Icon icon) {
			button.setIcon(icon);
		}

		@Override
		public JComponent getComponent() {
			return button;
		}

	}
}
