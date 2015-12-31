package br.fapema.morholt.android.exception;
public class ServerUnavailableException extends Exception {
	private static final long serialVersionUID = 3229220635310727548L;

	public ServerUnavailableException() {
		super();
	}

	public ServerUnavailableException(String detailMessage) {
		super(detailMessage);
	}

	public ServerUnavailableException(Throwable throwable) {
		super(throwable);
	}

	public ServerUnavailableException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
