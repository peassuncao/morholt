package br.fapema.morholt.collect;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Collect implements Serializable{
	
	@javax.persistence.Transient
	private static final long serialVersionUID = -995434988621269336L;
	@Id
	private String id;
	private String coletor;
	private String etapa;
	private String data;
	private String sitio;
	private String material;
	private String materialOutro;
	private String NP;
	private String qtd;
	private String origem;
	private String origemOutra;
	public String getMaterialOutro() {
		return materialOutro;
	}
	public void setMaterialOutro(String materialOutro) {
		this.materialOutro = materialOutro;
	}
	public String getOrigemOutra() {
		return origemOutra;
	}
	public void setOrigemOutra(String origemOutra) {
		this.origemOutra = origemOutra;
	}
	private String intervencao;
	private String nivel;
	private String zona;
	private String x;
	private String y;
	private String foto;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getColetor() {
		return coletor;
	}
	public void setColetor(String coletor) {
		this.coletor = coletor;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getSitio() {
		return sitio;
	}
	public void setSitio(String sitio) {
		this.sitio = sitio;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getNP() {
		return NP;
	}
	public void setNP(String nP) {
		NP = nP;
	}
	public String getQtd() {
		return qtd;
	}
	public void setQtd(String qtd) {
		this.qtd = qtd;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getIntervencao() {
		return intervencao;
	}
	public void setIntervencao(String intervencao) {
		this.intervencao = intervencao;
	}
	public String getNivel() {
		return nivel;
	}
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	public String getZona() {
		return zona;
	}
	public void setZona(String zona) {
		this.zona = zona;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
}
