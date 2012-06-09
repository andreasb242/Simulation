package ch.zhaw.simulation.sysintegration.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import org.jdesktop.swingx.action.TargetableAction;

import ch.zhaw.simulation.sysintegration.Toolbar;

public abstract class AbstractToolbar implements Toolbar {
	protected int defaultIconSize;

	public AbstractToolbar(int defaultIconSize) {
		this.defaultIconSize = defaultIconSize;
	}

	@Override
	public JButton add(final ToolbarAction action) {
		final JButton b = new JButton(action.getToolbarIcon(this.defaultIconSize));
		b.setToolTipText(action.getName());
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				action.run(e);
			}
		});
		add(b);

		return b;
	}

	@Override
	public int getDefaultIconSize() {
		return defaultIconSize;
	}

	@Override
	public JToggleButton addToogleAction(TargetableAction action) {
		final JToggleButton b = new JToggleButton((Icon) action.getValue(Action.SMALL_ICON));
		b.setToolTipText(action.getValue(Action.NAME).toString());
		add(b);

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

		return b;
	}

	@Override
	public JButton add(Action action) {
		final JButton b = new JButton((Icon) action.getValue(Action.SMALL_ICON));
		b.setToolTipText(action.getValue(Action.NAME).toString());
		add(b);

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
		return b;
	}
}
