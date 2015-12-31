package br.fapema.morholt.android.model;

import br.fapema.morholt.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import br.fapema.morholt.android.MainActivity;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.initial.load.LoadActivity;
import br.fapema.morholt.android.settings.SettingsActivity;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;
// TODO refactor duplicated code of BaseActivity
public class BaseActivity extends FragmentActivity {

	public static final String MENU_ITEM = "MENU_ITEM";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return open(item.getItemId());
	}	
	
	
	
	private Values tmpValues;
	public void saveValuesAsTemp() {
		try {
			tmpValues = getMyApplication().getValues().clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void logComparationValuesWithTemp() {
		if(tmpValues == null) {
			throw new RuntimeException("forgot to call saveValuesAsTemp");
		}
		getMyApplication().getValues().logDiff(tmpValues);
	}
	
	/**
	 * 
	 * @param itemId: R.id.action_map, R.id.load, R.id.action_collect_pages 
	 * @return
	 */
	public boolean open(int itemId) {
		getMyApplication().setCurrentMainPage(itemId);
	    /*
		if (itemId == R.id.action_map) {
			openMap();
			return true;
		} else*/ 
		
		
		
		
		if (itemId == R.id.load) {
			openLoadActivity();
			return true;
		} else if (itemId == R.id.action_new) {
			getMyApplication().initializeBased();
			openWizardActivity();
			return true;
		} 
		else if (itemId == R.id.action_edit) {
		//	getMyApplication().initializeBased();
			openWizardActivity();
			return true;
		} else if (itemId == R.id.action_settings) {
			openSettings();
			return true;
		} else {
			System.out.println("default");
			return false;
		}
	}
	
	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public MyApplication getMyApplication() {
		return (MyApplication) getApplication();
	}
	
	private void openLoadActivity() {
		Intent intent = new Intent(this, LoadActivity.class);
		startActivity(intent);
	}
	
	private void openWizardActivity() {
		Intent intent = new Intent(this, WizardActivity.class);
		startActivity(intent);
	}
	
	public void openInitialActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void openMap() {
		//new Intent(getApplicationContext(), MapActivity.class);
		//replaceContainerFragmentWith(fragment);		
	}
	/*
	public Values getCollectFinding() {
		return ((MyApplication) getApplication()).getCurrentContentValues();
	}
	*/
}