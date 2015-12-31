package br.fapema.morholt.android.wizardpager.wizard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import br.fapema.morholt.android.R;


public class BarCodeFragment extends BugFragment implements PageInterface {
private View rootView;
	
	public static final int BAR_CODE = 444;

	protected static final String ARG_KEY = "key_barcode";
	
	private Page mPage;

	public static BarCodeFragment create(String pageId) {
		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);

		BarCodeFragment fragment = new BarCodeFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		String pageId = (String) args.getSerializable(PAGE_ID);
		mPage = getPage(pageId);
		//setRetainInstance(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.barcode_fragment, container,
				false);
		HandleClick hc = new HandleClick();
        rootView.findViewById(R.id.butQR).setOnClickListener(hc);
        update();
		return rootView; 
	
	}
	
	@Override
	public void disable() {
		rootView.findViewById(R.id.butQR).setEnabled(false);
	}
	
	@Override
	public void update() {
        TextView tvResult=(TextView)rootView.findViewById(R.id.tvResult);
        tvResult.setText(mPage.getValue());
        tvResult.setSaveEnabled(false);
	}
	
    private class HandleClick implements OnClickListener{
    	public void onClick(View arg0) {
	    Intent intent = new Intent("com.google.zxing.client.android.SCAN");     
	    startActivityForResult(intent, BAR_CODE);	//Barcode Scanner to scan for us
    	}
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == BAR_CODE) {
            TextView tvStatus=(TextView)rootView.findViewById(R.id.tvStatus);
            TextView tvResult=(TextView)rootView.findViewById(R.id.tvResult);
            if (resultCode == getActivity().RESULT_OK) {
            	tvStatus.setText(intent.getStringExtra("SCAN_RESULT_FORMAT"));
            	tvResult.setText(intent.getStringExtra("SCAN_RESULT"));
            	mPage.saveValue(intent.getStringExtra("SCAN_RESULT"));
				BusProvider.getInstance().post(new PageChangeEvent(mPage));
            } else if (resultCode == getActivity().RESULT_CANCELED) {
            	tvStatus.setText("Press a button to start a scan.");
                tvResult.setText("Scan cancelled.");
            	mPage.saveValue(null);
				BusProvider.getInstance().post(new PageChangeEvent(mPage));
            }
        }
    }
	@Override
	public boolean validate() {
		if (!mPage.isRequired() || mPage.isCompleted()) {
			clearErrors();
			return true;
		} else {
			showErrors();
			return false;
		}
	}

	public void showErrors() {
		Toast t = Toast.makeText(getActivity(), "Obtenha o bar code para avançar.",
				Toast.LENGTH_SHORT);
		t.show();
	}
	
	@Override
	public void clearErrors() {
		// do nothing
	}
	
	@Override
	public void configureKeyboard() {
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	@Override
	public String getUniqueIdentifier() {
		return ARG_KEY;
	}
}	