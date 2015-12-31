package br.fapema.morholt.web.shared.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import br.fapema.morholt.web.client.gui.model.Model;

public class State {

	public State() {

	}
	
	public static List<String> values() {
		return Arrays.asList( "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RO", "RS", "RR", "SC", "SE", "SP", "TO");
	}
	
	public static List<Model> models() {
		ArrayList<Model> models = new ArrayList<Model>();
		List<String> values = values();
		for (String string : values) {
			HashMap<String, Object> contentValues = new HashMap<String, Object> ();
			contentValues.put("state", string);
			models.add(new Model(contentValues,"state", "state"));
		}
		return models;
	}
	
	

}
