package ch.zhaw.simulation.gui.codeditor.library;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import butti.javalibs.listener.ListenerList;

import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.math.Parser;

public class FormulaLibraryPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private JLabel lbDescription = new JLabel();

	private ListenerList listener = new ListenerList();

	public FormulaLibraryPanel() {
		setLayout(new BorderLayout());
		tree = new JTree();

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);

		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object n = e.getPath().getLastPathComponent();
				if (n instanceof LibraryFunctionNode) {
					Function fnc = ((LibraryFunctionNode) n).getFunction();
					String cls = fnc.getFunctionClass().getName();
					System.out.println(cls);

					lbDescription.setText("<html>" + fnc.getDescription() + "</html>");
				} else if (n instanceof LibraryConstNode) {
					Constant c = ((LibraryConstNode) n).getConst();
					lbDescription.setText(c.description);
				} else {
					lbDescription.setText(" ");
				}
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int pos = 0;
					String txt = null;

					TreePath p = tree.getClosestPathForLocation(e.getX(), e.getY());
					if (p == null) {
						return;
					}
					Object n = p.getLastPathComponent();
					if (n instanceof LibraryFunctionNode) {
						Function fnc = ((LibraryFunctionNode) n).getFunction();

						txt = fnc.getName() + "()";
						pos = -1;
					} else if (n instanceof LibraryConstNode) {
						Constant c = ((LibraryConstNode) n).getConst();

						txt = c.name;
					}

					if (txt != null) {
						listener.actionPerformed(new ActionEvent(this, pos, txt));
					}
				}
			}
		});

		add(lbDescription, BorderLayout.SOUTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void addActionListener(ActionListener l) {
		listener.addListener(l);
	}

	public void removeActionListener(ActionListener l) {
		listener.removeListener(l);
	}

	public void setParser(Parser parser) {
		tree.setModel(new DefaultTreeModel(new LibraryRootNode(parser)));
	}
}
