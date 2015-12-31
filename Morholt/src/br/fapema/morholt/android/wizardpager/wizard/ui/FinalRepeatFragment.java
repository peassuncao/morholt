package br.fapema.morholt.android.wizardpager.wizard.ui;

import android.content.Intent;
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
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.helper.StringUtils;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.model.repeat.FinalRepeatPage;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import br.fapema.morholt.android.R;

public class FinalRepeatFragment extends BugFragment implements PageInterface, OnClickListener {


	private FinalRepeatPage mPage;
	private Button button;

	protected EditText mEditTextInput;

	public static FinalRepeatFragment create(String pageId) {
		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);

		FinalRepeatFragment textFragment = new FinalRepeatFragment();
		textFragment.setArguments(args);

		return textFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		String pageId = (String) args.getSerializable(PAGE_ID);
		mPage = (FinalRepeatPage)getPage(pageId);
		setRetainInstance(false);
	}
	
	@Override
	public void disable() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_repeat_final,
				container, false);
		((TextView) rootView.findViewById(R.id.comment)).setText(mPage
				.getComment());
		

		button = (Button) rootView.findViewById(R.id.buttonNew);
		button.setText("adicionar");

		button.setOnClickListener(this);

		return rootView;
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
		mPage.addNewRepeat();
		((WizardActivity) getActivity()).changeToPageWith(mPage.getFirstPageKey());
		
	}

}