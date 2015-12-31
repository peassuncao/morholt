package br.fapema.morholt.web.client.gui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import br.fapema.morholt.web.client.gui.exception.MyUncaughtExceptionHandler;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.TableEnum;
import br.fapema.morholt.web.shared.TemplateReader;
import br.fapema.morholt.web.shared.exception.MyQuotaException;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MyEntryPoint implements EntryPoint {

	public static String URL_STRING = obtainDefautRootURL();
	static final Logger log = Logger.getLogger(MyEntryPoint.class.getName());
	private MyServiceClientImpl myServiceClientImpl;
	private Map<String, List<DataSource>> mapCollectTemplateToDatasources;
	private Dictionary loginInfo;
	public List<Model> templateModels;
	
	  public static String obtainLocalIp()  {
		  String ip = GWT.getModuleBaseURL(); //FIXME server, also do not log at this place
	//	  String ip = "127.0.0.1"; //FIXME server 
		  return ip;
	  }
	  
	  
	/**
	 * This is the entry point method. 
	 */
	public void onModuleLoad() {
	    GWT.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

		loginInfo = Dictionary.getDictionary("info");
		
		String url = GWT.getModuleBaseURL() + "myService";  // FIXME server
	//	String url = URL_STRING + "/arqueologia_appengine/" + "myService";
		log.info("service url: " + url);
		
		myServiceClientImpl = new MyServiceClientImpl(url);
		myServiceClientImpl.doLogin(loginInfo.get("email"), Ajax.call(new LoginCallBack(), "doLogin"));
		myServiceClientImpl.setUser(getUserFromJavascript());
	}
	
	private void startClient() {
		MainView mainView = new MainView(loginInfo, MyEntryPoint.this.myServiceClientImpl, mapCollectTemplateToDatasources, templateModels);
		MyEntryPoint.this.myServiceClientImpl.setMainView(mainView);
		
		RootLayoutPanel rootPanel = RootLayoutPanel.get();
		rootPanel.setStylePrimaryName("rootPanel");
		rootPanel.setSize("100%", "100%");
		rootPanel.add(mainView);
	}
	
	public WebUser getUserFromJavascript() {
  		String email = loginInfo.get("email");
  		WebUser user = new WebUser(email);
  		myServiceClientImpl.setUser(user);
  		return user;
	}
	
	public class LoginCallBack implements AsyncCallback<Profile> {
		@Override
		public void onFailure(Throwable caught) {
			log.severe( "onFailure: ");
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());

			if(caught instanceof MyQuotaException) 
				Window.alert("Site ultrapassou a quota do dia. (contate o administrador)");
			else
				Window.alert("Falha de inicialização - login");
		}
		@Override
		public void onSuccess(Profile profile) {
			WebUser user = myServiceClientImpl.getUser();
			user.setProfile(profile);
			Object tempTemplatePermission = profile.getProfileModel().getContentValues().get(TableEnum.TEMPLATE_ANDROID.kind + CRUDEL.LIST);
			profile.getProfileModel().getContentValues().put(TableEnum.TEMPLATE_ANDROID.kind + CRUDEL.LIST, true);
			myServiceClientImpl.listItem(TableEnum.TEMPLATE_ANDROID.kind, TableEnum.TEMPLATE_ANDROID.key, null, null, 0, null, "EntryPointTemplate", Ajax.call(new TemplateAndroidCallBack(), "readTemplate"));
			profile.getProfileModel().getContentValues().put(TableEnum.TEMPLATE_ANDROID.kind + CRUDEL.LIST, tempTemplatePermission);
			
			Window.setTitle("Instituto Sítio do Físico");
		}
	}
	
	public class TemplateAndroidCallBack implements AsyncCallback<List<Model>> {
		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("Falha de inicialização  ");
		}

		@Override
		public void onSuccess(List<Model> templateModels) {
			updateMapCollectTemplateToDatasources(templateModels);
			MyEntryPoint.this.templateModels = templateModels;
			startClient();
		}
		
		private void updateMapCollectTemplateToDatasources(List<Model> templateModels) {
			HashMap<String, List<DataSource>> mapTemplateToDatasources = new HashMap<String, List<DataSource>>();
			for (Model modelTemplate : templateModels) {
				mapTemplateToDatasources.putAll(TemplateReader.generateTemplateToDatasources(modelTemplate));
			}
			MyEntryPoint.this.mapCollectTemplateToDatasources = mapTemplateToDatasources;
		}
	}
	  
	  private static String obtainDefautRootURL()  {
		  return "http://" + obtainLocalIp() + ":8888"; 
	  }
}