package br.fapema.morholt.android.initial.load;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.settings.SettingsActivity;
import br.fapema.morholt.android.R;

public class BaseListActivity extends ListActivity {


	public static final String MENU_ITEM = "MENU_ITEM";
	//private CollectPagesFragment collectPagesFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return onOptionsItemSelected(item.getItemId());
	}	
	
	public Boolean onOptionsItemSelected(int itemId) {
		//Log.d("onOptionItemSelected", String.valueOf(itemId));
		/*
		if(!(this instanceof MainActivity)) {
			Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            intent.putExtra(MENU_ITEM, itemId);
            startActivity(intent);
            return true;
		}
		else {*/
			return open(itemId);
		//}
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
		} else 
		*/
		
		if (itemId == R.id.action_new) {
			getMyApplication().initializeBased();
			openWizardActivity();
			return true;
		} 
		else if (itemId == R.id.action_edit) {
		//	getMyApplication().initializeBased();
			openWizardActivity();
			return true;
		} 
		else if (itemId == R.id.action_settings) {
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
	
	
	
	private void openWizardActivity() {
		Intent intent = new Intent(this, WizardActivity.class);
		startActivity(intent);
		
		
		/*
		if(!(this instanceof MainActivity)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		
		if(((MyApplication) getApplication()).isJustCreated()) {
			((MyApplication) getApplication()).initialize();
		}
        collectPagesFragment = new CollectPagesFragment();
		ContainerUtil.replaceContainerFragmentWith(collectPagesFragment, R.id.contentPanel, getSupportFragmentManager());
		*/
	}
	
	private void openMap() {
		//new Intent(getApplicationContext(), MapActivity.class);
		//replaceContainerFragmentWith(fragment);		
	}
	/*
	public Values getCollectFinding() {
		return ((MyApplication) getApplication()).getCurrentContentValues();
	}*/
}