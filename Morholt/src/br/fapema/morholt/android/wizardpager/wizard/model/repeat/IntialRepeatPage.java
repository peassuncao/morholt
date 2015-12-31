package br.fapema.morholt.android.wizardpager.wizard.model.repeat;

import java.util.ArrayList;

import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.RepeatPageList;
import br.fapema.morholt.android.wizardpager.wizard.model.ReviewItem;
import br.fapema.morholt.android.wizardpager.wizard.ui.InitialRepeatFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;


public class IntialRepeatPage extends Page {

	private static final long serialVersionUID = -5284445253256241791L;
	
	private RepeatPageList repeatPageList;
	public IntialRepeatPage(String title, String comment, RepeatPageList repeatPageList) {
		
		super(title, null, comment, null);
		this.repeatPageList = repeatPageList;
	}
	
	public void addNewRepeat() {
		repeatPageList.addToValues();
	}
	
	@Override
	public PageInterface createFragment() {
		return InitialRepeatFragment.create(title);
	}

	@Override
	public void addReviewItemTo(ArrayList<ReviewItem> dest) {
		//do nothing
	}

}
