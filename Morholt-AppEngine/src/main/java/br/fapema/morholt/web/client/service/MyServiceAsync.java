package br.fapema.morholt.web.client.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

import br.fapema.morholt.collect.SimpleString;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.shared.BlobURL;
import br.fapema.morholt.web.shared.FilterInterface;

public interface MyServiceAsync {
	public void doLogin(String userEmail, AsyncCallback<Profile> callback);
	public void listItem(WebUser user, String kind, String keyName, String ancestor,  
			String startCursor, int limit, String sortProperty, String callerName, AsyncCallback<List<Model>> callback);
	public Request insert(WebUser user, String table,
			String parentKey, Map<String, Object> map, AsyncCallback<SimpleString> mycallBack);
	public void listAllocation(WebUser user, String projectId, String sortProperty, AsyncCallback<List<Model>> callback); //TODO callback type..
	public void insertAllocations(WebUser user, ArrayList<Map<String, String>> rows, AsyncCallback<List<String>> callback); 
	public void removeAllocations(WebUser user, List<String> stringKeys, AsyncCallback callback);
	public void readDatasources(WebUser user, AsyncCallback<Map<String, List<DataSource>>> callback);
	public void listItemEqual(WebUser webUser, String kind, String keyname,
			String ancestor, String startCursor, int limit,
			FilterInterface filterInterface, List<String> sortProperty,
			List<String> sortDirection, AsyncCallback<ModelList> callback);
	public void delete(WebUser user, String kind, String keyValue,
			AsyncCallback call);
	void export(String exportId, WebUser webUser, String kind, String keyname, String ancestor, String startCursor, int limit,
			FilterInterface filterInterface, List<String> sortProperty, List<String> sortDirection,
			AsyncCallback<Boolean> callback);
	public void getBlobURL(WebUser user, AsyncCallback<BlobURL> callback);
}
