package br.fapema.morholt.android.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grilledmonkey.niceql.structs.Column;

public class Database extends ArrayList<DataStructure> {

	private static final long serialVersionUID = 4630996898739790876L;
	
	public String getMainTableName() {
		return get(0).tableName;
	}
	
	public DataStructure getDatastructure(String tableName) {
		for (int i=0; i<size(); i++) {
			DataStructure dataStructure = get(i);
			if(dataStructure.tableName.equals(tableName))
				return dataStructure;
		}
		return null;
	}
	
	public Map<String, String> getMapTableKey() {
		Map<String, String> map = new HashMap<String,String>();
		for (int i=0; i<size(); i++) {
			DataStructure dataStructure = get(i);
			map.put(dataStructure.tableName, dataStructure.keyColumn);
		}
		return map;
	}
	
	public List<String> getKeys() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i=0; i<size(); i++) {
			DataStructure dataStructure = get(i);
			arrayList.add(dataStructure.keyColumn);
		}
		return arrayList;
	}
	
	public Set<String> getRelatedTables (String tableName) {
		HashSet<String> set = new HashSet<String>();
		for(int i=0; i < size(); i++) {
			DataStructure dataStructure = get(i);
			
			for (Column column : dataStructure.columns) {
				if((tableName+"_id").equals(column.getName())) {
					set.add(dataStructure.tableName);
					break;
				}
			}
			
			
		}
		return set;
	}
}
