package ch.zhaw.simulation.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.zhaw.simulation.model.flow.selection.SelectableElement;

public abstract class AbstractTransferable implements Transferable {
	public static DataFlavor SIMULATION_FLOWER = new DataFlavor("application/x-simulation", "Simulation");
	private DataFlavor flavors[] = { SIMULATION_FLOWER, DataFlavor.imageFlavor };

	private static BufferedImage dummyImage;

	static {
		dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		dummyImage.getGraphics().drawString("Dummy Image", 0, 50);
	}

	/**
	 * Creates a transferable object capable of transferring the specified
	 * string in plain text format.
	 */
	public AbstractTransferable(SelectableElement[] selected) {
		initClipboardData();

		for (SelectableElement s : selected) {
			addCopy(s);
		}
	}

	/**
	 * Initialies the clipboard Data element
	 */
	public abstract void initClipboardData();

	/**
	 * Creates the real copy of the element
	 */
	protected abstract void addCopy(SelectableElement s);

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
	 * @return the clipboard data
	 */
	protected abstract Object getClipboardData();

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
			return getClipboardData();
		} else if (flavor.equals(flavors[1])) {
			return dummyImage;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
