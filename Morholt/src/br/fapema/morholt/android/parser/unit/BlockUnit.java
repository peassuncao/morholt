package br.fapema.morholt.android.parser.unit;

import java.util.List;

public class BlockUnit implements Unit {
	
	List<Unit> blockLines;
	
	public BlockUnit(List<Unit> blockLines) {
		this.blockLines = blockLines;
	}

	public List<Unit> getUnits() {
		return blockLines;
	}
	
	public List<Unit> getContent() {
		return blockLines.subList(1, blockLines.size()-1);
	}

	@Override
	public int size() {
		int size = 0;
		for (Unit unit : blockLines) {
			size += unit.size();
		}
		return size;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Unit unit : blockLines) {
			sb.append(unit.toString() + "\n");
		}
		return sb.toString();
	}
}
