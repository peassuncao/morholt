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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import br.fapema.morholt.android.db.DAO;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.basic.SavePageEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

/**
 * Represents a single page in the wizard.
 */
public abstract class Page implements PageTreeNode, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6390258819466116890L;

	/**
     * The key into {@link #getData()} used for wizards with simple (single) values.
     */
    public static final String SIMPLE_DATA_KEY = "_";

    protected ModelCallbacks mCallbacks;

    /**
     * Current wizard values/selections.
     */
    protected String title;
    protected String comment;
    protected boolean mRequired = false;
    protected String mParentKey;
    protected String columnOnDB;
    protected boolean showable = true;
    protected ModelPath modelPath;
    protected PageInterface fragment;
    protected RepeatPageList containerRepeatPageList;
    
    public boolean isShowable() {
		return showable;
	}
    
	public Page(String title, String columnOnDB, String comment, ModelPath modelPath) {
        this.title = title;
        this.columnOnDB = columnOnDB;
        this.comment = comment;
        this.modelPath = modelPath;
        
 //       BusProvider.getInstance().register(this);
    }
	
	
	public void setModelPath(ModelPath modelPath) {
		this.modelPath = modelPath;
		Log.d(getClass().toString() + " setModelPath ", this + " modelpath:" + modelPath);
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " columnOnDB: " + columnOnDB;
	}

    public String getValue() {
        return DAO.getValue(columnOnDB, modelPath);
    }
    
    public Long getLongValue() {
        return DAO.getLongValue(columnOnDB, modelPath);
    }
    
    protected String getValue(String column) {
    	return DAO.getValue(column, modelPath);
    }
    
    public void saveValue(String value) {
    	DAO.save(columnOnDB, value, modelPath); 
    }
    
    public void saveLongValue(Long value) {
    	DAO.saveLong(columnOnDB, value, modelPath); 
    }
    
    protected void saveValue(String value, String column) {
    	DAO.save(column, value, modelPath);
    }
    

    public String getTitle() {
        return title;
    }

    public boolean isRequired() {
        return mRequired;
    }

    void setParentKey(String parentKey) {
        mParentKey = parentKey;
    }

    @Override
    public Page findByKey(String key) {
        return getKey().equals(key) ? this : null;
    }

    @Override
    public void flattenCurrentPageSequence(List<Page> dest) {
    	if(isShowable()) {
    		dest.add(this);
    	}
    }

    public abstract PageInterface createFragment();

    public String getKey() {
        return (mParentKey != null) ? mParentKey + ":" + title : title;
    }

    public abstract void addReviewItemTo(ArrayList<ReviewItem> dest);

    public boolean isCompleted() {
        return true;
    }


    public Page setRequired(boolean required) {
        mRequired = required;
        return this;
    }

	public String getColumnOnDB() {
		return columnOnDB;
	}

	public String getComment() {
		return comment;
	}
	
	public void save() {
		BusProvider.getInstance().post(new SavePageEvent(this));
	}
	
	public PageInterface getFragment() {
		return fragment;
	}

	public boolean belongsToRepeat() {
		return getContainerPageList() != null;
	}
	
	public boolean belongsToRepeatAndHasSameIndex(ModelPath repeatPath) {
		if (!belongsToRepeat()) return false;
		
		PageList pageList = getContainerPageList();
		Page page = pageList.get(0);
		return page.getModelPath().equals(repeatPath);
		
	}
	public RepeatPageList getContainerPageList() {
		return containerRepeatPageList;
	}

	public Page setContainerPageList(RepeatPageList containerPageList) {
		this.containerRepeatPageList = containerPageList;
		return this;
	}

	public ModelPath getModelPath() {
		return modelPath;
	}
	
}
