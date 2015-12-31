package br.fapema.morholt.android.wizardpager.wizard.model.repeat;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import br.fapema.morholt.android.db.DAO;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.model.DAOInterface;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.RepeatPageList;
import br.fapema.morholt.android.wizardpager.wizard.model.ReviewItem;
import br.fapema.morholt.android.wizardpager.wizard.ui.FinalRepeatFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.InitialRepeatFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;


public class FinalRepeatPage extends Page {

	private static final long serialVersionUID = -5284445253256241791L;
	
	private RepeatPageList repeatPageList;
	private String firstPageKey;
	public FinalRepeatPage(String title, String comment, RepeatPageList repeatPageList, String firstPageKey) {
		
		super(title, null, comment, null);
		this.repeatPageList = repeatPageList;
		this.firstPageKey = firstPageKey;
	}
	
	public void addNewRepeat() {
		repeatPageList.addRepeat();
	}
	
	@Override
	public PageInterface createFragment() {
		return FinalRepeatFragment.create(title);
	}

	@Override
	public void addReviewItemTo(ArrayList<ReviewItem> dest) {
		//do nothing
	}

	public String getFirstPageKey() {
		return firstPageKey;
	}


}
