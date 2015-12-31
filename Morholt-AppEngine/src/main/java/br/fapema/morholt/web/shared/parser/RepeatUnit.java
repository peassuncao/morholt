package br.fapema.morholt.web.shared.parser;

import java.util.List;

public class RepeatUnit implements Unit {
	
	List<Unit> repeatLines;
	
	public RepeatUnit(List<Unit> blockLines) {
		this.repeatLines = blockLines;
	}

	public List<Unit> getUnits() {
		return repeatLines;
	}

	@Override
	public int size() {
		int size = 0;
		for (Unit unit : repeatLines) {
			size += unit.size();
		}
		return size;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Unit unit : repeatLines) {
			sb.append(unit.toString() + "\n");
		}
		return sb.toString();
	}
	

	public List<Unit> getContent() {
		return repeatLines.subList(1, repeatLines.size()-1);
	}
}
