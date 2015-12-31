package br.fapema.morholt.web.client.gui.model;

import java.io.Serializable;

import com.google.gwt.user.client.Window;



public class Profile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5701317612385780824L;



	public enum CRUDEL {
		CREATE, READ, UPDATE, DELETE, ENTER, LIST, EXPORT, FEATURE
	};
	
	
	private Model profileModel;
	
	public Profile() {
	}
	
	public void setModel(Model profileModel) {
		this.profileModel = profileModel;
	}
	public Model getProfileModel() {
		return profileModel;
	}
	public String getName() {
		return profileModel.getKeyValue();
	}
	public boolean isAnonumous() {
		return getName().equals("anonymous");
	}
	
	
	
	public boolean authenticate(String kind, CRUDEL crude) {
		return profileModel.getBoolean(kind+crude);
	}
}
