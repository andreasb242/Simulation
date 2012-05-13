package ch.zhaw.simulation.diagram.strokeeditor;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import butti.javalibs.config.Config;
import butti.javalibs.gui.messagebox.Messagebox;

public class EditorDataRow {
	private StrokePreview preview;
	private JSpinner thikness;
	private JComboBox linestyle;
	private Dash customDash = null;

	public EditorDataRow(Paint paint, int thinkness, float[] dash, Color diagramBackground) {
		this.preview = new StrokePreview(paint, diagramBackground);
		this.thikness = new JSpinner(new SpinnerNumberModel(thinkness, 1, 20, 1));

		String str = Config.get("predefinedDash");
		String[] dashStr = new String[] {};
		if (str == null) {
			Messagebox.showError(null, "Dash Konfiguration", "<html>Kein «predefinedDash» erfasst in «" + Config.getConfigFile() + "».<br>"
					+ "Bitte erfassen Sie etwas wie: «:5 5: 10 10: 2 10: 10 10 2 10»</html>");
		} else {
			dashStr = str.split(":");
		}

		Vector<Dash> dashes = new Vector<Dash>();
		for (String s : dashStr) {
			dashes.add(new Dash(s));
		}

		this.linestyle = new JComboBox(dashes);
		this.linestyle.setRenderer(new DashComboboxRenderer());
		Dash d = new Dash(dash);

		this.linestyle.setSelectedItem(d);
		if (!d.equals(this.linestyle.getSelectedItem())) {
			this.linestyle.addItem(d);
			this.linestyle.setSelectedIndex(this.linestyle.getItemCount() - 1);
		}

		this.linestyle.setEditable(true);
	}

	public Paint getPaint() {
		return this.preview.getPaint();
	}

	public void setPaint(Paint paint) {
		this.preview.setPaint(paint);
	}

	public Stroke getStroke() {
		return this.preview.getStroke();
	}

	public void add(JPanel panel) {
		panel.add(this.preview);
		panel.add(this.thikness);
		panel.add(this.linestyle);

		this.thikness.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int thikness = (Integer) EditorDataRow.this.thikness.getValue();
				EditorDataRow.this.preview.setThikness(thikness);
			}
		});
		this.linestyle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = EditorDataRow.this.linestyle.getSelectedItem();
				if (selected instanceof Dash) {
					EditorDataRow.this.preview.setDash(((Dash) selected).getDash());
				} else {
					if (customDash == null) {
						customDash = new Dash(selected.toString());
						linestyle.addItem(customDash);
					} else {
						customDash.parse(selected.toString());
					}

					linestyle.setSelectedItem(customDash);
				}
			}
		});
	}
}
