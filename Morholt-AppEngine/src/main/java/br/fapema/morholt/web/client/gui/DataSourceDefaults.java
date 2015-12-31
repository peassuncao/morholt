package br.fapema.morholt.web.client.gui;

import static br.fapema.morholt.web.shared.TableEnum.PROJECT;
import static br.fapema.morholt.web.shared.TableEnum.TEMPLATE_ANDROID;
import static br.fapema.morholt.web.shared.TableEnum.STATE;
import static br.fapema.morholt.web.shared.TableEnum.CITY;

import java.util.ArrayList;
import java.util.List;

import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.shared.TableEnum;

public abstract class DataSourceDefaults {
	
	public static List<DataSource> obtainDataSourcesAndroidTemplate() { 
		DataSource dataSource1 = new DataSource(DataSourceTypeEnum.Text, "name", "nome", true, false, false);
		DataSource dataSource2 = new DataSource(DataSourceTypeEnum.TextArea, "template", "template", true, false, false);
		DataSource dataSource3 = new DataSource(DataSourceTypeEnum.TextArea, "values", "values", false, false, false);
		ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
		dataSources.add(dataSource1);
		dataSources.add(dataSource2);
		dataSources.add(dataSource3);
		return dataSources;
	}
	
	public static List<DataSource> obtainDataSourcesUsers() { 
		DataSource dataSource1 = new DataSource(DataSourceTypeEnum.Text, "Name", "usuário", true, false, false);
		DataSource dataSource2 = new DataSource(DataSourceTypeEnum.Text, "email", "email", true, false, false);
		DataSource dataSource3 = new DataSource(DataSourceTypeEnum.Combobox, "profile", "perfil", true, false, false); 
		
		ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
		dataSources.add(dataSource1);
		dataSources.add(dataSource2);
		dataSources.add(dataSource3);
		return dataSources;
	}
	
	public static List<DataSource> obtainDataSourcesProfiles() {
		ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
		DataSource dataSource1 = new DataSource(DataSourceTypeEnum.Text, "name", "nome", true, true, false);
		dataSources.add(dataSource1);
		
		for (TableEnum tableEnum : TableEnum.values()) {
			CRUDEL[] crudes = Profile.CRUDEL.values();
			for (CRUDEL crude : crudes) {
				DataSource dataSource = new DataSource(DataSourceTypeEnum.Checkbox, tableEnum.kind + crude, tableEnum.kind +" "+crude, false, false, false); 
				dataSources.add(dataSource);
			}
		}
		return dataSources;
	}
	
	public static List<DataSource> obtainDataSourcesProject() { 
		DataSource dataSource1 = new DataSource(DataSourceTypeEnum.Text, PROJECT.key, "nome", true, true, false);
		ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
		dataSources.add(dataSource1);

		DataSource dataSource2 = new DataSource(DataSourceTypeEnum.Combobox, TEMPLATE_ANDROID.kind, "android template", false, false, false);
		dataSources.add(dataSource2);
		
		DataSource dataSourceObservations = new DataSource(DataSourceTypeEnum.Text, "observations", "observações", true, true, false);
		dataSources.add(dataSourceObservations);
		
		DataSource dataSourceState = new DataSource(DataSourceTypeEnum.Combobox, STATE.key, "estado", false, false, false); 
		dataSources.add(dataSourceState);
		
		DataSource dataSourceCity = new DataSource(DataSourceTypeEnum.Combobox, CITY.key, "cidade", false, false, false); 
		dataSourceState.setDependent(dataSourceCity);
		return dataSources;
	}

}
