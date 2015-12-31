package br.fapema.morholt.android.model;


import java.io.Serializable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Used only before sending data, just one user is kept per smartphone
 * @author pedro
 *
 */
@Table(name = "User")
public class User extends Model implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8992782987777919113L;
	/**
	 * not to be used directly (just for ActiveAndroid)
	 */
	public User() {
	}
	
	@Column(name = "name")
	private String name;
	@Column(name = "password")
	private String password;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		save();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		save();
	}
}