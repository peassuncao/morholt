package br.fapema.morholt.web.shared;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;

public abstract class Authority {

	private static final Logger log = Logger.getLogger(Authority.class.getName());
	
	public static void authorize(Profile profile, String kind, Profile.CRUDEL crudel) throws AuthorizationException {
		if(profile == null) {
			log.info("authorize-> null profile");
			throw new AuthorizationException("null profile");
		}
		boolean ok = profile.authenticate(kind, crudel);
		if(!ok) {
			log.info("authorize not ok -> profile: " + profile + " kind: " + kind + " CRUDEL: " + crudel);
			throw new AuthorizationException("authorize not ok");
		}
	}
	
	public static void authenticate(WebUser user) throws AuthorizationException {
		log.log(Level.FINE, "authenticate");
		if (user == null) {
			log.info("authentication, user is null");
			throw new AuthorizationException("authorization failed!");
		}
		else {
			//FIXME remove
			log.log(Level.FINE, "authenticated, user email: " + user.getEmail());
		}
	}

}
