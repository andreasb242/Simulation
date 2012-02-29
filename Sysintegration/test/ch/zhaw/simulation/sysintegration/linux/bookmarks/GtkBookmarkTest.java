package ch.zhaw.simulation.sysintegration.linux.bookmarks;

public class GtkBookmarkTest {

	public static void main(String[] args) {
		GtkBookmarks bm = new GtkBookmarks();

		for (int i = 0; i < bm.getSize(); i++) {
			System.out.println("->" + bm.getElementAt(i));
		}

	}

}
