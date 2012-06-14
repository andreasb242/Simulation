package ch.zhaw.simulation.ant;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MacInfoPlist extends Task {
	private String buildpath = "";
	private String basedir = "";
	private String targetbase = "";
	private String target = "";
	private String input = "";
	private String additional = "";
	private String workdir = "";
	private String splash = "";
	private String libpath = "";

	public void execute() throws BuildException {
		Vector<String> buildPath = new Vector<String>();

		for (String a : additional.split(":")) {
			buildPath.add(a);
		}

		for (String b : buildpath.split(":")) {
			addBuildPath(buildPath, b, basedir, targetbase);
		}

		String file = fileGetContents(new File(input));

		StringBuilder b = new StringBuilder();
		for (String c : buildPath) {
			b.append("\t\t\t<string>" + c + "</string>\n");
		}

		if (b.length() > 0) {
			file = replace(file, "{CLASSPATH}", b.substring(1));
		} else {
			file = replace(file, "{CLASSPATH}", "");
		}

		file = replace(file, "{WORKINGDIRECTORY}", this.workdir);
		file = replace(file, "{SPLASH}", this.splash);
		file = replace(file, "{LIBPATH}", this.libpath);

		
		filePutContents(target, file);
	}

	public static String replace(final String aInput, final String aOldPattern, final String aNewPattern) {
		if (aOldPattern.equals("")) {
			throw new IllegalArgumentException("Old pattern must have content.");
		}

		final StringBuffer result = new StringBuffer();
		// startIdx and idxOld delimit various chunks of aInput; these
		// chunks always end where aOldPattern begins
		int startIdx = 0;
		int idxOld = 0;
		while ((idxOld = aInput.indexOf(aOldPattern, startIdx)) >= 0) {
			// grab a part of aInput which does not include aOldPattern
			result.append(aInput.substring(startIdx, idxOld));
			// add aNewPattern to take place of aOldPattern
			result.append(aNewPattern);

			// reset the startIdx to just after the current match, to see
			// if there are any further matches
			startIdx = idxOld + aOldPattern.length();
		}
		// the final chunk will go to the end of aInput
		result.append(aInput.substring(startIdx));
		return result.toString();
	}

	public static boolean filePutContents(String url, String contents) {
		try {
			FileWriter outFile = new FileWriter(url);
			PrintWriter out = new PrintWriter(outFile);

			out.write(contents);

			out.close();
			return true;
		} catch (Exception e) {
			System.out.println("filePutContents: " + url);
			e.printStackTrace();
			return false;
		}
	}

	private void addBuildPath(Vector<String> buildPath, String entry, String basedir, String targetbase) {
		File folder = new File(basedir + File.separator + entry);
		String[] jars = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		for (String j : jars) {
			buildPath.add(targetbase + "/" + entry + "/" + j);
		}
	}

	public static String fileGetContents(File file) {
		StringBuffer content = new StringBuffer();

		try {
			if (!file.exists()) {
				System.out.println("fileGetContents: File don't exist: " + file.getAbsolutePath());
				return null;
			}

			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = br.readLine()) != null) {
				content.append(line);
				content.append("\n");
			}
			in.close();
		} catch (Exception e) {
			System.out.println("fileGetContents: " + file);
			e.printStackTrace();
			return null;
		}

		return content.toString();
	}

	public void setTargetbase(String targetbase) {
		this.targetbase = targetbase;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}

	public void setBuildpath(String buildpath) {
		this.buildpath = buildpath;
	}

	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public void setWorkdir(String workdir) {
		this.workdir = workdir;
	}
	
	public void setSplash(String splash) {
		this.splash = splash;
	}

	public void setLibpath(String libpath) {
		this.libpath = libpath;
	}
}