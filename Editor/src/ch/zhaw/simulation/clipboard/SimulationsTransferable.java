package ch.zhaw.simulation.clipboard;


import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.zhaw.simulation.clipboard.TransferData.Type;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;


public class SimulationsTransferable implements Transferable {
	public static DataFlavor SIMULATION_FLOWER = new DataFlavor("application/x-simulation", "Simulation");
	private DataFlavor flavors[] = { SIMULATION_FLOWER, DataFlavor.imageFlavor };

	private static BufferedImage dummyImage;

	static {
		dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		dummyImage.getGraphics().drawString("Dummy Image", 0, 50);
	}

	/**
	 * Alle daten die ins Clipboard kopiert werden
	 */
	private ClipboardData data = new ClipboardData();

	/**
	 * Das Model um zusätzliche Daten abzufragen
	 */
	private AbstractSimulationModel model;

	/**
	 * Creates a transferable object capable of transferring the specified
	 * string in plain text format.
	 */
	public SimulationsTransferable(SelectableElement[] selected, AbstractSimulationModel model) {
		this.model = model;
		for (SelectableElement s : selected) {
			addCopy(s);
		}
	}

	private void addCopy(SelectableElement s) {
		// TODO !!!!!
//		if (s instanceof ContainerView) {
//			SimulationContainer c = ((ContainerView) s).getData();
//			data.add(new TransferData(c.getId(), c.getX(), c.getY(), Type.Container, c.getName(), c.getFormula(), 0, 0, null));
//		} else if (s instanceof InfiniteSymbol) {
//			InfiniteData d = ((InfiniteSymbol) s).getData();
//			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.InfiniteSymbol, "", "", 0, 0, null));
//		} else if (s instanceof ParameterView) {
//			SimulationParameter d = ((ParameterView) s).getData();
//			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.Parameter, d.getName(), d.getFormula(), 0, 0, null));
//		} else if (s instanceof TextView) {
//			CommentData d = ((TextView) s).getData();
//			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.Text, d.getName(), d.getText(), 0, 0, null));
//		} else if (s instanceof FlowConnectorParameter) {
//			FlowValve d = ((FlowConnectorParameter) s).getData();
//
//			int source = 0;
//			int target = 0;
//
//			for (FlowConnector f : model.getFlowConnectors()) {
//				if (f.getValve() == d) {
//					source = f.getSource().getId();
//					target = f.getTarget().getId();
//					break;
//				}
//			}
//
//			data.add(new TransferData(d.getId(), d.getX(), d.getY(), Type.Flow, d.getName(), d.getFormula(), source, target, null));
//		} else if (s instanceof ConnectorPoint) {
//			ParameterConnector d = ((ConnectorPoint) s).getConnector();
//			Point p = d.getConnectorPoint();
//			data.add(new TransferData(0, p.x, p.y, Type.Connector, null, null, d.getSource().getId(), d.getTarget().getId(), d.getConnectorPoint()));
//		} else {
//			throw new RuntimeException("Class not handled in copy / paste: " + s.getClass().getName());
//		}
	}

	/**
	 * Returns the array of flavors in which it can provide the data.
	 */
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	/**
	 * Returns whether the requested flavor is supported by this object.
	 * 
	 * @param flavor
	 *            the requested flavor for the data
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(flavors[0]) || flavor.equals(flavors[1]));
	}

	/**
	 * If the data was requested in the "java.lang.String" flavor, return the
	 * String representing the selection, else throw an
	 * UnsupportedFlavorException.
	 * 
	 * @param flavor
	 *            the requested flavor for the data
	 */
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(flavors[0])) {
			return data;
		} else if (flavor.equals(flavors[1])) {
			return dummyImage;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
