package ch.zhaw.simulation.diagram.persist;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class is used to save Diagram information, no user contents is saved.
 * The contents is not escaped!
 * 
 * @author Andreas Butti
 * 
 * @param <T>
 */
public abstract class AbstractPersistList<T> {
	private HashMap<String, T> contents = new HashMap<String, T>();
	private String id;

	public AbstractPersistList(String id) {
		this.id = "$" + id + "$";
	}

	protected abstract T elementFromString(String data);

	public boolean parse(String data) {
		if(data == null) {
			return false;
		}
		
		if (!data.startsWith(id + ":")) {
			return false;
		}

		data = data.substring(id.length() + 1);

		StringTokenizer token = new StringTokenizer(data, ":");
		while (token.hasMoreTokens()) {
			String key = token.nextToken();
			if (!token.hasMoreTokens()) {
				break;
			}
			String value = token.nextToken();

			if (key == null || "".equals(key)) {
				continue;
			}

			set(key, elementFromString(value));
		}

		return true;
	}

	protected abstract String elementToString(T e);

	public String asString() {
		StringBuilder b = new StringBuilder();
		b.append(id);

		for (Entry<String, T> d : contents.entrySet()) {
			b.append(":");
			b.append(d.getKey());
			b.append(":");
			b.append(elementToString(d.getValue()));
		}

		return b.toString();
	}

	protected void set(String key, T value) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}
		contents.put(key, value);
	}

	protected T get(String key) {
		return contents.get(key);
	}

	public Set<Entry<String, T>> contents() {
		return contents.entrySet();
	}
}
