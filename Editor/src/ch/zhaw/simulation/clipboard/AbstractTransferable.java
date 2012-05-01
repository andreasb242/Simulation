package ch.zhaw.simulation.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import ch.zhaw.simulation.clipboard.TransferData.Type;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.selection.SelectableElement;

public abstract class AbstractTransferable<T extends AbstractClipboardData<?, ?>> implements Transferable {
	public static DataFlavor SIMULATION_FLOWER = new DataFlavor("application/x-simulation", "Simulation");
	private DataFlavor flavors[] = { SIMULATION_FLOWER, DataFlavor.imageFlavor };

	protected BufferedImage exportedImage = null;

	/**
	 * Alle Daten die ins Clipboard kopiert werden
	 */
	protected T data;

	public interface TransferDataTransformer {
		public TransferData transform(SelectableElement<?> s, Object data);
	}

	private HashMap<Class<?>, TransferDataTransformer> transformers = new HashMap<Class<?>, AbstractTransferable.TransferDataTransformer>();

	/**
	 * Creates a transferable object capable of transferring the specified
	 * string in plain text format.
	 */
	public AbstractTransferable() {
		initClipboardData();

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				TextData d = (TextData) data;
				return new TransferData(d.getId(), d.getX(), d.getY(), Type.Text, d.getName(), d.getText(), 0, 0, null);
			}
		}, TextData.class);

		registerTransformer(new TransferDataTransformer() {

			@Override
			public TransferData transform(SelectableElement<?> s, Object data) {
				SimulationGlobalData d = (SimulationGlobalData) data;
				return new TransferData(d.getId(), d.getX(), d.getY(), Type.Text, d.getName(), d.getFormula(), 0, 0, null);
			}
		}, SimulationGlobalData.class);
	}

	protected void registerTransformer(TransferDataTransformer transformer, Class<?> clazz) {
		transformers.put(clazz, transformer);
	}

	/**
	 * Adds all data of the selected elements to the Transferable
	 */
	protected void addCopy(SelectableElement<?>[] selected) {
		for (SelectableElement<?> s : selected) {
			if (s.getData() == null) {
				continue;
			}
			if (!addCopy(s, s.getData())) {
				throw new RuntimeException("Class not handled in copy / paste: " + s.getClass().getName());
			}
		}
	}

	/**
	 * Initialies the clipboard Data element
	 */
	public abstract void initClipboardData();

	/**
	 * Creates the real copy of the element
	 * 
	 * @param data
	 */
	protected boolean addCopy(SelectableElement<?> s, Object data) {
		TransferDataTransformer transformer = transformers.get(data.getClass());
		if (transformer == null) {
			return false;
		}

		this.data.add(transformer.transform(s, data));

		return true;
	}

	/**
	 * Returns the array of flavors in which it can provide the data.
	 */
	@Override
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
	@Override
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(flavors[0])) {
			return getClipboardData();
		} else if (flavor.equals(flavors[1])) {
			return exportedImage;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
