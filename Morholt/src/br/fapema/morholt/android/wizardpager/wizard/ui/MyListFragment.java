package br.fapema.morholt.android.wizardpager.wizard.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.wizardpager.ApplicationInterface;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public abstract class MyListFragment extends ListFragment implements PageInterface{


	public static final String PAGE_ID = "PAGE_ID";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public Page getPage(String pageId) {
	    	return ((WizardActivity) getActivity()).getPage(pageId);
	}

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
	
    protected void hideKeyboard(Activity activity) {
		 final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		 imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		 //activity.keyboardVisible = false;
	}
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	if(checkIfCanChangeValues() == false) {
    		disable();
    	}
    }
    
    @Override
    public boolean checkIfCanChangeValues() {
    	return MyApplication.valuesAlreadySentToServer() == false;
    }
    
    public MyApplication getMyApplication() {
    	return (MyApplication) getActivity().getApplication();
    }

	@Override
	public boolean validate() {
		return false;
	}

	@Override
	public void showErrors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearErrors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markAsNotFirstOnResumeAnyomore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureKeyboard(boolean hideKeyboard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUniqueIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
