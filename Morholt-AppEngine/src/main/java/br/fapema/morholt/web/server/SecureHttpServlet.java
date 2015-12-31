package br.fapema.morholt.web.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.shared.exception.MyQuotaException;

public abstract class SecureHttpServlet extends HttpServlet {

	private static final long serialVersionUID = 8646951557626992683L;

	private MyServiceImpl myServiceClientImpl;
	static final Logger log = Logger.getLogger(SecureHttpServlet.class.getName());
	protected String url;
	
	 public void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws IOException, ServletException {
		 doSomething(req, resp);
	 }
	 
	 private void doSomething (HttpServletRequest req, HttpServletResponse resp) {
		 String email = req.getParameter("email");
			String allurl = req.getRequestURL().toString();
			String url = allurl.substring(0, allurl.length() - req.getRequestURI().length());
			url += "/myService"; 
			log.info("url: " + url);
		
			myServiceClientImpl = new MyServiceImpl();
			try {
				Profile profile = myServiceClientImpl.doLogin(email);
		  		WebUser user = new WebUser(email, profile);
				try {
					authorize(user);
					afterLogin(req, resp, user);
				} catch (IOException e) {
					log.log(Level.SEVERE, "afterLoginError: " + e.getMessage());
					throw new RuntimeException(e);
				} catch (AuthorizationException e) {
					log.log(Level.SEVERE, "authorize error: " + e.getMessage());
					throw new RuntimeException(e);
				}
			} catch (MyQuotaException e) {
				log.log(Level.SEVERE, "quota error: " + e.getMessage());
			}
	 }
	 
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		 doSomething(req, resp);
	}

	protected abstract void authorize(WebUser user) throws AuthorizationException;
	protected abstract void afterLogin(HttpServletRequest req, HttpServletResponse resp, WebUser user) throws IOException;
}