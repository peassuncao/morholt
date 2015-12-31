package br.fapema.morholt.android.parser;

import java.io.Serializable;

public class PathPair implements Serializable{
	private static final long serialVersionUID = -3088267706313558326L;
	public String kind;
	public Integer index;
	public PathPair(String kind, Integer index) {
		this.kind = kind;
		this.index = index;
	}
	public static PathPair c(String kind, Integer index) {
		return new PathPair(kind, index);
	}
	
	@Override
	public String toString() {
		return kind + "(" + index + ")";
	}
}
