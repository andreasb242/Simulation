package ch.zhaw.simulation.gui.configuration;

public class NameChecker {
	private static final String FIRST_ALLOWED = "abcedfghijklmnopqrstuvwxyzABCEDFGHIJKLMNOPQRSTUVWXYZ_";

	public boolean checkName(String name) {
		if(name == null) {
			return false;
		}
		
		if(name.length() < 1 || name.length() > 32) {
			return false;
		}
		
		char c = name.charAt(0);
		if(FIRST_ALLOWED.indexOf(c) == -1) {
			return false;
		}
		
		for(int i = 0; i < name.length(); i++) {
			c = name.charAt(i);
			
			if(FIRST_ALLOWED.indexOf(c) == -1 && !Character.isDigit(c)) {
				return false;
			}
		}
		
		return true;
	}
	
}
