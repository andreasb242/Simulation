package ch.zhaw.simulation.model;

/**
 * Checks the name of all Simulation objects against our rules
 * 
 * @author Andreas Butti
 */
public class NameChecker {
	private static final String ALLOWED = "abcedfghijklmnopqrstuvwxyzABCEDFGHIJKLMNOPQRSTUVWXYZ_";

	/**
	 * Intern reserviert z.B. für Matlab simulation
	 */
	private static final String[] BLOCKED_PREFIX = new String[] { "sim_" };

	/**
	 * Reserved names
	 */
	private static final String[] RESERVED_KEYWORDS = new String[] { "x", "y", "z", "dt", "dx", "dy", "dz", "e", "pi" };

	/**
	 * Checks a name
	 * 
	 * @param name
	 *            The name
	 * @return true if valid
	 */
	@Deprecated
	public boolean checkNameValid(String name) {
		try {
			checkName(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks a name
	 * 
	 * @param name
	 *            The name
	 * @throws InvalidNameException
	 *             If the name is invalid
	 */
	public void checkName(String name) throws InvalidNameException {
		if (name == null) {
			throw new InvalidNameException("Name is «null»");
		}

		if (name.length() < 1) {
			throw new InvalidNameException("Name length < 1");
		}
		if (name.length() > 32) {
			throw new InvalidNameException("Name length > 32");
		}

		char c = name.charAt(0);
		if (ALLOWED.indexOf(c) == -1) {
			throw new InvalidNameException("Name should not start with «" + c + "»");
		}

		for (int i = 0; i < name.length(); i++) {
			c = name.charAt(i);

			if (ALLOWED.indexOf(c) == -1 && !Character.isDigit(c)) {
				throw new InvalidNameException("Name should not contain «" + c + "»");
			}
		}

		for (String s : BLOCKED_PREFIX) {
			if (name.startsWith(s)) {
				throw new InvalidNameException("Name should not start with «" + s + "»");
			}
		}

		for (String s : RESERVED_KEYWORDS) {
			if (s.equals(name)) {
				throw new InvalidNameException("Name should not be «" + s + "»");
			}
		}
	}

}
