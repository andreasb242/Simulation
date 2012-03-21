package ch.zhaw.simulation.window.xy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.xy.SubmodelHandler;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.menu.xy.XYMenubar;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.toolbar.xy.XYToolbar;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.xy.sidebar.DensitySidebar;
import ch.zhaw.simulation.window.xy.sidebar.XYFormulaConfiguration;

public class XYWindow extends SimulationWindow<XYMenubar, XYToolbar, XYEditorView> {
	private static final long serialVersionUID = 1L;

	private DensitySidebar densitySidebar;

	private ActionListener densityListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			getView().updateDensity(densitySidebar.getSelected().getFormula());
		}

	};

	public XYWindow() {
		super(true);
	}

	public void init(XYEditorControl control) {
		XYEditorView view = new XYEditorView(control, new TransferableFactory() {

			@Override
			public AbstractTransferable createTransferable(SelectableElement[] selected) {
				// TODO Copy Paste XY
				return null;
			}
		});

		densitySidebar = new DensitySidebar(control.getParent(), control.getModel(), view, control.getSysintegration(), control.getApp().getFunctionHelp());

		densitySidebar.addActionListener(densityListener);

		control.setView(view);

		XYMenubar menubar = new XYMenubar(control.getSysintegration(), um, control.getClipboard());
		menubar.initMenusToolbar(control.getRecentMenu(), mainWindow);

		XYToolbar tb = new XYToolbar(control.getSysintegration(), mainWindow);
		tb.initToolbar(view.getUndoHandler());

		init(menubar, tb, view);

		SubmodelHandler submodelhandler = control.getSubmodelHandler();
		submodelhandler.addSubModelSelectionListener(tb);
		submodelhandler.addSubModelSelectionListener(getView());
	}

	@Override
	protected void initSidebar(FrameSidebar sidebar) {
		super.initSidebar(sidebar);

		sidebar.add(densitySidebar);
	}

	protected void initElementConfigurationSiebar() {
		final AbstractEditorControl<?> control = view.getControl();

		formulaConfiguration = new XYFormulaConfiguration((SimulationXYModel) control.getModel(), control.getSelectionModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void showFormulaEditor(AbstractNamedSimulationData data) {
				control.showFormulaEditor(getData());
			}

		};
	}

	@Override
	public void dispose() {
		if (densityListener != null) {
			densitySidebar.dispose();
			densitySidebar.removeActionListener(densityListener);
			densityListener = null;
		}

		if (getView() != null) {
			SubmodelHandler submodelhandler = getView().getControl().getSubmodelHandler();
			submodelhandler.removeSubModelSelectionListener(getView());
			submodelhandler.removeSubModelSelectionListener(getToolbar());
		}

		super.dispose();
	}
}
