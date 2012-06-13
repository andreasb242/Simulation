package ch.zhaw.simulation.filechoosertextfield;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import butti.javalibs.listener.ListenerList;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class FilechooserTextfield extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextField txtPath = new JTextField();
	private JButton btSelect = new JButton("...");

	private ListenerList changelistener = new ListenerList();

	/**
	 * @param parent
	 *            The parent Window for showing dialogs
	 * @param sys
	 *            The Sysintegration
	 * @param filefilter
	 *            For selection Files, not used for folders!
	 * @param showOnlyFolders
	 *            Show folders instead of files
	 * @param showHidden
	 *            Show hidden files / folders
	 */
	public FilechooserTextfield(final Window parent, final Sysintegration sys, final SimFileFilter filefilter, final boolean showOnlyFolders,
			final boolean showHidden, boolean editable) {
		setLayout(new LayoutManager() {

			@Override
			public void removeLayoutComponent(Component comp) {
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				Dimension pt = txtPath.getPreferredSize();
				Dimension pb = btSelect.getPreferredSize();
				return new Dimension(pt.width + pb.width + 5, Math.max(pt.height, pb.height));
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				Dimension pt = txtPath.getPreferredSize();
				Dimension pb = btSelect.getPreferredSize();
				return new Dimension(pb.width + 55, Math.max(pt.height, pb.height));
			}

			@Override
			public void layoutContainer(Container parent) {
				int btWidth = btSelect.getPreferredSize().width;
				int w = getWidth() - btWidth;
				txtPath.setBounds(0, 0, w - 5, getHeight());
				btSelect.setBounds(w, 0, btWidth, getHeight());
			}

			@Override
			public void addLayoutComponent(String name, Component comp) {
			}
		});

		add(txtPath);
		add(btSelect);

		txtPath.setEditable(editable);
		btSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (showOnlyFolders) {
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (!txtPath.getText().isEmpty()) {
						fc.setSelectedFile(new File(txtPath.getText()));
					}
					if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
						txtPath.setText(fc.getSelectedFile().getAbsolutePath());
						firePathChanged(fc.getSelectedFile().getAbsolutePath());
					}

				} else {
					File file = sys.showOpenDialog(parent, filefilter, txtPath.getText());
					if (file != null) {
						txtPath.setText(file.getAbsolutePath());
						firePathChanged(file.getAbsolutePath());
					}
				}
			}
		});
	}

	public String getPath() {
		return txtPath.getText();
	}

	public void setPath(String path) {
		txtPath.setText(path);
	}

	public void addChangeListener(ActionListener l) {
		changelistener.addListener(l);
	}

	public void removeChangeListener(ActionListener l) {
		changelistener.removeListener(l);
	}

	private void firePathChanged(String path) {
		ActionEvent e = new ActionEvent(this, 1, path);
		this.changelistener.actionPerformed(e);
	}

}
