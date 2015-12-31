package br.fapema.morholt.android;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.android.model.PreData;
import br.fapema.morholt.android.model.User;
import br.fapema.morholt.android.parser.Database;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.parser.XSLReader;
import br.fapema.morholt.android.wizardpager.ApplicationInterface;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;
import br.fapema.morholt.android.wizardpager.wizard.model.PageList;

import com.activeandroid.app.Application;
import com.activeandroid.query.Select;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import dagger.ObjectGraph;

public class MyApplication extends Application implements ApplicationInterface{
	private PreData preData;
	private static Values values;
	private Set<String> setOfViewsAlreadyValidating;
	private int currentMainPage;
	private int currentCollectPage = 0;
	private boolean isLoaded;
	private User user;
	public static Database database;
	private static Creator creator;
	private static ObjectGraph applicationGraph;
	public static PageList pagelist;
	private static Context context;
	private static ModelPath rootModelPath;
	private static GoogleAccountCredential credential;
	
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		setOfViewsAlreadyValidating = new HashSet<String>();
		
		user = new Select().from(User.class).executeSingle();
		if(user == null) {
			user = new User(); 
		}
		
		System.out.println("end");
	}
	
	public static void reinitialize() {
		applicationGraph = null;
	}
	
	public void initializeBased() {
		setLoaded(false);
		setListOfViewsAlreadyValidating(new HashSet<String>());
		PreData preData = new PreData();
		setPreData(preData);
		preData.save(); // TODO load from server
		setValues(getValuesWithSomeLastChoices(preData));
	}
	
	protected List<Object> getModules() {
		CollectModule collectModule = new CollectModule(this);
		creator = collectModule.getCreator(); // TODO maybe useless
		pagelist = creator.pageList;
		return Arrays.<Object> asList(collectModule);
	}

	public ObjectGraph getApplicationGraph() {
		Log.d("MyApplication", "getApplicationGraph");
		return applicationGraph;
	}
		 
	public boolean isJustCreated() {
		return preData == null;
	}

	public void setValues(Values values) {
		MyApplication.values = values;
		if(applicationGraph == null) { // this if is important to not be recreating pages
			applicationGraph = ObjectGraph.create(getModules().toArray());
		}
	}

	public void addToViewsAlreadyValidating(
			String string) {
		setOfViewsAlreadyValidating.add(string);
	}
	
	public boolean isViewAlreadyValidating(String view) {
		return setOfViewsAlreadyValidating.contains(view);
	}

	public void initialize() {
		setLoaded(false);
		setListOfViewsAlreadyValidating(new HashSet<String>());
		PreData preData = new PreData();
		setPreData(preData);
		preData.save(); // TODO load from server
	//	setMyContentValuesWizard(new Values(preData));
		setCurrentCollectPage(0);
		setCurrentMainPage(0);
	}


	
	private Values getValuesWithSomeLastChoices(PreData preData) {
		Values values = getValues();
		Values newValues = new Values();
		if(values != null) {
		//	copySomeValuesToNewCollect(currentMyContentValuesWizard, newMyContentValuesWizard);
		}
		return newValues;
	}
	
// TODO this should be a map kind to mycontentvalues, so we could have a sequence of pages where things ended up saved on different tables (not just on repeat or as a reference from the main/first table)
	public static Values getCurrentContentValues(ModelPath modelPath) {
		if(modelPath == null) return null;
		return modelPath.obtainContentValues(values);
	}

    public static boolean valuesAlreadySentToServer() {
    	return "1".equals(values.getAsString(XSLReader.SENT_TO_SERVER));
    }
    

	public static boolean photosAlreadySentToServer() {
    	return "1".equals(values.getAsString(XSLReader.PHOTO_SENT_TO_SERVER));
	}
    
	public Creator getCreator() {
		return creator;
	}

	@Override
	public void setDatabase(Database database) {
		this.database = database;
	}
	
	public String getMainTableName() {
		return database.getMainTableName();
	}
	
    public static Context getAppContext() {
        return MyApplication.context;
    }
    
    public static ModelPath getRootPath() {
    	return rootModelPath;
    }

	@Override
	public void setRootModelPath(ModelPath rootModelPath) {
		this.rootModelPath = rootModelPath;
	}

	public static GoogleAccountCredential getCredential() {
		return credential;
	}

	public void setCredential(GoogleAccountCredential credential) {
		this.credential = credential;
	}

	public int getCurrentMainPage() {
		return currentMainPage;
	}
	/**
	 * 
	 * @param currentMainPage: R.id.action_map, R.id.load or R.id.action_collect_pages
	 */
	public void setCurrentMainPage(int currentMainPage) {
		this.currentMainPage = currentMainPage;
	}

	public int getCurrentCollectPage() {
		return currentCollectPage;
	}

	public void setCurrentCollectPage(int currentCollectPage) {
		this.currentCollectPage = currentCollectPage;
	}

	public User getUser() {
		return user;
	}
	
	public static Values getRootContentValues() {
		return values;
	}
	
	public static Values denormalize() {
		// TODO pagelist.
		return null;
	}
	
	public PreData getCollect() {
		return preData;
	}

	public void setPreData(PreData preData) {
		this.preData = preData;
	}
	

	public Values getValues() {
		return values;
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public void setListOfViewsAlreadyValidating(
			Set<String> setOfViewsAlreadyValidating) {
		this.setOfViewsAlreadyValidating = setOfViewsAlreadyValidating;
	}
	
	public Set<String> getListOfViewsAlreadyValidating() {
		return setOfViewsAlreadyValidating;
	}

}