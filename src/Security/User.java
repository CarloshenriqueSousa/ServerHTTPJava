package Security;

public class User {

	private final static int id;
	private final static String cargo;
	private static final String username;
	
	public User(int id, String username, String cargo) {
		this.username = username;
		this.id = id;
		this.cargo = cargo;
	}

	public static String getCargo() {
		return cargo;
	}

	public static String getUsername() {
		return username;
	}
	
	
}
