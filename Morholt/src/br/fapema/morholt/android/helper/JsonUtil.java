package br.fapema.morholt.android.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.collect.collectendpoint.model.JsonMap;
import br.fapema.morholt.android.parser.Database;

public class JsonUtil {

	public static JsonMap denormalizeX(JsonMap json, Database database, Context context) {
		JsonMap simpleValues = new JsonMap();
	  	JsonMap mapOfRepeatTables = new JsonMap();
	  	for(String key : json.keySet()) {
	  		Object value = json.get(key);
	  		if(!(value instanceof List)) {
	  			
	  			simpleValues.put(key, value);
	  		}
	  		else {
	  			
	  			boolean shouldDenormalizeBeforeSendToServer = database.getDatastructure(key).shouldDenormalizeBeforeSendToServer();
	  			if(shouldDenormalizeBeforeSendToServer) {
	  				mapOfRepeatTables.put(key, value);
	  			}
	  			else {
	  				// TODO
	  			}
	  		}
	  	}
	  	
	  	for(String repeatTableName : mapOfRepeatTables.keySet()) {
  			@SuppressWarnings("unchecked")
			List<JsonMap> repeatRows = (List<JsonMap>)  json.get(repeatTableName);
  			
  			for (int i = 0; i < repeatRows.size(); i++) {
  				
				JsonMap repeatRow = repeatRows.get(i);
				
				for(String repeatColumn : repeatRow.keySet()) {
					if(repeatColumn.contains("_id") || repeatColumn.equals("lastUpdateOnSystem") || repeatColumn.equals("sentToServer")) continue;
					simpleValues.put(repeatTableName + String.valueOf(i) + repeatColumn , repeatRow.get(repeatColumn));
				}
				
			}
  			simpleValues.put(repeatTableName + "Qty", repeatRows.size());
	  	}
	  	simpleValues.remove("_id");
	  	simpleValues.remove("sentToServer");
	  	simpleValues.remove("photoSentToServer");

	  	return simpleValues;
  	}
	  	
	/**
	 * add extra fields for each date: day, month, year
	 */
	private static void addEasyDateFields(JsonMap simpleValues, Context context) {
		JsonMap newDateExtras = new JsonMap();
		for (String key : simpleValues.keySet()) {
			
			if(key.contains("date") || key.contains("Date") || key.equals(Creator.LAST_UPDATE_ON_SYSTEM)) {
				if(!simpleValues.containsKey(key+"_day")) {
					//DateFormat dateFormat = new DateFormat();
					
					int style = java.text.DateFormat.MEDIUM;

					Locale locale = context.getResources().getConfiguration().locale;
					java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(style, locale);
					
					Date date = null;
					try {
						date = dateFormat.parse((String)simpleValues.get(key));
						
						
						Calendar calendar = GregorianCalendar.getInstance();
						dateFormat.setCalendar(calendar);
						
						calendar.setTime(date);
						
						int day = calendar.get(Calendar.DAY_OF_MONTH);
						int month = calendar.get(Calendar.MONTH) + 1;
						int year = calendar.get(Calendar.YEAR);
						
						newDateExtras.put(key+"_day", String.valueOf(day));
						newDateExtras.put(key+"_month", String.valueOf(month));
						newDateExtras.put(key+"_year", String.valueOf(year));
						
					} catch (ParseException e) {
						throw new RuntimeException("on addEasyDateFields, error parsing date: " + date);
					}
				}
			}
		}
		simpleValues.putAll(newDateExtras);
	}
	
	
	public static List<JsonMap> denormalize(JsonMap json, Database database) {
	  	
	  	List<JsonMap> normalizedList = new ArrayList<JsonMap>();
	  	
	  	JsonMap simpleValues = new JsonMap();
	  	JsonMap listValues = new JsonMap();
	  	for(String key : json.keySet()) {
	  		Object value = json.get(key);
	  		if(!(value instanceof List)) {
	  			
	  			simpleValues.put(key, value);
	  		}
	  		else {
	  			
	  			boolean shouldDenormalizeBeforeSendToServer = database.getDatastructure(key).shouldDenormalizeBeforeSendToServer();
	  			if(shouldDenormalizeBeforeSendToServer) {
	  				listValues.put(key, value);
	  			}
	  			else {
	  				// TODO
	  			}
	  		}
	  	}
	  	
	  	normalizedList.add(simpleValues);
	  	
	  	
	  	for(String key : listValues.keySet()) {
	  			@SuppressWarnings("unchecked")
				List<JsonMap> list = (List<JsonMap>)  json.get(key);
	  			normalizedList = denormalize(key, list, normalizedList);
	  	}
	  	
	  	for (JsonMap jsonMap : normalizedList) {
			jsonMap.put("total", normalizedList.size());
		}
	  	
	  	return normalizedList;
	  }
	  
	  //TODO this should be recursive
	  private static List<JsonMap> denormalize(String listKey, List<JsonMap> newList, List<JsonMap> normalizedList) {
	  	List<JsonMap> result = new ArrayList<JsonMap>();
	  	int id=0;
	  	for (JsonMap newValues : newList) {
				for (JsonMap normalizedValues : normalizedList) {
					JsonMap valuesResult = new JsonMap();
					valuesResult.putAll(normalizedValues);
					valuesResult.putAll(newValues);
					valuesResult.put(listKey, id);
					result.add(valuesResult);
				}
				id++;
			}
	  	return result;
	  }

}
