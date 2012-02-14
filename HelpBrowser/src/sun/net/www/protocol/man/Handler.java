package sun.net.www.protocol.man;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Implementierung für das "man://" Protokoll (Hilfeprotokoll)
 * 
 * @author Andreas Butti
 */
public class Handler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		if (!url.getPath().endsWith(".html")) {
			URL u = new URL("file://" + new File(".").getAbsolutePath() + "/help/" + url.getPath());
			return u.openConnection();
		}

		return new HelpUrlConnection(url);
	}
}
