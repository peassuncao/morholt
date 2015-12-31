package br.fapema.morholt.android.db;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.parser.PathPair;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

// TODO maybe pass basic methos)save,load..) to DAOInterface
public class DAO implements Serializable {

	private static final long serialVersionUID = -8175043879541824343L;

	//private PageAndColumnList pageAndColumnList;
	//public Date lastUpdateOnSystem;

	
	public DAO() {
		// this.preData = collect;
//TODO		saveMe();
	}
	
	
	
	
	public void initializeKeepSomeValues() {

	}
	
	public static Values load(String id) {
		return DBHelper.load(id);
	}
	
	public static List<Values> listEq(String tablename, String column, String value) {
		return DBHelper.listRelatedToId(tablename, column, value, null);
	}

	private static Values getContentValues(ModelPath modelPath) {
		return MyApplication.getCurrentContentValues(modelPath);
	}

	public static void saveAll(String tablename, List<Values> listValues) {
		DBHelper.saveAll(listValues, new ModelPath(new PathPair(tablename, null)));
	}
	
	public static void save(String tablename, Values values) {
		DBHelper.save(values, new ModelPath(new PathPair(tablename, null)));
	}
	
	public static void save(ModelPath modelPath, Values values) {
		DBHelper.save(values, modelPath);
	}
	
	public static void saveMe(ModelPath modelPath) {
		MyApplication.getCurrentContentValues(modelPath).put(Values.LAST_UPDATE_ON_SYSTEM_COLUMN, new Date().toLocaleString()); 
		
		DBHelper.save(MyApplication.getCurrentContentValues(modelPath), modelPath);
	}
	
	public static void delete(ModelPath modelPath, Long id) {
		DBHelper.delete(id, modelPath);
	}

	public static String getValue(String column, ModelPath modelPath) {
		return getContentValues(modelPath).getAsString(column);
	}

	public static Long getLongValue(String column, ModelPath modelPath) {
		return getContentValues(modelPath).getAsLong(column);
	}
	
	public static HashMap<String, Boolean> getValues(List<String> columns, ModelPath modelPath) {
		HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
		for (String column : columns) {

			hashMap.put(column, getContentValues(modelPath).getAsBoolean(column));
		}
		return hashMap;

	}

	public static void addToContentValues(String column, List<Values> values, ModelPath modelPath) {
		getContentValues(modelPath).put(column, values);
	}
	
	// TODO make a more specific save for better performance
	public static void save(String column, String value, ModelPath modelPath) {
		getContentValues(modelPath).put(column, value);
		saveMe(modelPath);
	}

	public static void saveLong(String column, Long value, ModelPath modelPath) {
		getContentValues(modelPath).put(column, value);
		saveMe(modelPath);
	}
	
	public static void save(HashMap<String, Boolean> hashMap, ModelPath modelPath) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());
		}

		Iterator<Entry<String, Boolean>> it = hashMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Boolean> pairs = it.next();
			getContentValues(modelPath).put(pairs.getKey(), pairs.getValue());
		}

		saveMe(modelPath);
	}
}