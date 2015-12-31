package br.fapema.morholt.android;

import java.io.Serializable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import br.fapema.morholt.android.R;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.fapema.morholt.android.model.BaseActivity;
import br.fapema.morholt.android.model.ProjectInfo;

public class MainActivity extends BaseActivity implements Serializable, OnClickListener  {
	
	private static final long serialVersionUID = -5565646862947009227L;

	private SharedPreferences settings;
	
	public static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";
	static final int REQUEST_ACCOUNT_PICKER = 2;
	private MyApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initial_buttons);

		//TODO obtain project data from db
		application = (MyApplication)getApplication();
		
		
   //     Log.d(this.getLocalClassName() + ".onCreate", "GoogleApiAvailability: " + GooglePlayServicesUtil.isGooglePlayServicesAvailable(this));
		
		Button buttonNewBased = (Button) findViewById(R.id.buttonInitialNewBasedOnLastOne);
		buttonNewBased.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		MyApplication application = (MyApplication)getApplication();
		application.initializeBased();
		open(R.id.action_new);
	}
	
	
	private boolean prepareUser() {
		settings = getSharedPreferences(
			    "SharedPreferences", 0);
		// this is Client ID of the web application not of the android
		application.setCredential(GoogleAccountCredential.usingAudience(this,
			    "server:client_id:727728054183-g5347dt4o2k2klemra0f8mn5c7r4pnps.apps.googleusercontent.com"));
		setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		if (application.getCredential().getSelectedAccountName() != null) {
		  return true;
		} else {
		  chooseAccount();
		  return false;
		}
	}
	
	private void setSelectedAccountName(String accountName) {
		  SharedPreferences.Editor editor = settings.edit();
		  editor.putString(PREF_ACCOUNT_NAME, accountName);
		  editor.commit();
		  application.getCredential().setSelectedAccountName(accountName);
		}

	void chooseAccount() {
	  startActivityForResult(application.getCredential().newChooseAccountIntent(),
	      REQUEST_ACCOUNT_PICKER);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	   Intent data) {
	 super.onActivityResult(requestCode, resultCode, data);
	 switch (requestCode) {
	   case REQUEST_ACCOUNT_PICKER:
	     if (data != null && data.getExtras() != null) {
	       String accountName =
	           data.getExtras().getString(
	               AccountManager.KEY_ACCOUNT_NAME);
	       if (accountName != null) {
	         setSelectedAccountName(accountName);
	       }
	     }
	     break;
	  }
		onPostPostCreate();
	}
	
	private void onPostPostCreate() {
		if(ProjectInfo.load(this) == false) {
			open(R.id.action_settings);
			finish();
		}
		else {
			application.initializeBased();
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		boolean userOk = prepareUser();
		if(userOk) {
			onPostPostCreate();
		}
	}
}