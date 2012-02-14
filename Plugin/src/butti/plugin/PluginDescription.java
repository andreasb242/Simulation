package butti.plugin;

/**
 * Beinhaltet ein Plugin und die dazugehörige Beschreibung
 * 
 * @author Andreas Butti
 * 
 * @param <E>
 *            Das Interface das das Plugin implementieren muss
 */
public class PluginDescription<E> {
	private E plugin;
	private String name;
	private String description;
	private String author;
	private String filename;

	public PluginDescription() {
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public E getPlugin() {
		return plugin;
	}

	public void setPlugin(E plugin) {
		this.plugin = plugin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isValid() {
		return !author.equals("") && !description.equals("") && !name.equals("");
	}

	@Override
	public String toString() {
		return getName();
	}
}
