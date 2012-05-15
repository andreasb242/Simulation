package ch.zhaw.simulation.diagram.persist;

public class PersistListBoolean extends AbstractPersistList<Boolean> {
	public PersistListBoolean(String id) {
		super(id);
	}

	@Override
	protected Boolean elementFromString(String data) {
		if (data == null) {
			return false;
		}
		return Boolean.parseBoolean(data);
	}

	@Override
	protected String elementToString(Boolean e) {
		if (e == null) {
			e = false;
		}
		return Boolean.toString(e);
	}

}
