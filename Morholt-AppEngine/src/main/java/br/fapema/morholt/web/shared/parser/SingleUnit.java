package br.fapema.morholt.web.shared.parser;

import java.util.ArrayList;
import java.util.List;


public class SingleUnit implements Unit{
	private String line; 
	public SingleUnit(String line) {
		this.line = line;
	}
	public String getLine() {
		return line;
	}
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toString() {
		return line.toString();
	}
	@Override
	public List<Unit> getContent() {
		ArrayList<Unit> arrayList = new ArrayList<Unit>();
		arrayList.add(this);
		return arrayList;
	}
}
