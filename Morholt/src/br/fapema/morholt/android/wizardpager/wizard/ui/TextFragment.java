package br.fapema.morholt.android.wizardpager.wizard.ui;

import br.fapema.morholt.android.R;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.helper.StringUtils;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class TextFragment extends BugFragment implements PageInterface {

	public static final String TEXT_KEY = "mytext";

	public static final String INPUT_TYPE = "INPUT_TYPE";
	
	private Page mPage;

	protected EditText mEditTextInput;
	
	private Integer inputType;

	public static TextFragment create(String pageId, Integer inputType) {  
		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);
		args.putSerializable(INPUT_TYPE, inputType);

		TextFragment textFragment = new TextFragment();
		textFragment.setArguments(args);

		return textFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		String pageId = (String) args.getSerializable(PAGE_ID);
		inputType = (Integer) args.getSerializable(INPUT_TYPE);
		mPage = getPage(pageId);
		setRetainInstance(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_text,
				container, false);
		
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());
		((TextView) rootView.findViewById(R.id.comment)).setText(mPage
				.getComment());

		mEditTextInput = (EditText) rootView.findViewById(R.id.editTextInput);
		mEditTextInput.setSaveEnabled(false);
		mEditTextInput.setInputType(inputType);
		
		// if(StringUtils.isNotBlank(mPage.getData().getString(mKey))) {
		update();
		// }
		return rootView;
	}

	@Override
	public void onViewCreated(View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setInputType();
		mEditTextInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				Log.d(getClass().toString() + ".afterTextChanged:  ",
						editable.toString());

				String editableString = (editable != null) ? editable.toString() : null;
				
				boolean noChange = StringUtils.equalsOrBothBlank(editableString, mPage.getValue());
				if(noChange == false) {
					mPage.saveValue(editableString);
					BusProvider.getInstance().post(new PageChangeEvent(mPage));
				}
				if (!isFirstOnResume()) {
					validate();
				}
			}
		});
	}
	
	 @Override
	    public void onSaveInstanceState(final Bundle outState) {
	        super.onSaveInstanceState(outState);
	        if(outState != null) {
	        	outState.putString("value", mPage.getValue());
	        }
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);

	        if (savedInstanceState != null) {
	            //probably orientation change
	            // mEditTextInput.setText(savedInstanceState.getString("value"));
	        	mEditTextInput.setText(mPage.getValue());       	
	        } 
	    }

	protected void setInputType() {
		mEditTextInput.setInputType(inputType);
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

	@Override
	public void showErrors() {
		mEditTextInput.setError("campo obrigatório");
		configureKeyboard();
	}

	@Override
	public void clearErrors() {
		if(mEditTextInput != null)
			mEditTextInput.setError(null);
	}

	@Override
	public void markAsNotFirstOnResumeAnyomore() {
		super.markAsNotFirstOnResumeAnyomore();

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		update();
	}
	
	@Override
	public void configureKeyboard() {
		showKeyboard(getActivity(), mEditTextInput);

	}

	@Override
	public String toString() {
		return super.toString() + " text: "
				+ mEditTextInput.getText().toString();
	}

	@Override
	public String getUniqueIdentifier() {
		return mPage.getColumnOnDB();
	}

	@Override
	public void update() {
		mEditTextInput.setText(mPage.getValue()==null?"":mPage.getValue());
		
	}

	@Override
	public void disable() {
		mEditTextInput.setEnabled(false);
	}
}