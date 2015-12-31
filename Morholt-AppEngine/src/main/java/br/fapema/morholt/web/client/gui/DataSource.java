package br.fapema.morholt.web.client.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * this will be read from some place (xml, csv)
 * @author pedro
 *
 */
public class DataSource implements Comparable<DataSource>, Serializable{
	private static final long serialVersionUID = 3516480404952805090L;
	private String label;
	private boolean filterBy;
	private DataSource dependent;
	
	//needed for gwt sending
	public DataSource() {
		super();
	}
 	
	public DataSource(DataSourceTypeEnum type, String name, String label, boolean showOnList,
			boolean required, boolean filterBy) {
		super();
		this.type = type;
		this.name = name;
		this.label = label;
		this.showOnList = showOnList;
		this.required = required;
		this.filterBy = filterBy;
	}
	private DataSourceTypeEnum type;
	private String name;
	private boolean showOnList;
	private boolean required;
	private List<String> comboValues;
	
	
	
	public DataSourceTypeEnum getType() {
		return type;
	}
	public void setType(DataSourceTypeEnum type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isShowOnList() {
		return showOnList;
	}
	public void setShowOnList(boolean showOnDetail) {
		this.showOnList = showOnDetail;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	@Override
	public int compareTo(DataSource o) {
		if(name != null && o != null) {
			return name.compareTo(o.name);
		}
		else if (o != null) {
			return o.name.compareTo(name);
		}
		return 0;
	}
	
	public static List<DataSource> createDataSourcesExample() {
		ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
//		DataSource dataSource = new DataSource("String", "nome do projeto", true, false);
//		DataSource dataSource2 = new DataSource("String", "estado", true, false);
		

		DataSource dataSource = new DataSource(DataSourceTypeEnum.Text, "np", "NP", true, false, false);
		DataSource dataSource2 = new DataSource(DataSourceTypeEnum.Image, "photo", "foto", true, false, false);
		DataSource dataSource3 = new DataSource(DataSourceTypeEnum.Text, "latitude", "lat", true, false, false);
		
		dataSources.add(dataSource);
		dataSources.add(dataSource2);
		dataSources.add(dataSource3);
		return dataSources;
	}
	public String getLabel() {
		return label;
	}

	public void setComboValues(List<String> comboValues) {
		this.comboValues = comboValues;
		
	}

	public List<String> getComboValues() {
		return comboValues;
	}
	
	@Override
	public String toString() {
		return "Datasource-> type:" + type + " name:" + name + " label:" + label + " required:" + required + " showOnList:" + showOnList + " super.toString:" + super.toString();
	}

	public boolean isFilterBy() {
		return filterBy;
	}

	public boolean hasDependent () {
		return getDependent() != null;
	}
	
	public DataSource getDependent() {
		return dependent;
	}

	public void setDependent(DataSource dependent) {
		this.dependent = dependent;
	}
}
