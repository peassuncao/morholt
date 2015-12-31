package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;

import android.text.TextUtils;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.TextFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class TextPage extends Page {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6022108917671809903L;
	private Integer inputType;
	
	public TextPage(String title, String columnOnDB, String comment, ModelPath modelPath, Integer inputType) {
		super(title, columnOnDB, comment, modelPath);
		this.inputType = inputType;
	}

	@Override
	public PageInterface createFragment() {
		fragment = TextFragment.create(title, inputType);
		return fragment;
	}

	@Override
	public void addReviewItemTo(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(getTitle(), getValue(),
				getKey()));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(getValue());
	}
}
