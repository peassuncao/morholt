package br.fapema.morholt.android.wizardpager.wizard.model.repeat;

import java.util.ArrayList;

import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.RepeatPageList;
import br.fapema.morholt.android.wizardpager.wizard.model.ReviewItem;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;
import br.fapema.morholt.android.wizardpager.wizard.ui.repeat.ReviewRepeatFragment;

public class ReviewRepeatPage extends Page {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6022108917671809903L;

	private RepeatPageList repeatPageList;
	public ReviewRepeatPage(String title, String comment, ModelPath modelPath, RepeatPageList repeatPageList) {
		super(title, null, comment, modelPath); 
		setContainerPageList(repeatPageList);
	}

	@Override
	public PageInterface createFragment() {
		fragment = ReviewRepeatFragment.create(title);
		return fragment;
	}

	@Override
	public void addReviewItemTo(ArrayList<ReviewItem> dest) {
			dest.add(new ReviewItem(getTitle(), getTitle(),  
				getKey()));
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

}
