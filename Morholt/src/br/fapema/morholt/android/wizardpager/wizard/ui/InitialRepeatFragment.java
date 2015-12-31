package br.fapema.morholt.android.wizardpager.wizard.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.helper.StringUtils;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import br.fapema.morholt.android.R;

public class InitialRepeatFragment extends BugFragment implements PageInterface, OnClickListener {

	private Page mPage;

	protected EditText mEditTextInput;

	public static InitialRepeatFragment create(String pageId) {
		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);

		InitialRepeatFragment textFragment = new InitialRepeatFragment();
		textFragment.setArguments(args);

		return textFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		String pageId = (String) args.getSerializable(PAGE_ID);
		mPage = getPage(pageId);
		setRetainInstance(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_repeat_initial,
				container, false);
		
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());
		((TextView) rootView.findViewById(R.id.comment)).setText(mPage
				.getComment());
		
		return rootView;
	}
	
	@Override
	public void disable() {
	}
	
	@Override
	public void update() {
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void showErrors() {
		
	}

	@Override
	public void clearErrors() {
		
	}

	@Override
	public void configureKeyboard() {
		
	}

	@Override
	public String getUniqueIdentifier() {
		return null;
	}

	@Override
	public void onClick(View v) {
		
		
	}

}