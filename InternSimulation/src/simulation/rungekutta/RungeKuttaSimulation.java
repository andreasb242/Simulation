package simulation.rungekutta;

import java.security.InvalidAlgorithmParameterException;

import math.CompilerError;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.model.InfiniteData;
import ch.zhaw.simulation.model.SimulationAttachment;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;

import simulation.AbstractSimulation;
import simulation.Simulation;
import simulation.data.SimulationCollection;
import butti.javalibs.errorhandler.Errorhandler;

/**
 * Runge-Kutta implementation
 * 
 * <pre>
 * loop {
 * 		i := 0;
 * 		k1 := hf(x[i], y[i]);
 * 		k2 := hf(x[i] + h / 2, y[i] + k1 / 2);
 * 		k3 := hf(x[i] + h / 2, y[i] + k2 / 2);
 * 		k4 := hf(x[i] + h, y[i] + k3);
 * 		i := i + 1;
 * 		x[i] := x[i-1] + h;
 * 		y[i] := y[i-1] + (k1 + 2k2 + 2k3 + k4) / 6;
 * }
 * </pre>
 * 
 * @author Andreas Butti
 */
public class RungeKuttaSimulation extends AbstractSimulation {

	public enum FlowType {
		SRC, TMP1, TMP2, TMP3, TMP4
	};

	public RungeKuttaSimulation(Simulation sim) {
		super(sim);
	}

	@Override
	protected SimulationCollection simulate() throws CompilerError, ParseException, InvalidAlgorithmParameterException {
		SimulationCollection series = getSeries();

		try {
			for (SimulationContainer c : model.getSimulationContainer()) {
				c.a.tmp = new TmpContainerValues();
			}

			for (time = 0; time < endTime && !isCancelled(); time += dt) {
				setProgress((int) (time / endTime * 100));
				calcFlowValues1();
				calcFlowValues2();
				calcFlowValues3();
				calcFlowValues4();
				calcContainers();
				calcParameters();

				for (SimulationContainer c : model.getSimulationContainer()) {
					Object v = c.a.getContainerValue();
					if (v instanceof Double) {
						c.a.serie.add(time, (Double) v);
					} else {
						c.a.serie.setOtherType(v.getClass().getName());
					}
				}
			}
		} finally {
			cleanup();
		}

		return series;
	}

	private void calcFlowValues1() throws CompilerError, ParseException {
		calcFlowValues();
		calcContainersTmp12(FlowType.SRC, FlowType.TMP1, 0.5);
	}

	private void calcFlowValues2() throws CompilerError, ParseException {
		calcFlowValues();
		calcContainersTmp12(FlowType.SRC, FlowType.TMP2, 0.5);
	}

	private void calcFlowValues3() throws ParseException, CompilerError {
		calcFlowValues();
		calcContainersTmp3(FlowType.SRC, FlowType.TMP3);
	}

	private void calcFlowValues4() throws CompilerError, ParseException {
		calcFlowValues();
		calcContainersTmp4(FlowType.SRC, FlowType.TMP4);
	}

	protected void calcContainersTmp12(FlowType src, FlowType trg, double weight) throws ParseException {
		for (FlowConnector c : model.getFlowConnectors()) {
			doFlowTmp12(c, src, trg, weight);
		}
	}

	protected void calcContainersTmp3(FlowType src, FlowType trg) throws ParseException {
		for (FlowConnector c : model.getFlowConnectors()) {
			doFlowTmp3(c, src, trg);
		}
	}

	protected void calcContainersTmp4(FlowType src, FlowType trg) throws ParseException {
		for (FlowConnector c : model.getFlowConnectors()) {
			doFlowTmp4(c, src, trg);
		}
	}

	protected void doFlowTmp3(FlowConnector c, FlowType src, FlowType trg) throws ParseException {
		Object newValue = c.getParameterPoint().getNextFlowValue();
		SimulationObject source = c.getSource();
		SimulationObject target = c.getTarget();

		if (source instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (source instanceof SimulationContainer) {
			Object value;
			SimulationAttachment a = ((SimulationContainer) source).a;
			TmpContainerValues t = (TmpContainerValues) a.tmp;
			if (src == FlowType.SRC) {
				value = a.getContainerValue();
			} else {
				value = t.getValue(src);
			}

			Object newValue2 = a.multiple(newValue, dt);

			newValue = a.subtract(value, newValue2);

			t.set(newValue, trg);

			// Nächste TMP Wert
			newValue2 = a.subtract(value, newValue2);
			t.setValue(newValue2);

		} else {
			throw new RuntimeException("Source of flow is: " + source.getClass());
		}

		if (target instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (target instanceof SimulationContainer) {
			Object value;
			SimulationAttachment a = ((SimulationContainer) target).a;
			TmpContainerValues t = (TmpContainerValues) a.tmp;
			if (src == FlowType.SRC) {
				value = a.getContainerValue();
			} else {
				value = t.getValue(src);
			}

			Object newValue2 = a.multiple(newValue, dt);

			newValue = a.add(value, newValue2);

			t.set(newValue, trg);

			// Nächste TMP Wert
			newValue2 = a.add(value, newValue2);
			t.setValue(newValue2);
		} else {
			throw new RuntimeException("Target of flow is: " + target.getClass());
		}
	}

	protected void doFlowTmp12(FlowConnector c, FlowType src, FlowType trg, double weight) throws ParseException {
		Object newValue = c.getParameterPoint().getNextFlowValue();
		SimulationObject source = c.getSource();
		SimulationObject target = c.getTarget();

		if (source instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (source instanceof SimulationContainer) {
			Object value;
			SimulationAttachment a = ((SimulationContainer) source).a;
			TmpContainerValues t = (TmpContainerValues) a.tmp;
			if (src == FlowType.SRC) {
				value = a.getContainerValue();
			} else {
				value = t.getValue(src);
			}

			Object newValue2 = a.multiple(newValue, dt);

			newValue = a.subtract(value, newValue2);

			t.set(newValue, trg);

			// Nächste TMP Wert
			newValue2 = a.multiple(newValue2, weight);
			newValue2 = a.subtract(value, newValue2);
			t.setValue(newValue2);

		} else {
			throw new RuntimeException("Source of flow is: " + source.getClass());
		}

		if (target instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (target instanceof SimulationContainer) {
			Object value;
			SimulationAttachment a = ((SimulationContainer) target).a;
			TmpContainerValues t = (TmpContainerValues) a.tmp;
			if (src == FlowType.SRC) {
				value = a.getContainerValue();
			} else {
				value = t.getValue(src);
			}

			Object newValue2 = a.multiple(newValue, dt);

			newValue = a.add(value, newValue2);

			t.set(newValue, trg);

			// Nächste TMP Wert
			newValue2 = a.multiple(newValue2, weight);
			newValue2 = a.add(value, newValue2);
			t.setValue(newValue2);
		} else {
			throw new RuntimeException("Target of flow is: " + target.getClass());
		}
	}

	protected void doFlowTmp4(FlowConnector c, FlowType src, FlowType trg) throws ParseException {
		Object newValue = c.getParameterPoint().getNextFlowValue();
		SimulationObject source = c.getSource();
		SimulationObject target = c.getTarget();

		if (source instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (source instanceof SimulationContainer) {
			Object value;
			SimulationAttachment a = ((SimulationContainer) source).a;
			TmpContainerValues t = (TmpContainerValues) a.tmp;
			if (src == FlowType.SRC) {
				value = a.getContainerValue();
			} else {
				value = t.getValue(src);
			}

			Object newValue2 = a.multiple(newValue, dt);

			c.getParameterPoint().a.serie.add(time, newValue2);

			newValue = a.subtract(value, newValue2);

			t.set(newValue, trg);

		} else {
			throw new RuntimeException("Source of flow is: " + source.getClass());
		}

		if (target instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (target instanceof SimulationContainer) {
			Object value;
			SimulationAttachment a = ((SimulationContainer) target).a;
			TmpContainerValues t = (TmpContainerValues) a.tmp;
			if (src == FlowType.SRC) {
				value = a.getContainerValue();
			} else {
				value = t.getValue(src);
			}

			Object newValue2 = a.multiple(newValue, dt);

			c.getParameterPoint().a.serie.add(time, newValue2);

			newValue = a.add(value, newValue2);

			t.set(newValue, trg);
		} else {
			throw new RuntimeException("Target of flow is: " + target.getClass());
		}
	}

	protected void calcFlowValues() throws CompilerError, ParseException {
		for (FlowConnector c : model.getFlowConnectors()) {
			calcFlowValue(c);
		}
	}

	protected void calcFlowValue(FlowConnector c) throws CompilerError, ParseException {
		FlowParameterPoint d = c.getParameterPoint();

		Object value;
		try {
			value = d.a.calc(time, dt);
		} catch (NullPointerException e) {
			Errorhandler.logError(e, "Fehler beim parsen von Connector: " + c.toString());
			throw e;
		}

		if (value == null) {
			throw new NullPointerException("value == null");
		}

		d.setNextFlowValue(value);
	}

	protected void cleanup() {
		for (SimulationContainer c : model.getSimulationContainer()) {
			c.a.tmp = null;
		}
	}

	protected void calcContainers() throws ParseException {
		for (SimulationContainer c : model.getSimulationContainer()) {
			calcContainer(c);
		}
	}

	protected void calcContainer(SimulationContainer c) throws ParseException {
		TmpContainerValues tc = (TmpContainerValues) c.a.tmp;
		if (tc.value2 == null) {
			return;
		}

		// yn = yn + h / 6 * (K1 + 2 * K2 + 2 * K3 + K4);

		Object k2_2 = c.a.multiple(tc.value2, 2);

		Object k3_2 = c.a.multiple(tc.value3, 2);

		Object sum = c.a.add(k2_2, k3_2);
		sum = c.a.add(sum, tc.value1);
		sum = c.a.add(sum, tc.value4);

		sum = c.a.multiple(sum, 1d / 6d);

		c.a.setContainerValue(sum);
		TmpContainerValues t = (TmpContainerValues) c.a.tmp;
		t.setValue(null);
	}
}
