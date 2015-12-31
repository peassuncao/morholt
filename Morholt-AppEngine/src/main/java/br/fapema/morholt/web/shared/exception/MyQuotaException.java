package br.fapema.morholt.web.shared.exception;

public class MyQuotaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1990247579361143174L;

	public MyQuotaException() {
	}

	public MyQuotaException(String message) {
		super(message);
	}

	public MyQuotaException(Throwable cause) {
		super(cause);
	}

	public MyQuotaException(String message, Throwable cause) {
		super(message, cause);
	}

	public MyQuotaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
