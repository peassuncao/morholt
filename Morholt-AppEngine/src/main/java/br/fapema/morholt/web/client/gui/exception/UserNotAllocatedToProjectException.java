package br.fapema.morholt.web.client.gui.exception;

public class UserNotAllocatedToProjectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -852483881531237491L;
	private String msg;

	@Override
	public String getMessage() {
		return msg;
	}

	public UserNotAllocatedToProjectException(String string) {
		msg = string;
	}

	public UserNotAllocatedToProjectException() {
	}

}