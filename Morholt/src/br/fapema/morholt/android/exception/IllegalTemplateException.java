package br.fapema.morholt.android.exception;

public class IllegalTemplateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8106316539949971316L;

	public IllegalTemplateException() {
		// TODO Auto-generated constructor stub
	}

	public IllegalTemplateException(String detailMessage) {
		super(detailMessage);
	}

	public IllegalTemplateException(Throwable throwable) {
		super(throwable);
	}

	public IllegalTemplateException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
