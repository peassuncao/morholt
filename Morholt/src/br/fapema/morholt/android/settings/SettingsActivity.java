/**
 * 
 */
package br.fapema.morholt.android.settings;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.fapema.morholt.android.MainActivity;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.exception.IllegalTemplateException;
import br.fapema.morholt.android.exception.NotOnlineException;
import br.fapema.morholt.android.exception.ServerUnavailableException;
import br.fapema.morholt.android.helper.EndpointHelper;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.task.TaskResult;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import br.fapema.morholt.android.R;
import br.fapema.morholt.collect.collectendpoint.Collectendpoint;
import br.fapema.morholt.collect.collectendpoint.model.Model;

/**
 * @author pedro
 *
 */
public class SettingsActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		TextView labelUser = (TextView) findViewById(R.id.labelUser);
		labelUser.setText("usuário: " + getUser());
		
		Button buttonSettings = (Button) findViewById(R.id.buttonProjects);
		buttonSettings.setOnClickListener(this);
		
		Button accountChoosing = (Button) findViewById(R.id.buttonChooseAccount);
		accountChoosing.setOnClickListener(new AccountChooseOnClick());
		
		TextView labelVersion = (TextView) findViewById(R.id.labelVersion);
		labelVersion.setText("versão: " + getVersion());
		
	}

	private String getVersion() {
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.d(SettingsActivity.class.getName(), "error getting version: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return pInfo.versionName;
		
	}

	private String getUser() {
		String user = MyApplication.getCredential().getSelectedAccountName();
		return user == null ? "nenhum usuário selecionado"  : user;
	}
	
	@Override
	public void onClick(View v) {
		new EndpointsTask().execute(this);
	}
	
	
	private class AccountChooseOnClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			//((MyApplication)getApplication()).getCredential().setSelectedAccountName(null);
			removeUserFromSharedPreferences();
			
			Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
			startActivity(intent);
		}

		private void removeUserFromSharedPreferences() {
			SharedPreferences settings = getSharedPreferences(
				    "SharedPreferences", 0);
			  SharedPreferences.Editor editor = settings.edit();
			  editor.remove(MainActivity.PREF_ACCOUNT_NAME);
			  editor.commit();
		}
		
	}

	
	public class EndpointsTask extends AsyncTask<Context, Integer, String> {
		protected String doInBackground(Context... contexts) {
			try {

				Collectendpoint endpoint = EndpointHelper.obtainEndpoint(SettingsActivity.this);
				Log.i(this.getClass().getSimpleName(), "Endpoint: " + endpoint.getRootUrl());
				
				Model projectModel = endpoint.getProjetInfo().execute();
				if(projectModel == null) return "Não há ainda projeto alocado para você.";
				
				String projectName = (String)projectModel.getContentValues().get("name");
				String city = (String) projectModel.getContentValues().get("nome"); //TODO
				String state = (String) projectModel.getContentValues().get("state"); 
				String locality = (String) projectModel.getContentValues().get("locality"); 
				String sitio = (String) projectModel.getContentValues().get("sitio"); 
				String nameOfThePlace = (String) projectModel.getContentValues().get("nameOfThePlace"); 
				
				Log.i(SettingsActivity.class.getName(), "project: " + projectName + " templateModel: " + projectModel);
				String template = (String) projectModel.getContentValues().get("template");
				String values = (String) projectModel.getContentValues().get("values");
				
				
				String templateFileName = projectName + ".csv";
				boolean ok = writeFile(templateFileName, template.getBytes());
				

				String templateValuesFileName = null;
				boolean okValues = false; //FIXME
				if(values != null && !values.isEmpty()) {
					templateValuesFileName = projectName + "_values" + ".csv"; 
					okValues = writeFile(templateValuesFileName, values.getBytes());
				}
				
				ProjectInfo.setValues(templateFileName, projectName, projectName, templateValuesFileName, city, state, locality, sitio, nameOfThePlace); 
				ProjectInfo.save(SettingsActivity.this);
				
			} 
			catch (IllegalArgumentException ie) {
				ie.printStackTrace();
				Log.e("SettingsActivity", "illegal argument exception on endpoint: " + ie.getMessage());
				
				if(ie.getMessage().contains("the name must not be empty"))
					return "Escolha um usuário antes.";
				
				return "erro no envio (3)";

			}
			/**
			 * if package did change, change it also on https://console.developers.google.com/project/projetoarqueologia/apiui/credential#
			 */
			catch (GoogleAuthIOException ge) {
				ge.printStackTrace();
				Log.e("SettingsActivity", "authentication error on endpoint: " + ge.getMessage());
				return "falha na autenticação do usuário";
			}
			catch (IOException e) { 
				e.printStackTrace();
				Log.e("SettingsActivity", "error on endpoint: " + e.getMessage());
				if(e.getMessage()!=null && e.getMessage().contains("Failed on user authorization"))
					return "falha de autorização do usuário";

			else if(e.getMessage()!=null && e.getMessage().contains("not allocated to project"))
					return "este usuário não está alocado neste projeto";
			else
				return "erro no envio (2)";
			} catch (IllegalTemplateException e) {
				e.printStackTrace();
				Log.e("SettingsActivity", "illegal template exception on endpoint: " + e.getMessage());
				return "erro no envio (1)";
			} catch (NotOnlineException e) {
				e.printStackTrace();
				Log.e("SettingsActivity", "not online exception on endpoint: " + e.getMessage());
				return "erro: sem conexão com internet";
			} catch (ServerUnavailableException e) {
				e.printStackTrace();
				Log.e("SettingsActivity", "server unavailable exception on endpoint: " + e.getMessage());
				return "erro: servidor indisponível";
			}
			return null;
		}

		private boolean writeFile(String fileName, byte[] stringBytes) throws IllegalTemplateException {
			FileOutputStream outputStream;

			try {
			  outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
			  outputStream.write(stringBytes);
			  outputStream.close();
			} catch (Exception e) {
			  e.printStackTrace();
			  throw new IllegalTemplateException(e);
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result == null) {
				MyApplication.reinitialize();
				Toast.makeText(SettingsActivity.this, "sucesso", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
			else 
				Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_SHORT).show();
		}
	}
}