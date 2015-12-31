package br.fapema.morholt.web.client.gui.model;

public class LabelValueModel {
	
	private String label, value;
	
	public LabelValueModel() {
	}

	public LabelValueModel(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}