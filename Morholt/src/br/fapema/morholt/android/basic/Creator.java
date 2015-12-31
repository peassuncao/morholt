package br.fapema.morholt.android.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.fapema.morholt.android.db.DBHelper;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.parser.Database;
import br.fapema.morholt.android.parser.XSLReader;
import br.fapema.morholt.android.parser.unit.Unit;
import br.fapema.morholt.android.wizardpager.ApplicationInterface;
import br.fapema.morholt.android.wizardpager.wizard.model.PageList;
public class Creator {
	public static final String KEY = "key";
	public static final String LAST_UPDATE_ON_SYSTEM = "lastUpdateOnSystem";
	public static final String SENT_TO_SERVER = "sentToServer";
	public static final String PHOTO_SENT_TO_SERVER = "photoSentToServer";
	public static final String TABLE_KEY = "np"; 
	private static final String REPEAT_KEY = "RepeatKey";
	public PageList pageList;
	private static final String ORIGINAL_ID = null;
	
	public Creator(ApplicationInterface applicationInterface) {
		initializeData(applicationInterface);
		System.out.println("created");
	}

	// read from files
	private void initializeData(ApplicationInterface applicationInterface) {
		XSLReader.pageList = new PageList();
		List<String >templateLines = XSLReader.readFromFile(ProjectInfo.getTemplateFileName());
		removeFeatureLine(templateLines);
		
		List<String >templateValuesLines = XSLReader.readFromFile(ProjectInfo.getTemplateValuesFileName());

		
		String columnNames = templateLines.remove(0);
		String[] columnsNamesSplitted = columnNames.split("\\;", -1);
		List<Unit> units = XSLReader.groupByUnit(templateLines);
		Database database = XSLReader.generateDatabase(units, columnsNamesSplitted, ProjectInfo.getTableName(), Creator.TABLE_KEY, createMapComboNameValues(templateValuesLines));
		
		DBHelper.initialize(applicationInterface.getApplicationContext(), database);
		applicationInterface.setDatabase(database);
		pageList = XSLReader.pageList;
		applicationInterface.setRootModelPath(pageList.getRootPath());
		
		System.out.println("pagelist created");
	}
	
	private void removeFeatureLine(List<String> lines) {
		for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();) {
			String line =  iterator.next();
			if(line.contains("featuredXXX")) {
				iterator.remove();
				break;
			}
		}
	}
	
	private Map<String, List<String>> createMapComboNameValues(List<String >templateValuesLines) {
		if(templateValuesLines == null) return null;
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		for (String valuesLine : templateValuesLines) {
			ArrayList<String> values = new ArrayList<String>();
			String[] splittedValuesLine = valuesLine.split(":");
			for(String value : splittedValuesLine[1].split(";"))
				values.add(value);
			result.put(splittedValuesLine[0], values);
		}
		return result;
	}
}