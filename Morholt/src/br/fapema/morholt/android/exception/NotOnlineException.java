package br.fapema.morholt.android.exception;

public class NotOnlineException extends Exception {

	private static final long serialVersionUID = 1582816642165309721L;

	public NotOnlineException() {
		super();
	}

	public NotOnlineException(String detailMessage) {
		super(detailMessage);
	}

	public NotOnlineException(Throwable throwable) {
		super(throwable);
	}

	public NotOnlineException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
