package br.fapema.morholt.android.wizardpager.wizard.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.wizardpager.ApplicationInterface;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

/**
 * workaround for bug: https://code.google.com/p/android/issues/detail?id=40537
 *
 */
public abstract class BugFragment extends Fragment implements PageInterface{


	public static final String PAGE_ID = "PAGE_ID";

    private final SparseIntArray mRequestCodes = new SparseIntArray();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
    
    
    public Page getPage(String pageId) {
    	return ((WizardActivity) getActivity()).getPage(pageId);
    }
    

    /**
    * Registers request code (used in
    * {@link #startActivityForResult(Intent, int)}).
    * 
    * @param requestCode
    *            the request code.
    * @param id
    *            the fragment ID (can be {@link Fragment#getId()} of
    *            {@link Fragment#hashCode()}).
    */
    public void registerRequestCode(int requestCode, int id) {
        mRequestCodes.put(requestCode, id);
    }// registerRequestCode()

	public void configureKeyboard(boolean hideKeyboard) {
		if (hideKeyboard) {
			hideKeyboard(getActivity());
		} else {
			configureKeyboard();
		}
	}
    
    public abstract void configureKeyboard();
    
    protected void showKeyboard(Activity activity, EditText editText) {
	//	if(!activity.keyboardVisible) {
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if(!editText.equals(activity.getCurrentFocus())) {
				editText.requestFocus();
			}
		    imm.showSoftInput(editText, 0);
	//	    activity.keyboardVisible = true;
	//	}
	}
    
    protected void hideKeyboard(Activity activity) {
		 final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		 imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//		 activity.keyboardVisible = false;
	}
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (getParentFragment() instanceof BugFragment) {
            ((BugFragment) getParentFragment()).registerRequestCode(
                    requestCode, hashCode());
            getParentFragment().startActivityForResult(intent, requestCode);
        } else
            super.startActivityForResult(intent, requestCode);
    }// startActivityForResult()

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!checkNestedFragmentsForResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }// onActivityResult()

    /**
    * Checks to see whether there is any children fragments which has been
    * registered with {@code requestCode} before. If so, let it handle the
    * {@code requestCode}.
    * 
    * @param requestCode
    *            the code from {@link #onActivityResult(int, int, Intent)}.
    * @param resultCode
    *            the code from {@link #onActivityResult(int, int, Intent)}.
    * @param data
    *            the data from {@link #onActivityResult(int, int, Intent)}.
    * @return {@code true} if the results have been handed over to some child
    *         fragment. {@code false} otherwise.
    */
    protected boolean checkNestedFragmentsForResult(int requestCode,
            int resultCode, Intent data) {
        final int id = mRequestCodes.get(requestCode);
        if (id == 0)
            return false;

        mRequestCodes.delete(requestCode);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments == null)
            return false;

        for (Fragment fragment : fragments) {
            if (fragment.hashCode() == id) {
                fragment.onActivityResult(requestCode, resultCode, data);
                return true;
            }
        }

        return false;
    }// checkNestedFragmentsForResult()

    /*
    public CollectFinding getCollectFinding() {
    	MyApplication myApplication = (MyApplication) getActivity().getApplication();
    	return myApplication.getCurrentCollectFinding();
    }
    */
    public abstract String getUniqueIdentifier();
    
    public ApplicationInterface getApplication() {
    	return (ApplicationInterface) getActivity().getApplication();
    }

    public void markAsNotFirstOnResumeAnyomore() {
    	getApplication().addToViewsAlreadyValidating(getUniqueIdentifier());
    }

    public boolean isFirstOnResume() {
    	return getActivity()==null || !(getApplication().isLoaded() || getApplication().isViewAlreadyValidating(getUniqueIdentifier()));
    }

    @Override
    public boolean checkIfCanChangeValues() {
    	return MyApplication.valuesAlreadySentToServer() == false;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	if(checkIfCanChangeValues() == false) {
    		disable();
    	}
    }
}
