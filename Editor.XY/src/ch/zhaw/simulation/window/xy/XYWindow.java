package ch.zhaw.simulation.window.xy;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.menu.xy.XYMenubar;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.toolbar.xy.XYToolbar;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.sidebar.NameFormulaConfiguration;
import ch.zhaw.simulation.window.xy.sidebar.DensitySidebar;
import ch.zhaw.simulation.window.xy.sidebar.XYFormulaConfiguration;

public class XYWindow extends SimulationWindow<XYMenubar, XYToolbar, XYEditorView> {
	private static final long serialVersionUID = 1L;

	private DensitySidebar densitySidebar;

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

		densitySidebar = new DensitySidebar(control.getParent(), view.getDensity(), control.getModel(), view);

		control.setView(view);

		XYMenubar menubar = new XYMenubar(control.getSysintegration(), um, control.getClipboard());
		menubar.initMenusToolbar(control.getRecentMenu(), mainWindow);

		XYToolbar tb = new XYToolbar(control.getSysintegration(), mainWindow);
		tb.initToolbar(view.getUndoHandler());

		init(menubar, tb, view);
	}

	@Override
	protected void initSidebar(FrameSidebar sidebar) {
		super.initSidebar(sidebar);

		sidebar.add(densitySidebar);
	}

	protected void initElementConfigurationSiebar() {
		final AbstractEditorControl<?> control = view.getControl();

		formulaConfiguration = new XYFormulaConfiguration(control.getModel(), control.getSelectionModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void showFormulaEditor(AbstractNamedSimulationData data) {
				control.showFormulaEditor(getData());
			}

		};
	}

	@Override
	public void dispose() {
		densitySidebar.dispose();
		super.dispose();
	}
}
