package br.fapema.morholt.web.shared;

import java.io.Serializable;

public class BlobURL implements Serializable{
	private static final long serialVersionUID = 6113884558764477878L;
	private String url;

	public BlobURL() {
		super();
	}
	
	public BlobURL(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
