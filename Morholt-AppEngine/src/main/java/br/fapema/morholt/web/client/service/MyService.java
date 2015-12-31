package br.fapema.morholt.web.client.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.users.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import br.fapema.morholt.collect.SimpleString;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.exception.UserNotAllocatedToProjectException;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.shared.BlobURL;
import br.fapema.morholt.web.shared.FilterInterface;
import br.fapema.morholt.web.shared.exception.MyQuotaException;

@RemoteServiceRelativePath("myService")
public interface MyService extends RemoteService {
	public Profile doLogin(String userEmail) throws MyQuotaException;

	public List<Model> listItem(WebUser user, String kind, String keyName,
			String ancestor, String startCursor, int limit, String sortProperty, String callerName) throws AuthorizationException;

	public List<Model> listAllocation(WebUser user, String projectId, String sortProperty) throws AuthorizationException;

	public  List<String> insertAllocations(WebUser user, ArrayList<Map<String, String>> rows) throws AuthorizationException, MyQuotaException;

	public void removeAllocations(WebUser user, List<String> stringKeys) throws AuthorizationException, MyQuotaException;

	Map<String, List<DataSource>> readDatasources(WebUser user) throws AuthorizationException;

	public SimpleString insert(WebUser user, String table, String parentKey,
			Map<String, Object> map) throws AuthorizationException, MyQuotaException, UserNotAllocatedToProjectException;

	ModelList listItemEqual(WebUser webUser, String kind, String keyname,
			String ancestor, String startCursor, int limit,
			FilterInterface filterInterface, List<String> sortProperty, List<String> sortDirection) throws AuthorizationException;

	public String delete(WebUser user, String kind, String keyValue) throws AuthorizationException, MyQuotaException;

	boolean export(String exportId, WebUser webUser, String kind, String keyname, String ancestor, String startCursor, int limit,
			FilterInterface filterInterface, List<String> sortProperty, List<String> sortDirection)
					throws AuthorizationException;

	public BlobURL getBlobURL(WebUser user) throws Exception;
}
