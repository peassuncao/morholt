package br.fapema.morholt.web.server;
import static br.fapema.morholt.web.shared.TableEnum.ALLOCATION;
import static br.fapema.morholt.web.shared.TableEnum.PROFILE;
import static br.fapema.morholt.web.shared.TableEnum.USERS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entities;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import br.fapema.morholt.collect.CollectEndpoint;
import br.fapema.morholt.collect.SimpleString;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.exception.AuthenticationException;
import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.exception.UserNotAllocatedToProjectException;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.client.service.MyService;
import br.fapema.morholt.web.shared.Authority;
import br.fapema.morholt.web.shared.BlobURL;
import br.fapema.morholt.web.shared.FilterInterface;
import br.fapema.morholt.web.shared.MyFilterPredicate;
import br.fapema.morholt.web.shared.MyFilterPredicate.MyFilterOperator;
import br.fapema.morholt.web.shared.TableEnum;
import br.fapema.morholt.web.shared.TemplateReader;
import br.fapema.morholt.web.shared.exception.MyQuotaException;
public class MyServiceImpl extends RemoteServiceServlet implements MyService{

	private static final String ANONYMOUS = "anonymous";
	private static final long serialVersionUID = -6387961659373356900L;
	private static final Logger log = Logger.getLogger(MyServiceImpl.class.getName());
	
	
	@Override
	public BlobURL getBlobURL(WebUser user) throws Exception {

		CollectEndpoint coletaEndpoint = new CollectEndpoint();
		BlobURL blobURL = coletaEndpoint.getURLBlobWeb(transformUser(user), "/webImageUpload");
		return blobURL;
	}
	
	
	/**
	 * @return mapCollectTemplateToDatasources
	 */
	@Override
	public Map<String, List<DataSource>> readDatasources(WebUser user) throws AuthorizationException {
		log.info("readDataSource");
		 Authority.authenticate(user);
		
		ModelList modelTemplateList = doList(TableEnum.TEMPLATE_ANDROID.kind, TableEnum.TEMPLATE_ANDROID.key, null, null, null, null, null, null, "readDataSource");
		HashMap<String, List<DataSource>> mapTemplateToDatasources = new HashMap<String, List<DataSource>>();
		for (Model modelTemplate : modelTemplateList) {
			mapTemplateToDatasources.putAll(TemplateReader.generateTemplateToDatasources(modelTemplate));
		}
		return mapTemplateToDatasources; 
	}

	private User transformUser(WebUser webUser) {
		return new User(webUser.getEmail(), "gmail.com");
	}
	
	public static ModelList doList (String akind, String akeyname, String startCursor, Integer limit, String ancestor, FilterInterface filterInterface, List<String> sortProperties, List<String> sortDirections, String callerName) {
		log.log(Level.FINER, "doList");
	    FetchOptions fetchOptions;

	    if(limit == null || limit == 0) {
	    	fetchOptions = FetchOptions.Builder.withDefaults();
	    }
	    else {
	    	fetchOptions = FetchOptions.Builder.withLimit(limit);
	    }
	    
	    if (startCursor != null) {
	      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
	    }
	    
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query(akind);
		
		if(filterInterface != null) {
			query.setFilter(FilterConversor.convert(filterInterface));
		}
		
		if(sortProperties != null && !sortProperties.isEmpty()) {
			for (int i=0; i< sortProperties.size(); i++) {
				
				if(sortProperties.get(i) == null) continue;
				Query.SortDirection direction = (sortDirections == null || sortDirections.get(i) == null) ? 
						SortDirection.ASCENDING : Query.SortDirection.valueOf(sortDirections.get(i));
				
				query.addSort(sortProperties.get(i), direction);
			}
			/*
			if(sortDirection == null || sortDirection.equals("Asc")) query.addSort(sortProperty);
			else query.addSort(sortProperty, SortDirection.DESCENDING);*/
		}
		log.info("callerName: " + callerName + " query: " + query+"");
		//MyApplication.
		PreparedQuery preparedQuery = datastore.prepare(query);
		QueryResultList<Entity> results = preparedQuery.asQueryResultList(fetchOptions);

		ModelList list = new ModelList();
		for (Entity result : results) {
		  Map<String, Object> properties = result.getProperties();
		  HashMap<String, Object> hashMap = new HashMap<String, Object>();
		  for (String key : properties.keySet()) {
			  hashMap.put(key, (Object) properties.get(key));
		  }
		  hashMap.put("Key", KeyFactory.keyToString(result.getKey()));
		  
		  list.add(new Model(hashMap, akeyname, akind));
		}
		  list.setColumns(obtainColumns(akind, datastore));
		  
		
		return list;
	}

	private static ArrayList<String> obtainColumns(String akind, DatastoreService datastore) {
		Query q = new Query(Entities.PROPERTY_METADATA_KIND).setKeysOnly();
		  q.setAncestor(Entities.createKindKey(akind));
		  ArrayList<String> columns = new ArrayList<String>();
		  for (Entity e : datastore.prepare(q).asIterable()) {
			  columns.add(e.getKey().getName());
		  }
		  return columns;
	}

	
	/**
	 * 
	 * @param webUser
	 * @param kind
	 * @param keyname
	 * @param ancestor
	 * @param startCursor
	 * @param limit
	 * @return
	 * @throws AuthorizationException
	 */
	@Override
	public ModelList listItem(WebUser webUser, String kind, String keyname, String ancestor, String startCursor, int limit, String sortProperty, String callerName) throws AuthorizationException {
		log.log(Level.FINE, "listItem on: " + kind);
		ModelList collectionResponse = null;
		try {
			 Authority.authenticate(webUser);
			 Authority.authorize(webUser.getProfile(), kind, Profile.CRUDEL.LIST);
			collectionResponse = MyServiceImpl.doList(kind, keyname, startCursor, limit, ancestor, null, Collections.singletonList(sortProperty), null, callerName);
		} catch (AuthorizationException e) {
			throw new AuthorizationException("Failed on user authorization: " + webUser);
		}
		log.log(Level.FINE, "listItem done");
		return collectionResponse;
	}

	@Override
	public ModelList listItemEqual(WebUser webUser, String kind, String keyname, String ancestor, String startCursor, int limit, FilterInterface filterInterface, List<String> sortProperty, List<String> sortDirection) throws AuthorizationException {
		log.log(Level.FINE, "listItemEqual on: " + kind);
		ModelList collectionResponse = null;
		try {
			 Authority.authenticate(webUser);
			 Authority.authorize(webUser.getProfile(), kind, Profile.CRUDEL.LIST);
			collectionResponse = MyServiceImpl.doList(kind, keyname, startCursor, limit, ancestor, filterInterface, sortProperty, sortDirection, "listItemEqual");
		} catch (AuthorizationException e) {
			throw new AuthorizationException("Failed on user authorization: " + webUser);
		}
		log.log(Level.FINE, "listItem done");
		return collectionResponse;
	}
	
	public static Map<String, ModelList> mapTicketModel = new HashMap<String, ModelList>();
	
	@Override
	public boolean export(String exportId, WebUser webUser, String kind, String keyname, String ancestor, String startCursor, int limit, FilterInterface filterInterface, List<String> sortProperty, List<String> sortDirection) throws AuthorizationException {
		log.info("export on: " + kind);
		Double ticket = null;
		ModelList collectionResponse = null;
		try {
			 Authority.authenticate(webUser);
			 Authority.authorize(webUser.getProfile(), kind, Profile.CRUDEL.EXPORT);
			collectionResponse = MyServiceImpl.doList(kind, keyname, startCursor, limit, ancestor, filterInterface, sortProperty, sortDirection, "export");
			
			mapTicketModel.put(exportId, collectionResponse);
			log.info("export done");
			return true;
		}
		catch(Exception e) {
			log.info("error on export:" + e.getMessage());
			return false;
		}
	}
	
	@Override
	public Profile doLogin(String userEmail) throws MyQuotaException {
		log.log(Level.FINE, "doLogin on: " + userEmail);
		List<Model> usersWithSearchedEmail = null;
		Model profileModel = null;
		MyFilterPredicate filterPredicate = new MyFilterPredicate("email", MyFilterOperator.EQUAL, userEmail);
		usersWithSearchedEmail = MyServiceImpl.doList(
				USERS.kind, USERS.key, null, null, null, filterPredicate,
				null, null, "doLogin");

		if (usersWithSearchedEmail.isEmpty()) {
			
			if(userEmail.equals("test@example.com")) { //FIXME
				HashMap<String, Object> profile = new HashMap<String, Object>();
				profile.put("name", "admin");
				
				for (TableEnum tableEnum : TableEnum.values()) {
					CRUDEL[] crudes = Profile.CRUDEL.values();
					for (CRUDEL crude : crudes) {
						profile.put(tableEnum.kind + crude, true);
					}
				}
				DAO.insert(PROFILE.kind, null, profile);
				

				HashMap<String, Object> user = new HashMap<String, Object>();
				user.put("Name", "Usuario de teste");
				user.put("email", "test@example.com");
				user.put("profile", "admin");
				DAO.insert(USERS.kind, null, user);
				return doLogin(userEmail);
			}
			else if(userEmail.equals("xxx@gmail.com")) { //FIXME git
				HashMap<String, Object> profile = new HashMap<String, Object>();
				profile.put("name", "admin");
				
				for (TableEnum tableEnum : TableEnum.values()) {
					CRUDEL[] crudes = Profile.CRUDEL.values();
					for (CRUDEL crude : crudes) {
						profile.put(tableEnum.kind + crude, true);
					}
				}
				DAO.insert(PROFILE.kind, null, profile);

				HashMap<String, Object> user = new HashMap<String, Object>();
				user.put("Name", "Bhola");
				user.put("email", "peassuncao@gmail.com");
				user.put("profile", "admin");
				DAO.insert(USERS.kind, null, user);
				log.log(Level.FINE, "done email: peassuncao");
				return doLogin(userEmail);
			}
			else if(userEmail.equals("anonymous@ositiodofisico.appspot.com")) { //FIXME
				
				log.info("email: anonymous@ositiodofisico.appspot.com");
				HashMap<String, Object> profile = new HashMap<String, Object>();
				profile.put("name", "anonymous");
				
				for (TableEnum tableEnum : TableEnum.values()) {
					CRUDEL[] crudes = Profile.CRUDEL.values();
					for (CRUDEL crude : crudes) {
						profile.put(tableEnum.kind + crude, false);
					}
				}
				profile.put("collectLIST", true);
				profile.put("collectREAD", true);
				profile.put("collectENTER", true);
				profile.put("projectLIST", true);
				profile.put("projectREAD", true);
				profile.put("projectENTER", true);
				profile.put("androidTemplateLIST", true);
				
				DAO.insert(PROFILE.kind, null, profile);

				HashMap<String, Object> user = new HashMap<String, Object>();
				user.put("Name", "Anônimo");
				user.put("email", "anonymous@ositiodofisico.appspot.com");
				user.put("profile", "anonymous");
				DAO.insert(USERS.kind, null, user);
				log.log(Level.FINE, "done email: anonymous");
				return doLogin(userEmail);
			}
			
			else {
			
				filterPredicate = new MyFilterPredicate(PROFILE.key, MyFilterOperator.EQUAL, ANONYMOUS);
				profileModel = MyServiceImpl.doList(
						PROFILE.kind, PROFILE.key, null, null, null,
						filterPredicate, null, null, "doLogin").get(0);
				log.info("anonymous login");
			}
		} else {
			Model user = usersWithSearchedEmail.get(0);
			String profileName = user.get("profile");


			filterPredicate = new MyFilterPredicate(PROFILE.key, MyFilterOperator.EQUAL, profileName);
			profileModel = MyServiceImpl.doList(
					PROFILE.kind, PROFILE.key, null, null, null,
					filterPredicate, null, null, "doLogin").get(0);

		}
		Profile result = new Profile();
		result.setModel(profileModel);
		
		log.log(Level.FINE, "doLogin done");
		
		return result;
	}


	@Override
	public SimpleString insert(WebUser webUser, String table, String parentKey, Map<String, Object> map) throws AuthorizationException, MyQuotaException, UserNotAllocatedToProjectException {
		log.info("insert on " + table);

		CollectEndpoint coletaEndpoint = new CollectEndpoint();
		SimpleString result = null;
		try {
			result = coletaEndpoint.insertMyContentValues(transformUser(webUser), table, parentKey, map);
		} catch (AuthenticationException e) {
			log.info(e.getMessage());
			log.info("Failed on user authentication: " + webUser);
			throw new AuthorizationException("Failed on user authentication: " + webUser);
		}
		log.info("insert done");
		return  result;
	}
	
	public void checkUserIsAlocated(String projectId, WebUser webUser) throws UserNotAllocatedToProjectException, AuthorizationException  {
		List<Model> listAllocation = listAllocation(webUser, projectId, null);
		if (listAllocation.isEmpty()) {
			log.info("checkUserIsAlocated error-> projectId: " + projectId + " webUser: " + webUser );
			throw new UserNotAllocatedToProjectException("usuário não alocado para este projeto");
		}
	}
	
	@Override
	public List<Model> listAllocation(WebUser webUser, String projectId,
			String sortProperty) throws AuthorizationException {
		log.info("listAllocation");
		List<Model> result = null;
		try {
			 Authority.authenticate(webUser);
			 Authority.authorize(webUser.getProfile(), ALLOCATION.kind,
					Profile.CRUDEL.LIST);

			MyFilterPredicate filterPredicate = new MyFilterPredicate(
					"projectId", MyFilterOperator.EQUAL, projectId);
			result = MyServiceImpl.doList(ALLOCATION.kind, ALLOCATION.key,
					null, null, null, filterPredicate,
					Collections.singletonList(sortProperty), null, "listAllocation");
		} catch (AuthorizationException e) {
			log.info("error on listAllocation: " + e.getStackTrace());
			throw new AuthorizationException("Failed on user authorization: "
					+ webUser);
		}
		log.info("listAllocation done");
		return result;
	}

	@Override
	public List<String> insertAllocations(WebUser webUser, ArrayList<Map<String, String>> allocationRows) throws AuthorizationException, MyQuotaException {
		 Authority.authenticate(webUser);
		 Authority.authorize(webUser.getProfile(), ALLOCATION.kind, Profile.CRUDEL.CREATE);
		 return DAO.insertMult(ALLOCATION.kind, ALLOCATION.key, allocationRows); 
	}
	
	@Override
	public void removeAllocations(WebUser webUser, List<String> stringKeys) throws AuthorizationException, MyQuotaException {
		 Authority.authenticate(webUser);
		 Authority.authorize(webUser.getProfile(), ALLOCATION.kind, Profile.CRUDEL.DELETE);
		DAO.removeMult(ALLOCATION.kind, stringKeys); 
	}
	
	@Override
	public String delete(WebUser user, String kind, String keyValue) throws AuthorizationException, MyQuotaException {
		 Authority.authenticate(user);
		 Authority.authorize(user.getProfile(), kind, Profile.CRUDEL.DELETE);
		DAO.delete(kind, keyValue, user);
		return keyValue;
	}



}
