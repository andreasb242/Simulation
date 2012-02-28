package ch.zhaw.simulation.window.xy;

import ch.zhaw.simulation.clipboard.AbstractTransferable;
import ch.zhaw.simulation.clipboard.TransferableFactory;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditorView;
import ch.zhaw.simulation.frame.sidebar.FrameSidebar;
import ch.zhaw.simulation.menu.xy.XYMenubar;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.toolbar.xy.XYToolbar;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.xy.sidebar.DensitySidebar;

public class XYWindow extends SimulationWindow<XYMenubar, XYToolbar, XYEditorView> {
	private static final long serialVersionUID = 1L;

	private DensitySidebar densitySidebar = new DensitySidebar();

	public XYWindow() {
		super(true);
	}

	public void init(XYEditorControl control) {
		XYEditorView view = new XYEditorView(control, new TransferableFactory() {

			@Override
			public AbstractTransferable createTransferable(SelectableElement[] selected) {
				// TODO Auto-generated method stub
				return null;
			}
		});

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

}
