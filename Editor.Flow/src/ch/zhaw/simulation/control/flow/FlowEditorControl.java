package ch.zhaw.simulation.control.flow;

import java.util.Vector;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.dialog.overview.OverviewWindow;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.editor.elements.global.GlobalView;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.BezierHelperPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.flow.elements.valve.FlowValveElement;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.flow.gui.FlowEditorView;
import ch.zhaw.simulation.math.Autoparser;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarActionType;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.undo.action.flow.AddConnectorUndoAction;
import ch.zhaw.simulation.undo.action.flow.DeleteUndoAction;
import ch.zhaw.simulation.window.flow.FlowWindow;

public class FlowEditorControl extends AbstractEditorControl<SimulationFlowModel> {

	private FlowEditorView view;

	private Vector<DrawModusListener> drawModusListener = new Vector<DrawModusListener>();

	private Autoparser autoparser;

	public FlowEditorControl(SimulationApplication app, SimulationFlowModel model, SimulationDocument doc, FlowWindow parent, Settings settings) {
		super(parent, settings, app, doc, model);

		addListeners();

		autoparser = new Autoparser(this);

		doc.addListener(this);
		
		startAutoparser();
	}

	@Override
	public void stopAutoparser() {
		autoparser.stop();
		System.out.println("stop autoparser");
	}

	@Override
	public void startAutoparser() {
		autoparser.start();
		System.out.println("start autoparser");
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		Vector<AbstractNamedSimulationData> removedObjects = new Vector<AbstractNamedSimulationData>();
		Vector<AbstractConnectorData<?>> removedConnectors = new Vector<AbstractConnectorData<?>>();
		Vector<InfiniteData> removedInfinite = new Vector<InfiniteData>();

		Vector<AbstractConnectorData<?>> tmpRemovedConnectors = new Vector<AbstractConnectorData<?>>();
		for (SelectableElement el : elements) {
			if (el instanceof FlowValveElement) {
				FlowConnectorData c = ((FlowValveElement) el).getConnector();

				tmpRemovedConnectors.add(c);

				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(c.getValve()));
			} else if (el instanceof BezierHelperPoint) {

				// TODO !!!!!!!!!!

				// tmpRemovedConnectors.add(((BezierHelperPoint)
				// el).getConnector());
			}
		}

		addConnectors(removedConnectors, removedInfinite, tmpRemovedConnectors);

		for (SelectableElement el : elements) {
			if (el instanceof ParameterView || el instanceof ContainerView) {
				GuiDataTextElement<?> control = (GuiDataTextElement<?>) el;
				AbstractNamedSimulationData data = (AbstractNamedSimulationData) control.getData();

				removedObjects.add(data);
				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(data));
			}
		}
		for (SelectableElement el : elements) {
			if (el instanceof InfiniteSymbol) {
				addInfiniteData(removedInfinite, ((InfiniteSymbol) el).getData());
				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(((InfiniteSymbol) el).getData()));
			}
		}

		getUndoManager().addEdit(new DeleteUndoAction(removedObjects, removedConnectors, removedInfinite, this));
	}

	private void addInfiniteData(Vector<InfiniteData> removedInfinite, InfiniteData d) {
		if (!removedInfinite.contains(d)) {
			removedInfinite.add(d);
		}
	}

	private void addConnectors(Vector<AbstractConnectorData<?>> removedConnectors, Vector<InfiniteData> removedInfinite, Vector<AbstractConnectorData<?>> add) {
		for (AbstractConnectorData<?> c : add) {
			if (!removedConnectors.contains(c)) {
				removedConnectors.add(c);

				if (c instanceof FlowConnectorData) {
					if (c.getSource() instanceof InfiniteData) {
						addInfiniteData(removedInfinite, (InfiniteData) c.getSource());
					}
					if (c.getTarget() instanceof InfiniteData) {
						addInfiniteData(removedInfinite, (InfiniteData) c.getTarget());
					}
				}
			}
		}
	}

	private void addListeners() {
		model.addListener(new FlowSimulationAdapter() {

			@Override
			public void dataRemoved(AbstractSimulationData o) {
				clearStatus();
			}

			@Override
			public void dataChanged(AbstractSimulationData o) {
				clearStatus();
			}

			@Override
			public void dataAdded(AbstractSimulationData o) {
				clearStatus();
			}

			@Override
			public void connectorChanged(AbstractConnectorData<?> c) {
				clearStatus();
			}

			@Override
			public void connectorRemoved(AbstractConnectorData<?> c) {
				clearStatus();
			}

			@Override
			public void connectorAdded(AbstractConnectorData<?> c) {
				clearStatus();
			}
		});

		// Muss als erstes aufgerufen werden => als erstes hinzufügen
		selectionModel.addSelectionListener(new SelectionListener() {
			@Override
			public void selectionMoved(int dX, int dY) {
			}

			@Override
			public void selectionChanged() {
				SelectableElement elem = selectionModel.getSingleSelectedElement();
				if (elem == null) {
					selectionModel.clearDependentElement();
					return;
				}

				Vector<AbstractSimulationData> shadowData = new Vector<AbstractSimulationData>();

				if (elem instanceof GlobalView) {
					SimulationGlobalData g = ((GlobalView) elem).getData();

					for (AbstractSimulationData d : model.getData()) {
						Vector<SimulationGlobalData> usedGlobals = d.getUsedGlobals();
						if (usedGlobals != null && usedGlobals.contains(g)) {
							shadowData.add(d);
						}
					}
				}

				if (elem instanceof AbstractDataView<?>) {
					AbstractSimulationData d = ((AbstractDataView<?>) elem).getData();

					Vector<SimulationGlobalData> globals = d.getUsedGlobals();
					if (globals != null) {
						shadowData.addAll(globals);
					}
				}

				Vector<SelectableElement> elements = view.convertToSelectable(shadowData);

				selectionModel.setDependentElement(elements);
			}
		});
	}

	@Override
	protected void cancelAllActions() {
		super.cancelAllActions();
		view.cancelAddArrow();
	}

	public FlowEditorView getView() {
		return view;
	}

	public void fireDrawModusFinished() {
		for (DrawModusListener l : drawModusListener) {
			l.drawModusFinished();
		}
	}

	public void fireDrawModusEnabled() {
		for (DrawModusListener l : drawModusListener) {
			l.drawModusEnabled();
		}
	}

	public void addDrawModusListener(DrawModusListener l) {
		drawModusListener.add(l);
	}

	public void removeDrawModusListener(DrawModusListener l) {
		drawModusListener.remove(l);
	}

	public void addConnector(AbstractConnectorData<?> c) {
		getUndoManager().addEdit(new AddConnectorUndoAction(c, model));
	}

	public void addParameter() {
		cancelAllActions();
		addComponent(new SimulationParameterData(0, 0), "Parameter");
	}

	public void addContainer() {
		cancelAllActions();
		addComponent(new SimulationContainerData(0, 0), "Container");
	}

	public void addDensity() {
		cancelAllActions();
		addComponent(new SimulationDensityContainerData(0, 0), "Container");
	}

	@Override
	public boolean menuActionPerformedOverwrite(MenuToolbarAction action) {

		switch (action.getType()) {
		case FLOW_ADD_CONTAINER:
			addContainer();
			return true;

		case FLOW_ADD_DENSITY:
			addDensity();
			return true;

		case FLOW_ADD_PARAMETER:
			addParameter();
			return true;

		case FLOW_ADD_CONNECTOR:
			cancelAllActions();
			getSelectionModel().clearSelection();
			getView().addConnectArrow();
			return true;

		case FLOW_ADD_FLOW:
			cancelAllActions();
			getSelectionModel().clearSelection();
			getView().addFlowArrow();
			return true;

		case FORMULA_OVERVIEW:
			OverviewWindow w = new OverviewWindow(getParent(), getClipboard(), getModel(), getSysintegration().getGuiConfig());
			w.setVisible(true);
			return true;

		case CLOSE:
			// TODO DEBUG
			System.err.println("CLOSE => EXIT !!! DEBUG !!!");
			menuActionPerformed(new MenuToolbarAction(MenuToolbarActionType.EXIT));
			return true;
		}

		return false;
	}

	public void setView(FlowEditorView view) {
		this.view = view;
	}

	@Override
	public void dispose() {
		this.doc.removeListener(this);

		super.dispose();

		this.autoparser.stop();
		this.autoparser = null;

		this.drawModusListener.clear();

		this.view = null;
	}

}
