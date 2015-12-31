package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;

import javax.inject.Inject;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.CameraFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class CameraPage extends Page {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8478410124252081793L;

	public CameraPage(String title, String columnOnDB, String comment, ModelPath modelPath) {
		super(title, columnOnDB, comment, modelPath);
	}

	@Override
	public PageInterface createFragment() {
		return CameraFragment.create(title);
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

	public CameraPage setValue(String value) {
		saveValue(value);
		return this;
	}
}
