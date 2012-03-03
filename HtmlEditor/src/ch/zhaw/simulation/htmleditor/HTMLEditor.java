package ch.zhaw.simulation.htmleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

import org.jdesktop.swingx.action.ActionManager;
import org.jdesktop.swingx.action.TargetManager;
import org.jdesktop.swingx.action.TargetableAction;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.SysMenuShortcuts;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;

/**
 * Stellt ein HTML Editor dar.
 * 
 * @author Andreas Butti
 * 
 */
public class HTMLEditor extends BDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Zum laden und Speichern von HTML Dateien
	 */
	private InternalHTMLEditorKit htmlEditorKit = new InternalHTMLEditorKit();

	/**
	 * Der Editor
	 */
	private JXEditorPane editorPane;

	/**
	 * Die Toolbar
	 */
	private Toolbar toolBar;

	/**
	 * Die Menubar
	 */
	private JMenuBar menubar = new JMenuBar();

	/**
	 * Dokument Menu
	 */
	private JMenu mDocument = new JMenu("Dokument");

	/**
	 * Bearbeiten Menü
	 */
	private JMenu mEdit = new JMenu("Bearbeiten");

	/**
	 * Format Menü
	 */
	private JMenu mFormat = new JMenu("Format");

	/**
	 * Systemintegration
	 */
	private Sysintegration integration;

	/**
	 * Die Einstellungen
	 */
	private Settings settings;

	/**
	 * Das HTML Dokument
	 */
	private HtmlDocumentFile document = new HtmlDocumentFile();

	/**
	 * Ob das Dokument gespeichert oder verworfen wurde
	 */
	private boolean saved;

	/**
	 * Erstellt einen Editor
	 */
	public HTMLEditor(JFrame parent, Sysintegration integration, Settings settings) {
		super(parent);

		setModal(true);
		saved = true;

		setTitle("Texteditor");

		this.integration = integration;
		toolBar = integration.createToolbar();

		this.settings = settings;

		initEditorMenu();
		initEditMenu();
		initFormatMenu();

		initComponents();

		TargetManager targetManager = TargetManager.getInstance();
		targetManager.addTarget(editorPane);

		setJMenuBar(menubar);

		pack();
		setSize(getWidth(), 300);

		setLocationRelativeTo(parent);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				save();
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b) {
			saved = true;
			editorPane.requestFocus();
		}
	}

	public boolean isSaved() {
		return saved;
	}

	/**
	 * Editmenu initialiseren
	 */
	private void initEditMenu() {
		menubar.add(mEdit);

		mEdit.setMnemonic('B');

		SysMenuShortcuts s = integration.getMenu();

		addAction(mEdit, "Ausschneiden", "cut", "edit-cut", s.getEditCut());
		addAction(mEdit, "Kopieren", "copy", "editcopy", s.getEditCopy());
		addAction(mEdit, "Einfügen", "paste", "editpaste", s.getEditPaste());

		mEdit.addSeparator();
		toolBar.addSeparator();

		addStateAction(mEdit, "Rückgängig", "undo", "edit-undo", s.getEditUndo());
		addStateAction(mEdit, "Widerherstellen", "redo", "edit-redo", s.getEditRedo());

		mEdit.addSeparator();
		toolBar.addSeparator();

		addAction(mEdit, "Suchen", "find", "search", s.getEditSearch(), false);
	}

	private void initEditorMenu() {
		menubar.add(mDocument);

		mDocument.setMnemonic('E');

		JMenuItem itSaveAs = new JMenuItem("Speichern unter");
		itSaveAs.setAccelerator(integration.getMenu().getFileSaveAs());
		itSaveAs.setIcon(IconSVG.getIcon("save"));

		itSaveAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}

		});

		mDocument.add(itSaveAs);
		mDocument.addSeparator();

		JMenuItem itClose = new JMenuItem("Schliessen");
		itClose.setIcon(IconSVG.getIcon("close"));
		itClose.setAccelerator(integration.getMenu().getFileClose());
		mDocument.add(itClose);
		itClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				setVisible(false);
			}
		});

		JMenuItem itCloseWithoutSave = new JMenuItem("Änderungen verwerfen");
		itCloseWithoutSave.setIcon(IconSVG.getIcon("edit-delete"));
		mDocument.add(itCloseWithoutSave);

		itCloseWithoutSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				saved = false;
			}
		});
	}

	protected void saveAs() {
		String lastSavePath = settings.getSetting("texteditor.lastSavePath");
		File file = integration.showSaveDialog(this, htmlFileFilter, lastSavePath);

		if (file == null) {
			return;
		}

		settings.setSetting("texteditor.lastSavePath", file.getParent());

		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			Document doc = editorPane.getDocument();
			htmlEditorKit.write(out, doc, 0, doc.getLength());
			out.close();
		} catch (Exception e) {
			Errorhandler.showError(e, "Fehler beim export");
		}
	}

	/**
	 * Formatmenu initialiseren
	 */
	private void initFormatMenu() {
		menubar.add(mFormat);

		mFormat.setMnemonic('F');

		addAction(mFormat, "Linksbündig", "left-justify", "text_left.png", null).setGroup("alignment");
		addAction(mFormat, "Zentriert", "center-justify", "text_center.png", null).setGroup("alignment");
		addAction(mFormat, "Rechtsbündig", "right-justify", "text_right.png", null).setGroup("alignment");

		mEdit.addSeparator();
		toolBar.addSeparator();

		addToogleAction(mFormat, "Fett", "font-bold", "text_bold.png");
		addToogleAction(mFormat, "Kursiv", "font-italic", "text_italic.png");
		addToogleAction(mFormat, "Unterstrichen", "font-underline", "text_under.png");

		mEdit.addSeparator();
		toolBar.addSeparator();
	}

	/**
	 * Eine Action hinzufügen
	 * 
	 * @param menu
	 *            Das Menu
	 * @param name
	 *            Der Name des Eintrages
	 * @param actionName
	 *            Die Aktion die ausgeführt wird
	 * @param iconName
	 *            Der Name des Icons
	 * @return Die hinzugefügte Action
	 */
	private TargetableAction addToogleAction(JMenu menu, String name, String actionName, String iconName) {
		Icon icon;

		if (iconName.endsWith(".png")) {
			icon = loadIcon(iconName);
		} else {
			icon = IconSVG.getIcon(iconName, 24);
		}

		ActionManager manager = ActionManager.getInstance();

		final TargetableAction action = new TargetableAction(name, actionName, icon);
		manager.addAction(action);
		action.setStateAction(true);

		toolBar.addToogleAction(action);

		return action;
	}

	/**
	 * Eine Action hinzufügen
	 * 
	 * @param menu
	 *            Das Menu
	 * @param name
	 *            Der Name des Eintrages
	 * @param actionName
	 *            Die Aktion die ausgeführt wird
	 * @param iconName
	 *            Der Name des Icons
	 * @param keyStroke
	 *            KeyCommand
	 * @return Die hinzugefügte Action
	 */
	private TargetableAction addStateAction(JMenu menu, String name, String actionName, String iconName, KeyStroke keyStroke) {
		Icon icon;

		if (iconName.endsWith(".png")) {
			icon = loadIcon(iconName);
		} else {
			icon = IconSVG.getIcon(iconName, 24);
		}

		ActionManager manager = ActionManager.getInstance();

		final TargetableAction action = new TargetableAction(name, actionName, icon);
		action.setAccelerator(keyStroke);
		manager.addAction(action);

		toolBar.add(action);

		return action;
	}

	/**
	 * Eine Action hinzufügen
	 * 
	 * @param menu
	 *            Das Menu
	 * @param name
	 *            Der Name des Eintrages
	 * @param actionName
	 *            Die Aktion die ausgeführt wird
	 * @param iconName
	 *            Der Name des Icons
	 * @param addToToolbar
	 *            Ob die Aktion in der Toolbar angezeigt werden soll
	 * @return Die hinzugefügte Action
	 */
	private TargetableAction addAction(JMenu menu, String name, String actionName, String iconName, KeyStroke keyStroke, boolean addToToolbar) {
		Icon icon;

		if (iconName.endsWith(".png")) {
			icon = loadIcon(iconName);
		} else {
			icon = IconSVG.getIcon(iconName, 24);
		}

		final TargetableAction action = new TargetableAction(name, actionName, icon);
		menu.add(action).setAccelerator(keyStroke);

		if (addToToolbar) {
			toolBar.add(new ToolbarAction(name, icon) {

				@Override
				protected void actionPerformed(ActionEvent e) {
					action.actionPerformed(e);
				}
			});
		}

		return action;
	}

	/**
	 * Eine Action hinzufügen
	 * 
	 * @param menu
	 *            Das Menu
	 * @param name
	 *            Der Name des Eintrages
	 * @param actionName
	 *            Die Aktion die ausgeführt wird
	 * @param iconName
	 *            Der Name des Icons
	 * @return Die hinzugefügte Action
	 */
	private TargetableAction addAction(JMenu menu, String name, String actionName, String iconName, KeyStroke keyStroke) {
		return addAction(menu, name, actionName, iconName, keyStroke, true);
	}

	/**
	 * Lädt ein Icon
	 * 
	 * @param file
	 *            Der Dateinamen
	 * @return Das Icon
	 */
	private Icon loadIcon(String file) {
		return new ImageIcon(getClass().getResource("icons/" + file));
	}

	private void save() {
		normalizeImageIds();
		document.setHtml(getContents());
	}

	private void normalizeImageIds() {
		Document doc = editorPane.getDocument();

		for (Element e : doc.getRootElements()) {
			handleTag(e);
		}
	}

	private void handleTag(Element elem) {
		if ("img".equals(elem.getName())) {
			System.out.println("handle img");
			// TODO: image element editor
		}

		for (int i = 0; i < elem.getElementCount(); i++) {
			Element e = elem.getElement(i);
			handleTag(e);
		}
	}

	public HtmlDocumentFile getDocument() {
		return document;
	}

	/**
	 * Initialisiert die GUI
	 */
	private void initComponents() {
		editorPane = new JXEditorPane("text/html", "") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
		};

		editorPane.setEditorKit(htmlEditorKit);
		editorPane.setBackground(Color.WHITE);

		setLayout(new BorderLayout());

		add(toolBar.getComponent(), BorderLayout.NORTH);

		add(editorPane, BorderLayout.CENTER);
	}

	/**
	 * Setzt den Inhalt
	 * 
	 * @param html
	 *            String
	 */
	public void setContents(final String html) {
		try {
			editorPane.setDocument(new HTMLDocument());

			htmlEditorKit.read(new StringReader(html), editorPane.getDocument(), 0);

			editorPane.resetUndoHandler();
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
	}

	/**
	 * Gibt den Inhalt des WYSIWYG Editors zurück
	 * 
	 * @return HTML
	 */
	private String getEditorContents() {
		HTMLDocument doc = (HTMLDocument) editorPane.getDocument();

		StringWriter textOut = new StringWriter();
		HTMLWriter w = new HTMLWriter(textOut, doc, 0, doc.getLength());
		try {
			w.write();
		} catch (Exception e) {
			Errorhandler.showError(e, "Fehler bei der HTML code Generierung");
		}
		return textOut.toString();

	}

	/**
	 * Gibt den Inhalt als HTML String zurück
	 */
	public String getContents() {
		return getEditorContents();
		// return trimHtml(getEditorContents());
	}

	private static SimFileFilter htmlFileFilter = new SimFileFilter() {

		@Override
		public String getDescription() {
			return "HTML Dokument";
		}

		@Override
		public boolean accept(File f) {
			return (f.isDirectory() || f.getName().endsWith(".html"));
		}

		@Override
		public String getExtension() {
			return ".html";
		}
	};

}