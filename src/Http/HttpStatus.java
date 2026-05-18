package Http;

/**
 * CONCEITO: Enum com campos e construtores
 * Enums podem ter campos, construtores e métodos como classes
 * Cada constante chama o construtor com seus própios valores
 */

public enum HttpStatus {
	OK(200, "OK"),
	CREATED(201, "CREATED"),
	NO_CONTENT(204, "NO CONTENT"),
	BAD_REQUEST(400, "BAD REQUEST"),
	NOT_FOUND(404, "NOT FOUND"),
	METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),
	INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");
	
	private final int code;
	private final String message;
	
	HttpStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	/**
	 * Métodos Getter expoem campos privados controladamente
	 * @return
	 */

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return code + "" + message;
	}
	
	
}
