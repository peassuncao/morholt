package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.HashMap;
import java.util.List;

import br.fapema.morholt.android.parser.ModelPath;

public interface DAOInterface {
	public abstract void save(String column, String value, ModelPath modelPath);
	public abstract String getValue (String column, ModelPath modelPath);
	

	public abstract HashMap<String,Boolean> getValues (List<String> columns, ModelPath modelPath);
	public abstract void save(HashMap<String,Boolean> hashMap, ModelPath modelPath);
}
