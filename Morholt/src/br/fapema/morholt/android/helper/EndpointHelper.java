package br.fapema.morholt.android.helper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import br.fapema.morholt.collect.collectendpoint.Collectendpoint;
import br.fapema.morholt.android.exception.NotOnlineException;
import br.fapema.morholt.android.exception.ServerUnavailableException;

public class EndpointHelper {
	private static final int SERVER_TIMEOUT = 17000;

	public static Collectendpoint obtainEndpoint(Context context) throws NotOnlineException, ServerUnavailableException {
		
		if(!isOnline(context)) {
			throw new NotOnlineException();
		}
	//	if(!isConnectedToServer(Collectendpoint.DEFAULT_ROOT_URL, SERVER_TIMEOUT)) {
	//		throw new ServerUnavailableException(); //FIXME
	//	}
		
		  final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context,
				    "server:client_id:802459644262-gpgb0o87k161qvtclsudcvf4mfb7h99s.apps.googleusercontent.com"); //FIXME credential
		  
		
		SharedPreferences settings = context.getSharedPreferences(
			    "SharedPreferences", 0);
		
		// this is Client ID of the web application not of the android
		credential.setSelectedAccountName(settings.getString("PREF_ACCOUNT_NAME", null)); 
		
		HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
				credential.initialize(request);
			}
		};
		
		 Collectendpoint.Builder endpointBuilder = new Collectendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new GsonFactory(), httpRequestInitializer);
		// endpointBuilder.setApplicationName("arqueologia_appengine"); // from Arqueologia_AppEngine.gwt.xml
		 Collectendpoint endpoint = endpointBuilder.setRootUrl(Collectendpoint.DEFAULT_ROOT_URL).build();
		 
		Log.i(context.getClass().getSimpleName(), "Endpoint: " + endpoint.getRootUrl());
		return endpoint;
	}
	
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public static boolean isConnectedToServer(String url, int timeout) throws ServerUnavailableException {
		url="http://192.168.229.2:8888/";
		try {
			URL myUrl = new URL(url.replace("/_ah/api/", ""));
			URLConnection connection = myUrl.openConnection();
			connection.setConnectTimeout(timeout);
			connection.connect();
			return true;
		} catch (Exception e) {
			Log.i(EndpointHelper.class.getName(), "isConnectedToServer exception: " + e.getMessage());
			throw new ServerUnavailableException(e);
		}
	}
}
