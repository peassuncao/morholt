package br.fapema.morholt.android.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import br.fapema.morholt.android.initial.load.TwoColumnListViewInterface;

/**
 * this is the data pre set on server
 * @author pedro
 *
 */
@Table(name = "PreData")
public class PreData extends Model implements Serializable{
	
	/**
	 * not to be used directly (just for ActiveAndroid)
	 */
	public PreData() {
	}
		
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8175043879541824343L;
	

	
	@Column(name = "colector")
	private String colector;
	@Column(name = "etapa")
	private String etapa="coleta";
	@Column(name = "dateCollect", index=true)
	private String dateCollect;
	@Column(name = "sitio")
	private String sitio;
	
	
	
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public String getDateCollect() {
		return dateCollect;
	}
	public void setDateCollect(String data) {
		this.dateCollect = data;
	}
	public String getSitio() {
		return sitio;
	}
	public void setSitio(String sitio) {
		this.sitio = sitio;
	}

	public String getColector() {
		return colector;
	}
	public void setColector(String colector) {
		this.colector = colector;
	}
}
