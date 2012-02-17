package ch.zhaw.simulation.sim.sidebar;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.numerictextfield.NumericTextField;

public class DefaultSimulationSidebar extends JXTaskPane implements FocusListener {
	private static final long serialVersionUID = 1L;

	private NumericTextField ntStart = new NumericTextField();
	private NumericTextField ntEnd = new NumericTextField();
	private NumericTextField ntDt = new NumericTextField();

	public DefaultSimulationSidebar() {
		setTitle("Simulation Einstellungen");

		add(new JLabel("Startzeit"));
		add(ntStart);
		ntStart.addFocusListener(this);

		add(new JLabel("Endzeit"));
		add(ntEnd);
		ntEnd.addFocusListener(this);

		add(new JLabel("dt"));
		add(ntDt);
		ntDt.addFocusListener(this);
	}


	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
//		dontAcceptEvents = true;
//		double start = 1;
//		try {
//			start = ntStart.getDoubleValue();
//
//			if (start < 0) {
//				start = 0;
//				ntStart.setValue(0);
//			}
//			model.setStartTime(start);
//		} catch (Exception ex) {
//			ntStart.setValue(model.getStartTime());
//		}
//
//		double end = 1;
//		try {
//			end = endTime.getDoubleValue();
//
//			if (end < 0) {
//				end = 1;
//				endTime.setValue(1);
//			}
//			model.setEndtime(end);
//		} catch (Exception ex) {
//			endTime.setValue(model.getEndTime());
//		}
//
//		double dt = 1;
//		try {
//			dt = dtTime.getDoubleValue();
//
//			if (dt <= 0) {
//				dt = 1;
//				dtTime.setValue(1);
//			}
//			model.setDt(dt);
//		} catch (Exception ex) {
//			dtTime.setValue(model.getDt());
//		}
//
//		dontAcceptEvents = false;
	}

}
