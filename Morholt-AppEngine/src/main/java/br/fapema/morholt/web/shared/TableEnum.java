package br.fapema.morholt.web.shared;

public enum TableEnum {
	PROJECT("project", "name"), PROFILE("profile", "name"),
	USERS("user", "Name"), TEMPLATE_ANDROID("androidTemplate", "name"),
	ALLOCATION("allocation", "email"), COLLECT("collect", "np"), HISTORIC("historic", null),
	UPLOAD_CSV("uploadCSV", null), STATE("state", "state"), CITY("cidade", "nome"); 

	public String kind;
	public String key;
	TableEnum(String kind, String key) {
		this.kind = kind;
		this.key = key;
	}
}
