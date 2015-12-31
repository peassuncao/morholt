package br.fapema.morholt.web.client.gui.grid.enter;

import java.util.ArrayList;
import java.util.List;

public class EnterTypes {

	private List<EnterType> items;
	
	public EnterTypes(EnterType ...types) {
		items = new ArrayList<EnterType>();
		addAll(types);
	}

	private void addAll(EnterType[] types) {
		if(types == null) return;
		for (int i = 0; i < types.length; i++) {
			items.add(types[i]);
		}
		
	}
	
	public boolean contains(EnterType item) {
		return items.contains(item);
	}
}
