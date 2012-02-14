package sun.net.www.protocol.man;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HelpUrlConnection extends URLConnection {
	private StringBuilder contents = new StringBuilder();
	private String urlPrefix = new File(".").getAbsolutePath() + "/help/";
	private boolean loaded = false;

	protected HelpUrlConnection(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {
		if (loaded) {
			return;
		}
		loaded = true;

		readHtml("header.tpl");

		if("java".equals(url.getHost())) {
			DynamicHelp.request(url.getPath(), contents);
		} else {
			readHtml(url.getHost() + "/" + url.getPath());
		}
		
		readHtml("footer.tpl");
	}

	private void readHtml(String file) throws IOException {
		FileReader in = new FileReader(urlPrefix + file);

		BufferedReader r = new BufferedReader(in);

		String line;

		do {
			line = r.readLine();
			if (line != null) {
				contents.append(line);
			}
		} while (line != null);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		connect();
		String str = contents.toString();
		return new ByteArrayInputStream(str.getBytes());
	}

	@Override
	public int getContentLength() {
		return contents.length();
	}

	@Override
	public String getContentType() {
		return "text/html";
	}

	@Override
	public String getContentEncoding() {
		return "UTF-8";
	}
}
