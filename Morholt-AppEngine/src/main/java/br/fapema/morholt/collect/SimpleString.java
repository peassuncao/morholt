package br.fapema.morholt.collect;

import java.io.Serializable;

public class SimpleString  implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801457987948283440L;
	private String string;
	
	public SimpleString() {
		super();
	}
	
	public SimpleString(String string) {
		super();
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}