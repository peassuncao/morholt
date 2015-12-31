package br.fapema.morholt.web.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.DataSourceTypeEnum;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.shared.parser.BlockUnit;
import br.fapema.morholt.web.shared.parser.RepeatUnit;
import br.fapema.morholt.web.shared.parser.SingleUnit;
import br.fapema.morholt.web.shared.parser.Unit;
import br.fapema.morholt.web.shared.util.StringUtils;

public class TemplateReader {

	public static final String SENT_TO_SERVER = "sentToServer";
	
	public static void read() {
		
	}
	
	public static Map<String, List<DataSource>> generateTemplateToDatasources(Model templateModel ) {
		String template = templateModel.get("template");
		
		ArrayList<String> templateLinesList = new ArrayList<String>();
		String[] templateLinesArray = template.split("\n");
		for (String astring : templateLinesArray) {
			templateLinesList.add(astring);
		}

		String columnNames = templateLinesList.remove(0);
		String[] columnsNamesSplitted = columnNames.split("\\;", -1);
	
		List<br.fapema.morholt.web.shared.parser.Unit> units = TemplateReader.groupByUnit(templateLinesList);
		
		
		HashMap<String, List<String>> comboMapNameToValues = createComboMapNameToValues(templateModel);
		
		return TemplateReader.generateTemplateToDatasources(units, columnsNamesSplitted, templateModel.get("name"), comboMapNameToValues);
		
	}
	
	private static HashMap<String, List<String>> createComboMapNameToValues (Model templateModel) {

		String valuesLines = templateModel.get("values");
		if(valuesLines == null || valuesLines.isEmpty()) return null;
		ArrayList<String> lines = new ArrayList<String>();
		String[] alines = valuesLines.split("\n");
		for (String astring : alines) {
			lines.add(astring);
		}
		
		HashMap<String, List<String>> mapNameValues = new HashMap<String, List<String>>();
		for(String line : lines) {
			String[] dividedLine = line.split(":");
			
			String name = dividedLine[0];
			String[] values = dividedLine[1].split(";");
			ArrayList<String> valuesList = new ArrayList<String>();
			for(String value : values)
				valuesList.add(value);
			
			mapNameValues.put(name, valuesList);
		}
		return mapNameValues;
		
	}
	
	private static List<DataSource> generateDataSources(List<SingleUnit> units, String[] columnsNamesSplitted, HashMap<String, List<String>> comboMapNameToValues) {
		List<DataSource> columns = new ArrayList<DataSource>();
		for (Unit unit : units) {
				SingleUnit single = (SingleUnit) unit;
				String line = single.getLine();
				Map<String, String> nameValuesMap = parseColumns(line, columnsNamesSplitted);
				String name = nameValuesMap.get("name").trim();
				String label = nameValuesMap.get("label").trim();
				
				boolean filterBy = StringUtils.isEmpty(nameValuesMap.get("filterBy")) ? false : Boolean.valueOf(nameValuesMap.get("filterBy")); //FIXME filter
				
				boolean showOnList = StringUtils.isEmpty(nameValuesMap.get("showOnList")) ? true : Boolean.valueOf(nameValuesMap.get("showOnList"));
				boolean required = "true".equals(nameValuesMap.get("required").trim())?true:false;
				String type = nameValuesMap.get("type").trim().toLowerCase();
				if("geopoint".equals(type)) {
					DataSource columnX = new DataSource(DataSourceTypeEnum.Text, name+"X", label+"X", showOnList, false, filterBy);
					DataSource columnY = new DataSource(DataSourceTypeEnum.Text, name+"Y", label+"Y", showOnList, false, filterBy);
					columns.add(columnX);
					columns.add(columnY);
				}
				else if (type.equals("image")){
					DataSource column = new DataSource(DataSourceTypeEnum.Image, name, label, showOnList, false, filterBy); // required is false to be able to save partial results locally
					columns.add(column);
					DataSource column2 = new DataSource(DataSourceTypeEnum.ImageBoolean, name+"Boolean", label, false, false, filterBy); // required is false to be able to save partial results locally
					columns.add(column2);
				}
				
				else if (type.contains("select_one")){
					DataSource column = new DataSource(DataSourceTypeEnum.Combobox, name, label, showOnList, false, filterBy); // required is false to be able to save partial results locally
					if(comboMapNameToValues != null && !comboMapNameToValues.isEmpty())
						column.setComboValues(comboMapNameToValues.get(name));
					columns.add(column);
				}
				
				else if (type.equals("date")){
					DataSource column = new DataSource(DataSourceTypeEnum.Date, name, label, showOnList, false, filterBy); // required is false to be able to save partial results locally
					columns.add(column);
				}
				
				else if(type.equals("checkbox")) {
					DataSource column = new DataSource(DataSourceTypeEnum.Checkbox, name, label, showOnList, false, filterBy); // required is false to be able to save partial results locally
					columns.add(column);
				}
				
				else if(type.equals("text")) {
					DataSource column = new DataSource(DataSourceTypeEnum.Text, name, label, showOnList, false, filterBy); // required is false to be able to save partial results locally
					columns.add(column);
				}
				else if(type.equals("decimal")) {
					DataSource column = new DataSource(DataSourceTypeEnum.Text, name, label, showOnList, false, filterBy); // required is false to be able to save partial results locally
					columns.add(column);
				}
				else {
					throw new RuntimeException("unkown type: " + type);
				}
		}
		return columns;
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
	
	public static Map<String, List<DataSource>> generateTemplateToDatasources(List<Unit> units, String[] columnsNamesSplitted, String templateName, HashMap<String, List<String>> comboMapNameToValues) {
		
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
		
		Map<String, List<DataSource>> tableToDataSources = new HashMap<String, List<DataSource>>();
		
		tableToDataSources.put(templateName, generateDataSources(simpleUnits, columnsNamesSplitted, comboMapNameToValues));
		
		
		// TODO repeat is not really implemented
		for (Unit repeatUnit : repeatUnits) {
			String repeatTableName = null; //TODO
			List<Unit> unitList = repeatUnit.getContent();
			tableToDataSources.putAll(generateTemplateToDatasources(unitList, columnsNamesSplitted, repeatTableName, comboMapNameToValues));
		}
		return tableToDataSources;
	}
	/*
	public static List<String> readFromFile() {
		List<String> lines = new ArrayList<String>();
		try {
			File file = new File("WEB-INF/assets/fc_teste20.csv");
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "ISO-8859-1");
			
			BufferedReader reader = new BufferedReader(inputStreamReader);
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	// if not empty (has some word)
	        	if(line.matches(".*\\w.*")) 
	        		lines.add(line);
	        }
	        reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return lines;
	}
	*/
	
	/**
	 * groups as BlockUnit or SingleUnit
	 * @param lines
	 * @return
	 */
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
