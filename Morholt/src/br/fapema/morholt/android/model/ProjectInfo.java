package br.fapema.morholt.android.model;

import android.app.Activity;
import android.content.SharedPreferences;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

/**
 * projectInfo saved on SharedPreferences
 * @author bhola
 *
 */
public class ProjectInfo {
	
	private static final String SHARED_PREFS = "TEMPLATE_INFO";

	private static String templateFileName, templateValuesFileName, projectName, tableName, city, state,  locality, sitio, nameOfThePlace;
	
	private static Values values;
	
	/**
	 * 
	 * @param nameOfThePlace 
	 * @param sitio 
	 * @param locality 
	 * @param state 
	 * @param city 
	 * @param templateValuesFileName 
	 * @param templateFileName to be saved on android
	 * @param projectName
	 */
	public static void setValues(String aTemplateFileName, String aProjectName, String aTableName, String aTemplateValuesFileName, String acity, String astate, String alocality, String asitio, String anameOfThePlace) {
		templateFileName = aTemplateFileName;
		projectName = aProjectName;
		city = acity;
		state = astate;
		locality = alocality;
		sitio = asitio;
		nameOfThePlace = anameOfThePlace;
		tableName = aTableName;
		templateValuesFileName = aTemplateValuesFileName;
		
		values = new Values();
		values.put("templateFileName", templateFileName);
		values.put("templateValuesFileName", templateValuesFileName);
		values.put("projectName", projectName);
		values.put("tableName", tableName);
		values.put("cidade", city);
		values.put("state", state);
		values.put("locality", locality);
		values.put("sitio", sitio);
		values.put("nameOfThePlace", nameOfThePlace);
	}
	public static String getTemplateFileName() {
		return templateFileName;
	}
	public static String getTemplateValuesFileName() {
		return templateValuesFileName;
	}
	public static String getProjectName() {
		return projectName;
	}
	public static String getTableName() {
		return tableName;
	}
	
	public static void save(Activity context) {
		SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
	    editor.putString("templateFileName", templateFileName);
	    editor.putString("templateValuesFileName", templateValuesFileName);
	    editor.putString("projectName", projectName);
	    editor.putString("cidade", city);
	    editor.putString("state", state);
	    editor.putString("locality", locality);
	    editor.putString("sitio", sitio);
	    editor.putString("nameOfThePlace", nameOfThePlace);
	    editor.putString("tableName", tableName);
	    editor.commit();
	}
	
	public static boolean load(Activity context) {
		SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
		templateFileName = preferences.getString("templateFileName", null);
		templateValuesFileName = preferences.getString("templateValuesFileName", null);
		projectName = preferences.getString("projectName", null);
		city = preferences.getString("cidade", null);
		state = preferences.getString("state", null);
		locality = preferences.getString("locality", null);
		sitio = preferences.getString("sitio", null);
		nameOfThePlace = preferences.getString("nameOfThePlace", null);
		tableName = preferences.getString("tableName", null);
		return projectName != null;
	}
	
	public static boolean ready() {
		return projectName != null;
	}
	public static String getCity() {
		return city;
	}
	public static String getState() {
		return state;
	}
	public static String getLocality() {
		return locality;
	}
	public static String getSitio() {
		return sitio;
	}
	public static String getNameOfThePlace() {
		return nameOfThePlace;
	}
}