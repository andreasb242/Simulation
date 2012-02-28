package ch.zhaw.simulation.control.flow;

import java.util.Vector;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.app.SimulationApplication;
import ch.zhaw.simulation.dialog.overview.OverviewWindow;
import ch.zhaw.simulation.dialog.snapshot.SnapshotDialog;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.GuiDataElement;
import ch.zhaw.simulation.editor.elements.global.GlobalView;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorParameter;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.ConnectorPoint;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.InfiniteSymbol;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerView;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterView;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.flow.gui.FlowEditorView;
import ch.zhaw.simulation.math.Autoparser;
import ch.zhaw.simulation.menutoolbar.actions.MenuToolbarAction;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionListener;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.undo.action.flow.AddConnectorUndoAction;
import ch.zhaw.simulation.undo.action.flow.DeleteUndoAction;
import ch.zhaw.simulation.window.flow.FlowWindow;

public class FlowEditorControl extends AbstractEditorControl<SimulationFlowModel> {

	private FlowEditorView view;

	private Vector<DrawModusListener> drawModusListener = new Vector<DrawModusListener>();

	private Autoparser autoparser;

	public FlowEditorControl(SimulationApplication app, SimulationFlowModel model, SimulationDocument doc, FlowWindow parent,
			Settings settings) {
		super(parent, settings, app, doc, model);


		addListeners();

		autoparser = new Autoparser(this);
	}


	public void stopAutoparser() {
		autoparser.stop();
	}

	public void startAutoparser() {
		autoparser.start();
	}

	@Override
	protected void delete(SelectableElement[] elements) {
		Vector<NamedSimulationObject> removedObjects = new Vector<NamedSimulationObject>();
		Vector<Connector<?>> removedConnectors = new Vector<Connector<?>>();
		Vector<InfiniteData> removedInfinite = new Vector<InfiniteData>();

		Vector<Connector<?>> tmpRemovedConnectors = new Vector<Connector<?>>();
		for (SelectableElement el : elements) {
			if (el instanceof FlowConnectorParameter) {
				FlowConnector c = ((FlowConnectorParameter) el).getConnector();

				tmpRemovedConnectors.add(c);

				addConnectors(removedConnectors, removedInfinite, model.getConnectorsTo(c.getValve()));
			} else if (el instanceof ConnectorPoint) {
				tmpRemovedConnectors.add(((ConnectorPoint) el).getConnector());
			}
		}

		addConnectors(removedConnectors, removedInfinite, tmpRemovedConnectors);

		for (SelectableElement el : elements) {
			if (el instanceof ParameterView || el instanceof ContainerView) {
				GuiDataTextElement<?> control = (GuiDataTextElement<?>) el;
				NamedSimulationObject data = (NamedSimulationObject) control.getData();

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

	private void addConnectors(Vector<Connector<?>> removedConnectors, Vector<InfiniteData> removedInfinite, Vector<Connector<?>> add) {
		for (Connector<?> c : add) {
			if (!removedConnectors.contains(c)) {
				removedConnectors.add(c);

				if (c instanceof FlowConnector) {
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
			public void dataRemoved(SimulationObject o) {
				clearStatus();
			}

			@Override
			public void dataChanged(SimulationObject o) {
				clearStatus();
			}

			@Override
			public void dataAdded(SimulationObject o) {
				clearStatus();
			}

			@Override
			public void connectorChanged(Connector<?> c) {
				clearStatus();
			}

			@Override
			public void connectorRemoved(Connector<?> c) {
				clearStatus();
			}

			@Override
			public void connectorAdded(Connector<?> c) {
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

				Vector<SimulationObject> shadowData = new Vector<SimulationObject>();

				if (elem instanceof GlobalView) {
					SimulationGlobal g = ((GlobalView) elem).getData();

					for (SimulationObject d : model.getData()) {
						Vector<SimulationGlobal> usedGlobals = d.getUsedGlobals();
						if (usedGlobals != null && usedGlobals.contains(g)) {
							shadowData.add(d);
						}
					}
				}

				if (elem instanceof GuiDataElement<?>) {
					SimulationObject d = ((GuiDataElement<?>) elem).getData();

					Vector<SimulationGlobal> globals = d.getUsedGlobals();
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

	public void addConnector(Connector<?> c) {
		getUndoManager().addEdit(new AddConnectorUndoAction(c, model));
	}

	public void addParameter() {
		cancelAllActions();
		addComponent(new SimulationParameter(0, 0), "Parameter");
	}

	public void addContainer() {
		cancelAllActions();
		addComponent(new SimulationContainer(0, 0), "Container");
	}

	public void takeSnapshot() {
		SnapshotDialog dlg = new SnapshotDialog(getParent(), getSysintegration(), view, view.getBounds());
		dlg.setVisible(true);
	}

	@Override
	public boolean menuActionPerformedOverwrite(MenuToolbarAction action) {

		switch (action.getType()) {
		case FLOW_ADD_CONTAINER:
			addContainer();
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

		case SNAPSHOT:
			takeSnapshot();
			return true;

		}

		return false;
	}

	public void setView(FlowEditorView view) {
		this.view = view;
	}
}
