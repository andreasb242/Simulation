package ch.zhaw.simulation.sim.intern.model;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Add;
import org.nfunk.jep.function.Multiply;
import org.nfunk.jep.function.Subtract;

import ch.zhaw.simulation.sim.intern.rungekutta.TmpValue;

public class SimulationContainerAttachment extends SimulationAttachment {
	private Object containerValue;

	public SimulationContainerAttachment() {
	}

	public Object getContainerValue() {
		return containerValue;
	}

	public void setContainerValue(Object containerValue) {
		if (containerValue == null) {
			throw new NullPointerException("containerValue == null");
		}
		this.containerValue = containerValue;
	}

	public void addContainerValue(Object value, double dt) throws ParseException {
		if (value == null) {
			throw new NullPointerException("value == null");
		}

		value = multiple(value, dt);
		containerValue = add(containerValue, value);
	}

	public void minusContainerValue(Object value, double dt) throws ParseException {
		if (value == null) {
			throw new NullPointerException("value == null");
		}

		value = multiple(value, dt);

		containerValue = subtract(containerValue, value);
	}

	public Object add(Object param1, Object param2) throws ParseException {
		Add add = (Add) parsed.jep.getOperatorSet().getAdd().getPFMC();
		return add.add(param1, param2);
	}

	public Object subtract(Object param1, Object param2) throws ParseException {
		Subtract sub = (Subtract) parsed.jep.getOperatorSet().getSubtract().getPFMC();
		return sub.sub(param1, param2);
	}

	public Object multiple(Object param1, Object param2) throws ParseException {
		Multiply mul = (Multiply) parsed.jep.getOperatorSet().getMultiply().getPFMC();
		try {
			return mul.mul(param1, param2);
		} catch (ParseException e) {
			System.err.println("param1: " + param1);
			System.err.println("param2: " + param2);
			throw e;
		}
	}

	public Object calc(double time, double dt) throws ParseException {
		if (containerValue != null) {
			if (tmp instanceof TmpValue) {
				Object v = ((TmpValue) tmp).getValue();
				if (v != null) {
					return v;
				}
			}
			return containerValue;
		}

		return super.calc(time, dt);
	}

}
