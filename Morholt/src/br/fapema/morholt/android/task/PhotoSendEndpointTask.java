package br.fapema.morholt.android.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
*/

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import br.fapema.morholt.android.CloudEndpointUtils;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.exception.NotOnlineException;
import br.fapema.morholt.android.exception.ServerUnavailableException;
import br.fapema.morholt.android.helper.EndpointHelper;
import br.fapema.morholt.collect.collectendpoint.Collectendpoint;
import br.fapema.morholt.collect.collectendpoint.model.BlobURL;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;
import br.fapema.morholt.android.wizardpager.wizard.model.CameraPage;

public class PhotoSendEndpointTask extends AsyncTask<Context, Integer, Long> {

	private static final String SEPARA = "SEPARA";
	private EndpointCallback endpointCallback;
	private Integer listPosition;
	private Values values;
	private Context context;
	
	/**
	 * @param photoEndpointCallback
	 * @param listPosition
	 * @param values
	 */
	public PhotoSendEndpointTask(Context context, EndpointCallback photoEndpointCallback, Integer listPosition, Values values) {
		this.endpointCallback = photoEndpointCallback;
		this.listPosition = listPosition;
		this.values = values;
		this.context = context;
	}

	protected Long doInBackground(Context... contexts) {
		Log.i("PhotoSendEndpointTask", "doInBackground start"); 
		GoogleAccountCredential credential = MyApplication.getCredential();
		
		
		
		Collectendpoint.Builder endpointBuilder2 = new Collectendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new com.google.api.client.json.jackson2.JacksonFactory(), credential);
		Collectendpoint endpoint2 = CloudEndpointUtils.updateBuilder(
				endpointBuilder2).build();
		Log.i(this.getClass().getSimpleName(), "Endpoint2: " + endpoint2.getRootUrl());
		
		
		
		
		Collectendpoint.Builder endpointBuilder = new Collectendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new com.google.api.client.json.jackson2.JacksonFactory(), credential);
		try {
			Collectendpoint endpoint = EndpointHelper.obtainEndpoint(context);
			Log.i(this.getClass().getSimpleName(), "Endpoint: " + endpoint.getRootUrl());
		    BlobURL blobURL = endpoint.getBlobURL().execute();
			String url = blobURL.getUrl();
			if(url.contains("brandi-i7:8888"))  // FIXME local hostname 
				url = url.replace("brandi-i7:8888",  Collectendpoint.LOCAL_DEFAULT_IP); 
			
			Log.i(this.getClass().getSimpleName(), "Photo Url: " + url);
			
				Map<String, File> files = new HashMap<String, File>();
				List<String> fileNames = getCameraColumnNames();
				for (String fileName : fileNames) {
					final File file = new File(getPhotoUrl(fileName));  
					files.put(fileName, file);
				}
				// TODO this is not working with local server
				postFile(url, files);
		} 
		catch(SocketTimeoutException e) {
			e.printStackTrace();
			Log.e("ReviewFragment", "GetURLEndpointsTask, timeout: " + e.getMessage());
			return (long) SendEndpointTask.RESULT_ERROR_TIMEOUT;
		}
		catch (IOException e) {
			
			e.printStackTrace();
			Log.e("ReviewFragment", "GetURLEndpointsTask, error2 on endpoint: " + e.getMessage());
			return (long) 1;
		} catch (NotOnlineException e1) {
			Log.e("ReviewFragment", "GetURLEndpointsTask, notOnlineException on endpoint: " + e1.getMessage());
			return (long) SendEndpointTask.RESULT_ERROR_NOT_ONLINE;
		} catch (ServerUnavailableException e1) {
			Log.e("ReviewFragment", "GetURLEndpointsTask, ServerUnavailableException on endpoint: " + e1.getMessage());
			return (long) SendEndpointTask.RESULT_ERROR_SERVER_UNAVAILABLE;
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.e("ReviewFragment", "GetURLEndpointsTask, error on endpoint: " + e.getMessage());
			return (long) 1;
		} 
		Log.i("PhotoSendEndpointTask", "doInBackground end"); 
		return (long) SendEndpointTask.RESULT_OK;
	}
	
	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		endpointCallback.endpointCallback(new TaskResult(listPosition, result));
	}


	private String getPhotoUrl(String column) {
		return values.getAsString(column); 
	}
	
	private List<String> getCameraColumnNames() {
		return MyApplication.pagelist.obtainColumnNameFromPages(CameraPage.class);
	}
	   
	public void postFile(String url, Map<String, File> files) throws Exception {
		String key = values.getAsString("key"); 
		MultipartUtility multipartUtility = new MultipartUtility(url, "UTF-8");
		multipartUtility.addFormField("sometestkey", "sometestvalue");
		  for (String fileName : files.keySet()) {
		//   	builder.addBinaryBody(key+SEPARA + fileName, files.get(fileName), ContentType.create("image/jpeg"), key+SEPARA + fileName);    
		  
		multipartUtility.addFilePart(key+SEPARA + fileName, files.get(fileName));
		  }
		multipartUtility.finish();
	} 
}