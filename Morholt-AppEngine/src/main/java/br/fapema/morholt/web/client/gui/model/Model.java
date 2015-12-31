package br.fapema.morholt.web.client.gui.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

import br.fapema.morholt.web.shared.BlobURL;

public class Model implements Serializable, Cloneable, IsSerializable{
	

	private static final Logger log = Logger.getLogger(Model.class.getName()); 
	
	public  static final String FEATURED = "featuredXXX";
	private static final long serialVersionUID = 6386995055177110443L;
	private Map<String, Object> contentValues;
	private String keyName;
	private String kind;
	private HashMap<String, List<Model>> mapKindToSmallTables = new HashMap<String, List<Model>>();
	@SuppressWarnings("unused") //to allow serialization of dates
	private Date fooDate; 
	@SuppressWarnings("unused") //to allow serialization of longs
	private Long fooLong;
	@SuppressWarnings("unused") //to allow serialization of booleans
	private Boolean fooBoolean; 
	@SuppressWarnings("unused") //to allow serialization of booleans
	private BlobURL fooBlobURL;
	
	/**
	 * needed, don´t use directly
	 */
	public Model() {
		
	}
	
	public Model(HashMap<String, Object> contentValues, String keyName, String kind) {
		this.contentValues = contentValues;
		this.keyName = keyName;
		this.kind = kind;
	}
	
	public Model(String keyName, String kind) {
		contentValues = new HashMap<String, Object>();
		this.keyName = keyName;
		this.kind = kind;
	}
	
	
	public String toCSV(String separator) {
		StringBuilder sb = new StringBuilder();
		for(String key : contentValues.keySet()) {
			sb.append(contentValues.get(key)+separator);
		}
		return sb.toString();
	}
	
	public void removeAllSmallTables() {
		mapKindToSmallTables = new HashMap<String, List<Model>>();
	}
	
	public void putSmallTables(String kind, List<Model> smallTables) {
		mapKindToSmallTables.put(kind, smallTables);
	}
	
	public List<Model> getSmallTables(String kind) {
		return mapKindToSmallTables.get(kind);
	}
	
	public String get(String column) {
		return getAsString(column);
	}
	
	public Boolean getBoolean(String column) {
		Object object = contentValues.get(column);
		if(object == null) return false;
		return (Boolean) object;
	}
	
	public String getKeyName() { 
		return keyName; 
	}
	
	public String getKeyValue() { 
		return get(keyName); 
	}
	
	private String getAsString(String key) {
        Object value = contentValues.get(key);
        
        
        
        if(value instanceof Date) {
			//DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
			return DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format((Date)value);
        }

        else
        	return value != null ? value.toString() : null;
    }

	public static List<Model> genetateColectModels() { // TODO remove
		ArrayList<Model> models = new ArrayList<Model>();
		
		for(int i=0; i<100; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("NP", "123" + Math.random());
			map.put("foto", "http://lh4.ggpht.com/txO0yrcWnZqhDg2u_ryNQ1rDjcge6NEPbgsohuazhqpp2LYogc5jHrDViA-ledztsDozXJlGk-SK0CV3P6sz4fA-oTgcGKBk=s32");
			map.put("latitude", "duas foto" + Math.random());
			Model model = new Model(map, "NP", "akind");

			models.add(model);
		}
		
		return models;
	}

	public Map<String, Object> getContentValues() {
		return contentValues;
	}
	
	

	public void setContentValues(Map<String, Object> contentValues) {
		this.contentValues = contentValues;
	}
	
	public void put(String column, String value) {
		contentValues.put(column, value);
	}
	
	public void putObject(String column, Object value) {
		contentValues.put(column, value);
	}
	
	public void setFeatured(boolean value) {
		contentValues.put(FEATURED, value);
	}
	
	public Date getDate(String column) {
		return (Date) contentValues.get(column);
	}
	
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Model->");
		Set<String> keySet = contentValues.keySet();
		for (String key : keySet) {
			stringBuilder.append(key + ": " + contentValues.get(key) + "; ");
		}
		stringBuilder.append("END");
		return stringBuilder.toString();
	}

	public String getKind() {
		return kind;
	}

	public HashMap<String, List<Model>> getMapKindToSmallTables() {
		return mapKindToSmallTables;
	}

	public void setMapKindToSmallTables(
			HashMap<String, List<Model>> mapKindToSmallTables) {
		this.mapKindToSmallTables = mapKindToSmallTables;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Model)) return false;
		return getKeyValue().equals(((Model)obj).getKeyValue());
	}

	public Boolean isFeatured() {
		return (Boolean)contentValues.get(FEATURED);
	}

	//TODO this is terrible, clone seem to not work on gwt.. better see this
	public Object clone()  {  
        Model model = new Model();
        model.keyName = this.keyName;
        model.kind = this.kind;
        model.contentValues = new HashMap<String, Object>();
       for (String name : this.contentValues.keySet())  {
    	   Object value = this.contentValues.get(name);
    	   Object newValue;
    	   if(value instanceof String) 
    		   newValue = new String((String) value);
    	   else if(value instanceof Long) 
    		   newValue = new Long((Long) value);
    	   else if(value instanceof Boolean) 
    		   newValue = new Boolean((Boolean) value);
    	   else if(value instanceof Date) 
    		   newValue = new Date(((Date) value).getTime());
    	   else if(value == null)
    		   newValue = null;
    	   else {
    		   log.info("clone error : " + value.getClass().toString());
    		   throw new RuntimeException(value.toString());
    	   }
    	   
    	   model.contentValues.put(new String(name), newValue);
       }
        model.mapKindToSmallTables = new HashMap<String, List<Model>>(this.mapKindToSmallTables);
        return model;  
    }  
}