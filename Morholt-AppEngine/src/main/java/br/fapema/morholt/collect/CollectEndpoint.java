package br.fapema.morholt.collect;

import static br.fapema.morholt.web.shared.TableEnum.ALLOCATION;
import static br.fapema.morholt.web.shared.TableEnum.PROJECT;
import static br.fapema.morholt.web.shared.TableEnum.TEMPLATE_ANDROID;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import br.fapema.morholt.web.client.gui.exception.AuthenticationException;
import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.exception.UserNotAllocatedToProjectException;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.server.DAO;
import br.fapema.morholt.web.server.MyServiceImpl;
import br.fapema.morholt.web.shared.BlobURL;
import br.fapema.morholt.web.shared.MyFilterPredicate;
import br.fapema.morholt.web.shared.MyFilterPredicate.MyFilterOperator;
import br.fapema.morholt.web.shared.exception.MyQuotaException;

@Api(name = "collectendpoint", clientIds = {CollectEndpoint.WEB_CLIENT_ID, CollectEndpoint.ANDROID_CLIENT_ID}, 
     namespace = @ApiNamespace(ownerDomain = "morholt.fapema.br", ownerName = "morholt.fapema.br", packagePath = "collect"),
     audiences = {CollectEndpoint.ANDROID_AUDIENCE}
)
public class CollectEndpoint implements Serializable{
	
	  public static final String WEB_CLIENT_ID = "727728054183-g5347dt4o2k2klemra0f8mn5c7r4pnps.apps.googleusercontent.com";
	                                                  
	  // FIXME PRODUCTION this ANDROID_CLIENT_ID is generated from the SHA1 of a debug key, do it for real for production
	//  public static final String ANDROID_CLIENT_ID = "727728054183-u292na7rieh3d99avfdke5t7jt4u1pfp.apps.googleusercontent.com"; //FIXME local server
	  public static final String ANDROID_CLIENT_ID = "727728054183-0hmadh01jt56m6h2bnq0vm18iigmct79.apps.googleusercontent.com"; //FIXME server
	  public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;

	  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";


	private static final long serialVersionUID = 8126695928300666573L;
	private static final Logger log = Logger.getLogger(CollectEndpoint.class.getName());
	
	private void authenticate(User user) throws AuthenticationException {
		if (user == null) {
			throw new AuthenticationException("authentication failed!");
		}
		else {
			log.info("authenticated");
		}
	}
	
	
	private void authorize(Profile profile, String kind, Profile.CRUDEL crudel) throws AuthorizationException {
		boolean ok = profile.authenticate(kind, crudel);
		
		if(!ok) {
			log.info("authorize not ok -> profile: " + profile + " kind: " + kind + " CRUDEL: " + crudel);
			throw new AuthorizationException();
		}
	}
	
	@ApiMethod(name = "insertMyContentValues")
   public SimpleString insertMyContentValues(User user, @Named("table") String table, @Named("keys") String parentKey, Map<String, Object> map) throws  AuthorizationException, MyQuotaException, AuthenticationException, UserNotAllocatedToProjectException {

		String key = "";
		log.info("insertMy parentKey: " + key +" user:" + user + " project: " + map.get("project_name"));
		authenticate(user);
		//TODO data validation
		Profile profile = new MyServiceImpl().doLogin(user.getEmail());
		
		Key resultKey = null;
		try {
			authorize(profile, table,  Profile.CRUDEL.CREATE);
			if("collect".equals(table))
				new MyServiceImpl().checkUserIsAlocated((String)map.get("project_name"), new WebUser(user.getEmail(), profile));
			
			map.remove("contentValuesTableName");
			map.remove("keys");
			map.remove("table");
			map.put("user", user.getNickname());
			
			map.put(MyContentValues.LAST_UPDATE_ON_SYSTEM_COLUMN, new Date());
			resultKey = DAO.insert(table, parentKey, map);
			return  new SimpleString(KeyFactory.keyToString(resultKey));
			
	} catch (AuthorizationException e) {
			throw new AuthorizationException("Failed on user authorization: " + user.getEmail());
	} catch (UserNotAllocatedToProjectException e) {
		throw new UserNotAllocatedToProjectException("User " + user.getEmail() + " not allocated to project " +map.get("project_name") );
	}
	}
	
	@ApiMethod(name = "getURLBlobWeb")
	public @Named BlobURL getURLBlobWeb(User user, @Named("path") String path) throws Exception {
		authenticate(user); 
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		String blobUploadUrl = blobstoreService.createUploadUrl(path);
		return new BlobURL(blobUploadUrl);
	}
	
	@ApiMethod(name = "getBlobURL")
	public BlobURL getBlobURL(User user) throws Exception {
		authenticate(user); 
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		String blobUploadUrl = blobstoreService.createUploadUrl("/myUploadURL");
		return new BlobURL(blobUploadUrl);
	}
	
	@ApiMethod(name="getProjetInfo", path="getProjetInfo")
	public Model getProjectInfo(User user) throws AuthenticationException  {
		log.info("obtainProjectInfo");
	    authenticate(user); 
		String userEmail = user.getEmail();
		MyFilterPredicate filterPredicate = new MyFilterPredicate("email", MyFilterOperator.EQUAL, userEmail);
		List<Model> allocations = MyServiceImpl.doList(ALLOCATION.kind, ALLOCATION.key, null, null, null, filterPredicate, null, null, "CollectEndpoint.getProjectInfo");
		if(allocations == null || allocations.size()==0) {
			return null;
		}
		Model allocation = allocations.get(0);
		String projectId = allocation.get("projectId");

		filterPredicate = new MyFilterPredicate(PROJECT.key, MyFilterOperator.EQUAL, projectId);
		ModelList projects = MyServiceImpl.doList(PROJECT.kind, PROJECT.key, null, null, null, filterPredicate, null, null, "CollectEndpoint.getProjectInfo");
		if(projects == null || projects.isEmpty()) {
			log.info("no project found, filterPredicate:" + filterPredicate);
			return null;
		}
		Model project = projects.get(0);
		
		filterPredicate = new MyFilterPredicate(TEMPLATE_ANDROID.key, MyFilterOperator.EQUAL, project.get(TEMPLATE_ANDROID.kind));
		ModelList modelList = MyServiceImpl.doList(TEMPLATE_ANDROID.kind, TEMPLATE_ANDROID.key, null, null, null, filterPredicate, null, null, "CollectEndpoint.getProjectInfo");
		// TODO treat modelList empty
		
		Model templateAndroid = null;
		if(!modelList.isEmpty()) {
			templateAndroid = modelList.get(0);
			Map<String, Object> projectContentValues = project.getContentValues();
			projectContentValues.remove("Key");
			projectContentValues.remove("user");
			templateAndroid.getContentValues().putAll(project.getContentValues());
		}
		return templateAndroid;
	}
}