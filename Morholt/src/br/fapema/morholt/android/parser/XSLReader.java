package br.fapema.morholt.android.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.activeandroid.util.Log;
import com.grilledmonkey.niceql.structs.Column;
import com.grilledmonkey.niceql.structs.Index;

import android.text.InputType;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.android.parser.unit.BlockUnit;
import br.fapema.morholt.android.parser.unit.RepeatUnit;
import br.fapema.morholt.android.parser.unit.SingleUnit;
import br.fapema.morholt.android.parser.unit.Unit;
import br.fapema.morholt.android.wizardpager.wizard.model.BarCodePage;
import br.fapema.morholt.android.wizardpager.wizard.model.CameraPage;
import br.fapema.morholt.android.wizardpager.wizard.model.DatePage;
import br.fapema.morholt.android.wizardpager.wizard.model.GPSPage;
import br.fapema.morholt.android.wizardpager.wizard.model.PageList;
import br.fapema.morholt.android.wizardpager.wizard.model.SingleFixedChoicePage;
import br.fapema.morholt.android.wizardpager.wizard.model.TextPage;

public class XSLReader {

	public static final String SENT_TO_SERVER = "sentToServer";
	public static final String PHOTO_SENT_TO_SERVER = "photoSentToServer";
	public static PageList pageList = new PageList();
	
	public static void read() {
		
	}
	
	private static List<Column> generateColumns(List<SingleUnit> units, String[] columnsNamesSplitted) {
		List<Column> columns = new ArrayList<Column>();
		for (Unit unit : units) {
				SingleUnit single = (SingleUnit) unit;
				String line = single.getLine();
				Map<String, String> nameValuesMap = parseColumns(line, columnsNamesSplitted);
				String name = nameValuesMap.get("name").trim();
				boolean required = "true".equals(nameValuesMap.get("required").trim())?true:false;
				String type = nameValuesMap.get("type").trim();
				if("geopoint".equals(type)) {
					Column columnX = new Column(name+"X", "string", false);
					Column columnY = new Column(name+"Y", "string", false);
					Column columnZ = new Column(name+"Z", "string", false);
					Column columnLat = new Column(name+"Lat", "string", false);
					Column columnLong = new Column(name+"Long", "string", false);
					columns.add(columnX);
					columns.add(columnY);
					columns.add(columnZ);
					columns.add(columnLat);
					columns.add(columnLong);
				}
				else {
					Column column = new Column(name, "string", false); // required is false to be able to save partial results locally
					columns.add(column);
				}
		}
		return columns;
	}
	
	private static DataStructure generateDatastructure(List<SingleUnit> singleUnits, String[] columnsNamesSplitted, String tableName, String tableKey) {
		
		
		List<Column> columns = generateColumns(singleUnits, columnsNamesSplitted);
		Column column4 = new Column(Creator.LAST_UPDATE_ON_SYSTEM, "string", false);
	//	Column column5 = new Column(Creator.SENT_TO_SERVER, Column.INTEGER, true);
		Column column8 = new Column(Creator.KEY, Column.TEXT, false);
		Column column9 = new Column(SENT_TO_SERVER, Column.TEXT, false);
		Column column10 = new Column(PHOTO_SENT_TO_SERVER, Column.TEXT, false);

		columns.add(column4);
	//	columns.add(column5);
		columns.add(column8);
		columns.add(column9);
		columns.add(column10);
			
		boolean denormalizeBeforeSendToServer = true; 
		List<Index> nullIndexes = null;
		DataStructure dataStructure = DataStructure.c(tableName, tableKey, columns, nullIndexes, denormalizeBeforeSendToServer);
		return dataStructure;
	}
	
	private static List<Unit> disgroupBlocks(List<Unit> units) {
		ArrayList<Unit> result = new ArrayList<Unit>();
		for (Unit unit : units) {
			if(unit instanceof RepeatUnit) {
				result.add(unit);
			}
			else if(unit instanceof BlockUnit) {
				result.addAll(disgroupBlocks(unit.getContent()));
			}
			else {
				result.add(unit);
			}
		}
		return result;
	}
	
	public static Database generateDatabase(List<Unit> units, String[] columnsNamesSplitted, String tableName, String tableKey, Map<String, List<String>> mapComboNameValues) {
		Database database = new Database();
		
		List<Unit> repeatUnits = new ArrayList<Unit>();
		List<SingleUnit> simpleUnits = new ArrayList<SingleUnit>();
		
		units = disgroupBlocks(units);
		for (Unit unit : units) {
			if(unit instanceof RepeatUnit) {
				repeatUnits.add(unit);
			}
			else {
				simpleUnits.add((SingleUnit)unit);
			}
		}
		
		DataStructure dataStructure = generateDatastructure(simpleUnits, columnsNamesSplitted, tableName, tableKey);
		
		ModelPath rootPath = new ModelPath(PathPair.c(tableName, null));
		pageList.setRootPath(rootPath);
		
		pageList.addAll(generatePageList(simpleUnits, columnsNamesSplitted, tableName, mapComboNameValues));
		
		database.add(dataStructure);
		
		for (Unit repeatUnit : repeatUnits) {
			String repeatTableName = null; //TODO
			String repeatTableKey = null; //TODO
			List<Unit> unitList = repeatUnit.getContent();
			Database repeatDataStructure = generateDatabase(unitList, columnsNamesSplitted, repeatTableName, repeatTableKey, mapComboNameValues);
			database.addAll(repeatDataStructure);
		}
		
		return database;
	}
	
	private static PageList generatePageList(List<SingleUnit> simpleUnits, String[] columnsNamesSplitted, String tableName, Map<String, List<String>> mapComboNameValues) {
		
		
		ModelPath rootPath = new ModelPath(PathPair.c(tableName, null));
		
		PageList pageList = new PageList(rootPath);
		
		for (SingleUnit unit : simpleUnits) {
				SingleUnit single = (SingleUnit) unit;
				String line = single.getLine();
				Map<String, String> nameValuesMap = parseColumns(line, columnsNamesSplitted);
				String type = nameValuesMap.get("type").trim();
				String hint = nameValuesMap.get("hint").trim();
				String name = nameValuesMap.get("name").trim();
				String label = nameValuesMap.get("label").trim();
				boolean required = "true".equals(nameValuesMap.get("required").trim())?true:false;
				if(type.equals("text")) {
					pageList.add(new TextPage(label, name, hint, rootPath, InputType.TYPE_CLASS_TEXT).setRequired(required));
				}
				else if (type.equals("barcode")) {
					pageList.add(new BarCodePage(label, name, hint, rootPath).setRequired(required));
				}
				else if (type.equals("decimal")) {
					pageList.add(new TextPage(label, name, hint, rootPath, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL).setRequired(required));
				}
				else if (type.startsWith("select_one")) {
					String[] values = null;
					if(mapComboNameValues != null && mapComboNameValues.get(name) != null) {
						values = new String[mapComboNameValues.get(name).size()];
						mapComboNameValues.get(name).toArray(values);
					}
					pageList.add(new SingleFixedChoicePage(label, name, hint, rootPath).setChoices(values).setRequired(required));
				}
				else if (type.equals("integer")) {
					pageList.add(new TextPage(label, name, hint, rootPath, InputType.TYPE_CLASS_NUMBER).setRequired(required));
				}
				else if (type.equals("date")) {
					pageList.add(new DatePage(label, name, hint, rootPath).setRequired(required));
				}
				else if (type.equals("note")) {
				//	pageList.add(new TextPage(label, name, hint, rootPath, InputType.TYPE_CLASS_TEXT).setRequired(required));
				}
				else if (type.equals("geopoint")) {
					pageList.add(new GPSPage(label, name + "X", name + "Y", name+"Z", name + "Lat", name + "Long", hint, rootPath).setRequired(required));
				}
				else if (type.equals("image")) {
					pageList.add(new CameraPage(label, name, hint, rootPath).setRequired(required));
				}
				else {
					throw new RuntimeException("unkown type: " + type);
				}
		}
		return pageList;
	}
	
	public static List<String> readFromFile(String fileName) {
		if(fileName == null) return null;
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream fileInputStream = MyApplication.getAppContext().openFileInput(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	// if not empty (has some word)
	        	if(line.matches(".*\\w.*")) 
	        		lines.add(line);
	        }
	        reader.close();
		} catch (IOException e) {
			Log.e(XSLReader.class.getSimpleName(), "error reading file: " + fileName + " " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("error reading file", e);
		}
		return lines;
	}
	
	public static List<Unit> groupByUnit(List<String> lines) {
		ArrayList<Unit> result = new ArrayList<Unit>();
		
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(line.startsWith("begin group")) {
				ArrayList<Unit> newGroupUnits = new ArrayList<Unit>();
				int endIndex = findMatchingEndIndex(i+1, lines);
				newGroupUnits.add(new SingleUnit(line));
				List<Unit> groupedByUnit = groupByUnit(lines.subList(i+1, endIndex));
				newGroupUnits.addAll(groupedByUnit);
				newGroupUnits.add(new SingleUnit(lines.get(endIndex)));
				result.add(new BlockUnit(newGroupUnits));
				i+=size(groupedByUnit)+1;
			} //TODO can add if for repeat
			else {
				result.add(new SingleUnit(line));
			}
		}
		return result;
	}
	
	private static int size(List<Unit> units) {
		int size = 0;
		for (Unit unit : units) {
			size += unit.size();
		}
		return size;
	}
	
	public static int findMatchingEndIndex(int start, List<String> lines) {
		int openedGroups = 1;
		for (int i = start; i < lines.size(); i++) {
			String line = lines.get(i);
			if(line.startsWith("begin group")) openedGroups++;
			else if (line.startsWith("end group")) openedGroups--;
			
			if(openedGroups == 0) return i;
		}
		throw new RuntimeException("mismatched group formation");
	}
	
	
	
	public static Map<String, String> parseColumns (String line, String[] columnsNamesSplitted) {
		Map<String, String> result = new HashMap<String, String>();
		
		String[] columnsValues = line.split("\\;", -1);
		for(int i=0; i < columnsValues.length; i++) {
			result.put(columnsNamesSplitted[i], columnsValues[i]);
		}
		return result;
	}
	
	public static Map<String, List<String>> parseColumns (List<String> lines) {
		String columnNames = lines.remove(0);
		
		String[] columnsNamesSplitted = columnNames.split("\\;", -1);
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		
		for(int i=0; i<columnsNamesSplitted.length; i++) {
			result.put(columnsNamesSplitted[i].trim(), new ArrayList<String>());
		}
		
		for (String line : lines) {
			String[] columnsValues = line.split("\\;", -1);
			for(int i=0; i<columnsNamesSplitted.length && i < columnsValues.length; i++) {
				
				
				List<String> list = result.get(columnsNamesSplitted[i]);
				list.add(columnsValues[i]);
			}
		}
		return result;
		
	}
}
