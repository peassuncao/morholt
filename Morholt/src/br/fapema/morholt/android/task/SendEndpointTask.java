package br.fapema.morholt.android.task;

import java.io.IOException;
import java.net.ConnectException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.collect.collectendpoint.Collectendpoint;
import br.fapema.morholt.collect.collectendpoint.model.JsonMap;
import br.fapema.morholt.collect.collectendpoint.model.SimpleString;
import br.fapema.morholt.android.db.DAO;
import br.fapema.morholt.android.exception.NotOnlineException;
import br.fapema.morholt.android.exception.ServerUnavailableException;
import br.fapema.morholt.android.helper.EndpointHelper;
import br.fapema.morholt.android.helper.JsonUtil;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;
import br.fapema.morholt.android.wizardpager.wizard.helper.StringUtils;
import br.fapema.morholt.android.wizardpager.wizard.model.CameraPage;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;

public class SendEndpointTask extends AsyncTask<Context, Integer, TaskResult> {
	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR_SERVER_UNAVAILABLE = 4;
	public static final int RESULT_ERROR_AUTHORIZATION = 5;
	public static final int RESULT_ERROR_NOT_ALLOCATED = 6;
	public static final int RESULT_ERROR_NOT_ONLINE = 3;
	public static final int RESULT_ERROR_IO = 1;
	public static final int RESULT_ERROR_AUTHENTICATION = 2;
	private Activity activity;
	private EndpointCallback endpointCallback;
	private MyApplication myApplication;
	/**
	 * optional
	 */
	private Integer listPosition;
	private Values values;
	
	public SendEndpointTask(Activity activity, EndpointCallback endpointCallback, MyApplication myApplication) {
		this.activity = activity;
		this.endpointCallback = endpointCallback;
		this.myApplication = myApplication;
		values = MyApplication.getRootContentValues(); 
	}
	
	public SendEndpointTask(Activity activity, EndpointCallback endpointCallback, MyApplication myApplication, Values values, int position) {
		this.activity = activity;
		this.endpointCallback = endpointCallback;
		this.myApplication = myApplication;
		listPosition = position;
		this.values = values;
	}

	protected TaskResult doInBackground(Context... contexts) {
		try {
			Collectendpoint endpoint = EndpointHelper.obtainEndpoint(activity);
		    JsonMap denormalizedValues = obtainJsonMap();
		    
		    addImageNotBlankProperty(denormalizedValues);
		    
			SimpleString key = endpoint.insertMyContentValues("collect", Creator.TABLE_KEY, denormalizedValues).execute();	
			
			if(key != null) {
	            values.put("key", key.getString());
				DAO.save(myApplication.getMainTableName(), values);
			}
		} 
		catch (GoogleAuthIOException ge) {
			ge.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "authentication error on endpoint: " + ge.getMessage());
			return new TaskResult(listPosition, (long) RESULT_ERROR_AUTHENTICATION);
		}
		catch(ConnectException e) {
			e.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "server unavailable exception: " + e.getMessage());
			return new TaskResult(listPosition, (long) RESULT_ERROR_SERVER_UNAVAILABLE);
		}
		catch (IOException e) { 
			e.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "error on endpoint: " + e.getMessage());
			
			if(e.getMessage()!=null && e.getMessage().contains("Failed on user authorization"))
					return new TaskResult(listPosition, (long) RESULT_ERROR_AUTHORIZATION);

			else if(e.getMessage()!=null && e.getMessage().contains("not allocated to project"))
					return new TaskResult(listPosition, (long) RESULT_ERROR_NOT_ALLOCATED);
			else
				return new TaskResult(listPosition, (long) RESULT_ERROR_IO);
		}
		catch (NotOnlineException e) {
			e.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "not online exception: " + e.getMessage());
			return new TaskResult(listPosition, (long) RESULT_ERROR_NOT_ONLINE);
			
		} catch (ServerUnavailableException e) {
			e.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "server unavailable exception: " + e.getMessage());
			return new TaskResult(listPosition, (long) RESULT_ERROR_SERVER_UNAVAILABLE);
		}
		return new TaskResult(listPosition, (long) RESULT_OK);
	}

	private void addImageNotBlankProperty(JsonMap denormalizedValues) {
		for (Page page : MyApplication.pagelist) {
			if(page instanceof CameraPage) {
				denormalizedValues.put(page.getColumnOnDB()+"Boolean", StringUtils.isNotBlank(page.getValue()));
			}
		}
	}

	private JsonMap obtainJsonMap() throws IOException {
		JsonMap jsonMap = values.toJsonMap();
		Log.d("sentMsg", jsonMap.toPrettyString());
	
		JsonMap denormalizedValues = JsonUtil.denormalizeX(jsonMap, MyApplication.database, activity);
		denormalizedValues.put("project_name", ProjectInfo.getProjectName());
		denormalizedValues.put("cidade", ProjectInfo.getCity());
		denormalizedValues.put("state", ProjectInfo.getState());
		denormalizedValues.put("locality", ProjectInfo.getLocality());
		denormalizedValues.put("sitio", ProjectInfo.getSitio());
		denormalizedValues.put("nameOfThePlace", ProjectInfo.getNameOfThePlace());
		denormalizedValues.remove(Creator.LAST_UPDATE_ON_SYSTEM);
		return denormalizedValues;
	}
	
	



	@Override
	protected void onPostExecute(TaskResult result) {
		super.onPostExecute(result);
		Log.i(this.getClass().getSimpleName(), " result: " + String.valueOf(result.result == 0));
		endpointCallback.endpointCallback(result);
	}
}
