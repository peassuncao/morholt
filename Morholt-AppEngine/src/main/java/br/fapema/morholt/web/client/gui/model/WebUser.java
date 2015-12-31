package br.fapema.morholt.web.client.gui.model;

import java.io.Serializable;

public class WebUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8455212572646875425L;
	private String email;
	private Profile profile;
	
	public WebUser() {
		
	}
	
	public WebUser(String email) {
		this.email = email;
	}
	
	public WebUser(String email, Profile profile) {
		this.email = email;
		this.profile = profile;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	@Override
	public String toString() {
		return "WebUser-> email: " + email + "profile: " + profile;
	}
}
