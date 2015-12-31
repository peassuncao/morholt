package br.fapema.morholt.web.client.gui.exception;

public class AuthorizationException extends Exception {
	private static final long serialVersionUID = -8290141662655015177L;

	private String msg;

	@Override
	public String getMessage() {
		return msg;
	}

	public AuthorizationException(String string) {
		msg = string;
	}

	public AuthorizationException() {
	}

}