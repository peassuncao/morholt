package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.BarCodeFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class BarCodePage extends Page {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3066130232137389240L;

	public BarCodePage(String title, String columnOnDB, String comment, ModelPath modelPath) {
		super(title, columnOnDB, comment, modelPath);
	}

	@Override
	public PageInterface createFragment() {
		return BarCodeFragment.create(title);
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

	public BarCodePage setValue(String value) {
		saveValue(value);
		return this;
	}
}
