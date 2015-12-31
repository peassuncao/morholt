/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.basic.TreeChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.SingleChoiceFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

/**
 * A page representing a branching point in the wizard. Depending on which choice is selected, the
 * next set of steps in the wizard may change.
 */
public class BranchPage extends SingleFixedChoicePage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7224527893152501014L;
	private List<Branch> mBranches = new ArrayList<Branch>();
	private boolean hasValue = true;
	
    public BranchPage(String title, String columnOnDB, String comment, ModelPath modelPath) {
        super(title, columnOnDB, comment, modelPath);
    }

    @Override
    public Page findByKey(String key) {
        if (getKey().equals(key)) {
            return this;
        }

        for (Branch branch : mBranches) {
            Page found = branch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public void flattenCurrentPageSequence(List<Page> destination) {
        super.flattenCurrentPageSequence(destination);
        if(hasValue) {
	        for (Branch branch : mBranches) {
	            if (branch.choice.equals(getValue())) {
	                branch.childPageList.flattenCurrentPageSequence(destination);
	                break;
	            }
	        }
        }
        else {
        	if(!showable) {
        		mBranches.get(0).childPageList.flattenCurrentPageSequence(destination);
        	}
        }
    }

    public BranchPage setShowable(boolean showable) {
		this.showable = showable;
		return this;
	}
    
    /**
     * @param string
     * @return
     */
    public BranchPage setData(String string) {
    	saveValue(string);
    	return this;
    }
    
    public BranchPage addBranch(Branch branch) {
    	mBranches.add(branch);
    	return this;
    }
    
    public BranchPage addBranch(String choice, Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey(choice);
        }
        mBranches.add(new Branch(choice, childPageList));
        return this;
    }
    
    public BranchPage addBranch(String choice) {
        mBranches.add(new Branch(choice, new PageList()));
        return this;
    }

    @Override
    public PageInterface createFragment() {
        return SingleChoiceFragment.create(title);
    }

    public String getOptionAt(int position) {
        return mBranches.get(position).choice;
    }
    
    @Override
    public void saveValue(String value) {
    	super.saveValue(value);
    	BusProvider.getInstance().post(new TreeChangeEvent());
    }

    public int getOptionCount() {
        return mBranches.size();
    }

    @Override
    public void addReviewItemTo(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), getValue(), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(getValue());
    }

    
    public BranchPage setValue(String value) {
        saveValue(value);
        return this;
    }

	public boolean getHasValue() {
		return hasValue;
	}

	public BranchPage setHasValue(boolean hasValue) {
		this.hasValue = hasValue;
		return this;
	}

	public List<Branch> getBranches() {
		return mBranches;
	}

}
