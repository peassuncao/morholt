package br.fapema.morholt.web.client.gui.exception;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147647119417494942L;
	private String msg;

	@Override
	public String getMessage() {
		return msg;
	}

	public AuthenticationException(String string) {
		msg = string;
	}

	public AuthenticationException() {
	}

}