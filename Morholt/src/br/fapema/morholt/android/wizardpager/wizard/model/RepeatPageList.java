package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;
import java.util.List;

import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.parser.PathPair;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;
import br.fapema.morholt.android.wizardpager.wizard.model.repeat.FinalRepeatPage;
import br.fapema.morholt.android.wizardpager.wizard.model.repeat.IntialRepeatPage;
import br.fapema.morholt.android.wizardpager.wizard.model.repeat.ReviewRepeatPage;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class RepeatPageList extends PageList {
	
	private static final long serialVersionUID = 9088992534128278584L;
	private int currentRepeat = 0; //TODO maybe this won´t work for remove
	private ModelPath parentModelPath;
	private boolean joinWithParent;
	private String kind;
	
	/**
	 * 
	 * @param kind
	 * @param joinWithParent true if at the time of sending to the server, this list is joined with the parent (no normalization)
	 * @param parentPath
	 * @param firstPageKey
	 * @param initialRepeatPageTitle
	 * @param initialRepeatPageComment
	 * @param finalRepeatPageTitle
	 * @param finalRepeatPageComment
	 * @param pages
	 */
	public RepeatPageList(String kind, boolean joinWithParent, ModelPath parentPath, String firstPageKey, String initialRepeatPageTitle, String initialRepeatPageComment, String finalRepeatPageTitle, String finalRepeatPageComment, Page ...pages) {
		this.parentModelPath = parentPath;
		this.kind = kind;
		this.joinWithParent = joinWithParent;
		initializeValues(); //this is called very on the beggining, so not even the first button was pressed. not very useful to set application values, as they will get lost/replaced
		
		if(initialRepeatPageTitle != null) {
			add (new IntialRepeatPage(initialRepeatPageTitle, initialRepeatPageComment, this));
		}

        for (Page page : pages) {
            add(page);
            page.setContainerPageList(this);
        }
        

		add (new FinalRepeatPage(finalRepeatPageTitle, finalRepeatPageComment, this, firstPageKey)); // todo use basemodel and repeat dao to add to base dao content values the new ones
		add( new ReviewRepeatPage("repeatReviewTit", "comentarioReviewRepeat", parentModelPath.add(PathPair.c(kind, currentRepeat)), this)); 
		updatePagesModelPath(parentModelPath.add(PathPair.c(kind, currentRepeat)));
	}

	
	public void nextRepeat() {
		if(currentRepeat + 1 < getRepeatValuesSize()) {
			currentRepeat++;
			updatePagesModelPath(parentModelPath.add(PathPair.c(kind, currentRepeat)));
//			talvez mudar o framento aqui tb pra que depois venha certo no restore ou alterar restore
			
	//		ou procurr como fazer change page
		}
	}
	
	public void addRepeat(){
		currentRepeat++;
		addToValues();
		updatePagesModelPath(parentModelPath.add(PathPair.c(kind, currentRepeat)));
	}
	
	public void previousRepeat() {
		if (currentRepeat > 0) {
			currentRepeat--;
			updatePagesModelPath(parentModelPath.add(PathPair.c(kind, currentRepeat)));
		}
	}
	
	/**
	 *  returns the selected page/ total number of pages
	 */
	public String getSelectedPageNumberOverTotalText() {
		return (currentRepeat+1) + "/" + getRepeatValuesSize();
	}
	
	public String getFirstPageKey() {
		//return get(0).getKey();
		return "GPS"; //TODO
	}

	public int getRepeatValuesSize() {
		Values parentValues = MyApplication.getCurrentContentValues(parentModelPath);
		List<Values> valuesList = parentValues.getAsValuesList(kind);
		if(valuesList == null) { 
			return 0;
		}
		else {
			return valuesList.size();
		}
	}
	
	public List<Values> getRepeatValues() {
		Values parentValues = getParentValues();
		List<Values> valuesList = parentValues.getAsValuesList(kind);
		if(valuesList == null) {
			valuesList = new ArrayList<Values>();
			parentValues.put(kind, valuesList);
		}
		return valuesList;
	}
	
	public Values getParentValues() {
		return MyApplication.getCurrentContentValues(parentModelPath);
	}
	
	private void initializeValues() {
		currentRepeat = 0;
		List<Values> repeatValues = getRepeatValues();
		if (repeatValues.isEmpty()) {
			addToValues();
		}
	}
	
	public void pointPagesToIndexZero() {
		currentRepeat = 0;
		for(int i=0; i<size(); i++) {
			Page page = get(i);
			page.getModelPath().setIndex(currentRepeat);
		}
	}
	
	public void addToValues() {
		List<Values> repeatValues = getRepeatValues();
		Values newValues = new Values();
		repeatValues.add(newValues);
	}
	
	public void refreshPages() {
		for(int i=0; i<size(); i++) {
			Page page = get(i);
			PageInterface fragment = page.getFragment();
			if(fragment != null) { 
				fragment.update();
			}
		}
	}
	
	private void updatePagesModelPath(ModelPath modelPath) {
		for(int i=0; i<size(); i++) {
			Page page = get(i);
			page.setModelPath(modelPath);
			PageInterface fragment = page.getFragment();
			if(fragment != null) { 
				fragment.update();
			}
		}
	}

	public Page[] toArray() {
		Page[] pages = new Page[size()];
		for (int i = 0; i < size(); i++) {
			pages[i] = get(i);
		}
		return pages;
	}


	public boolean isJoinWithParent() {
		return joinWithParent;
	}
	
}
