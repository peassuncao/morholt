package br.fapema.morholt.web.client.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

import br.fapema.morholt.collect.SimpleString;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.MainView;
import br.fapema.morholt.web.client.gui.MyEntryPoint;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.shared.BlobURL;
import br.fapema.morholt.web.shared.FilterInterface;

public class MyServiceClientImpl {
	private static final Logger log = Logger.getLogger(MyServiceClientImpl.class.getName());
	
	private MyServiceAsync service;
	private MainView mainView;
	private WebUser user;
	
	public MyServiceClientImpl(String url) {
		service = GWT.create(MyService.class);
		
		((ServiceDefTarget)service).setServiceEntryPoint(url);
		

	// timeout, letting the server do this 
	//	  CustomRpcRequestBuilder builder = new CustomRpcRequestBuilder();
	//    ((ServiceDefTarget)service).setRpcRequestBuilder(builder);
	}

	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}
	
	public void doLogin(String userEmail, AsyncCallback<Profile> callback) {
		service.doLogin(userEmail, callback);
	}


	public void getBlobURL(AsyncCallback<BlobURL> callback) {
	  service.getBlobURL(user, callback);
	}
	
	public void readDatasources(AsyncCallback<Map<String, List<DataSource>>> callback) {
		log.info("readDatasources");
		service.readDatasources(user, callback);
		log.info("readDatasources called");
	}

	public void listItem(String kind, String keyName, String ancestor, String startCursor, int limit, String sortProperty, String callerName, AsyncCallback callback) {
		log.info("listItem");
		if(callback == null) {
			callback = new DefaultCallback(kind, keyName);
		}
		service.listItem(user, kind, keyName, ancestor, startCursor, limit, sortProperty, callerName, callback);
		log.info("listItem done");
	}

/*
	public void listItemEqual(String kind, String keyname, String ancestor, String startCursor, int limit, String propertyName, String property, 
			String sortProperty, AsyncCallback callback) {
		log.info("listItemEqual");
		if(callback == null) {
			callback = new DefaultCallback(kind, keyname);
		}
		service.listItemEqual(user, kind, keyname, ancestor, startCursor, limit, propertyName, property, sortProperty, callback);
		log.info("listItemEqual done");
	}
	*/
	
	public void listItemEqual(String kind, String keyname, String ancestor, String startCursor, int limit, FilterInterface filterInterface, 
			List<String> sortProperty, List<String> sortDirection, AsyncCallback callback) {
		log.info("listItemEqual");
		if(callback == null) {
			callback = new DefaultCallback(kind, keyname);
		}
	//	log.info("user: " + user);
		service.listItemEqual(user, kind, keyname, ancestor, startCursor, limit, filterInterface, sortProperty, sortDirection, callback);
		log.info("listItemEqual done");
	}

	public MainView getMainView() {
		return mainView;
	}
	
	private class DefaultCallback implements AsyncCallback { //TODO remove default callbacks, always use a specific one instead
		private String kind;
		private String keyName;
		public DefaultCallback(String kind, String keyName) {
			this.kind = kind;
			this.keyName = keyName;
		}
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("error" + caught.getMessage());
			System.out.println("Erro no callback.");
			log.info("Erro no callback " +  caught.getStackTrace());
		}

		@Override
		public void onSuccess(Object result) {
			System.out.println("Sucesso no callback.");
			log.info("Sucesso no callback.");
			
			if (result != null && result instanceof List<?>) {
				List<Model> list = (List<Model>) result;
				mainView.collectWasUpdated(kind, keyName, list);
			}

		}
	}
	
	private class CSVCallback implements AsyncCallback {
		private String kind;
		private String keyName;
		private String exportId;
		public CSVCallback(String kind, String keyName, String exportId) {
			this.kind = kind;
			this.keyName = keyName;
			this.exportId = exportId;
		}
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("Erro no callback.");
			log.info("Erro no callback " +  caught.getStackTrace());
			throw new RuntimeException(caught);
		}

		@Override
		public void onSuccess(Object result) {
			System.out.println("Sucesso no callback.");
			log.info("Sucesso no callback.");
			final String url = GWT.getHostPageBaseURL() + "csv?exportId="+exportId;
			log.info("download url: " + url);
			log.info("\nexportId: " + exportId);
			final Frame f = new Frame();
			  f.setUrl(url);
			  // Set a size of 0px unless you want the file be displayed in it
			  // For .html images .pdf, etc. you must configure your servlet
			  // to send the Content-Disposition header
			  f.setSize("0px", "0px");
			  
			  RootPanel.get().add(f);
			  // Configure a timer to remove the element from the DOM
			  new Timer() {
			    public void run() {
			      f.removeFromParent();
			    }
			  }.schedule(10000);
		}
	}
	
	
	

	private void putLastUpdateOnSystem(Map<String, Object> map) {
		Date date = new Date();
		map.put("lastUpdateOnSystem", date); 
	}
	
	public void insert(String table, String parentKey, Map<String, Object> map,
			final AsyncCallback<SimpleString> mycallBack) {
		if (mycallBack == null)
			throw new IllegalArgumentException("on insert");

		putLastUpdateOnSystem(map);

		final Request r = service.insert(user, table, parentKey, map, mycallBack);
	}

	/**
	 * 
	 * @param kind
	 * @param keyValue
	 * @param call
	 * @return keyValues
	 */
	public void delete(String kind, String keyValue, AsyncCallback call) {
		service.delete(user, kind, keyValue, call);
		
	}
	
	public void listAllocation(String projectId, String sortProperty, AsyncCallback callback) {
		log.info("listAllocation for: " + projectId);
		if(callback == null) {
			throw new RuntimeException("null callback on listAllocation");
		}
		service.listAllocation(user, projectId, sortProperty, callback);
		log.info("listAllocation done");
	}

	public void insertAllocations(String projectId, ModelList movedUsers, AsyncCallback<List<String>> callback) {
		ArrayList<Map<String, String>> rows = obtainAllocationRows(projectId,
				movedUsers);
		service.insertAllocations(user, rows, callback);
	}

	public void removeAllocations(List<String> stringKeys) { 
		service.removeAllocations(user, stringKeys, new DoNothingCallback()); 
	}
	
	private ArrayList<Map<String, String>> obtainAllocationRows(
			String projectId, ModelList movedUsers) {
		ArrayList<Map<String, String>> rows = new ArrayList<Map<String, String>>();
		
		for (Model user : movedUsers) {
			HashMap<String, String> columns = new HashMap<String, String>();
			columns.put("projectId", projectId);
			columns.put("user", user.get("Name")); //TODO needed?
			columns.put("email", user.get("email"));
			rows.add(columns);
		}
		return rows;
	}

	public void setUser(WebUser user) {
		this.user = user;
	}

	public WebUser getUser() {
		return user;
	}
	
	public Profile getProfile() {
		return user.getProfile();
	}

	public static Double exportTicket = 0.0;
	public void export(String kind, String keyname, String ancestor, String startCursor, int limit, FilterInterface filterInterface, 
			List<String> sortProperty, List<String> sortDirection, AsyncCallback callback) {
		log.info("export");
		
		String exportId = String.valueOf(exportTicket) + user.getEmail();
		callback = new CSVCallback(kind, keyname, exportId);
	    
		service.export(exportId, user, kind, keyname, ancestor, startCursor, limit, filterInterface, sortProperty, sortDirection, Ajax.call(callback, kind+"_export"));
		exportTicket++;
		log.info("export done");
	}

	
}