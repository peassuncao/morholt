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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.fapema.morholt.android.parser.ModelPath;

/**
 * Represents a list of wizard pages.
 */
public class PageList extends ArrayList<Page> implements PageTreeNode {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8851619946434833072L;

	private ModelPath rootPath;
	
	public PageList(ModelPath rootPath) {
		this.rootPath = rootPath;
    }
    
    public PageList(Page... pages) {
        for (Page page : pages) {
            add(page);
        }
    }

    @Override
    public Page findByKey(String key) {
        for (Page childPage : this) {
            Page found = childPage.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public void flattenCurrentPageSequence(List<Page> dest) {
        for (Page childPage : this) {
            childPage.flattenCurrentPageSequence(dest);
        }
    }

    private List<Page> addPageToList(Page page) {
    	List<Page> pages = new ArrayList<Page>();
    	if(page instanceof BranchPage) {
			List<Branch> branches = ((BranchPage) page).getBranches();
			for (Branch branch : branches) {
				for(Page apage : branch.childPageList) {
					pages.addAll(addPageToList(apage));
				}
			}
    	}
    	else {
    		pages.add(page);
		}
    	return pages;
    }
    
	public void initializeRepeats() {
		List<Page> allPages = new ArrayList<Page>();
		for (Page page : this) {
			allPages.addAll(addPageToList(page));
		}
		
		Set<RepeatPageList> repeatLists = new HashSet<RepeatPageList>();
		for (Page page : allPages) {
			if(page.belongsToRepeat()) {
				repeatLists.add(page.getContainerPageList());
			}
		}
		
		for (RepeatPageList repeatPageList : repeatLists) {
			repeatPageList.pointPagesToIndexZero();
		}
		
	}
	
	public List<String> obtainColumnNameFromPages(Class pageClass) {
		List<String> result = new ArrayList<String>();
		
		for (Page page : this) {
			if(pageClass.isInstance(page)) {
				result.add(page.columnOnDB);
			}
		}
		return result;
	}

	public ModelPath getRootPath() {
		return rootPath;
	}

	public void setRootPath(ModelPath rootPath) {
		this.rootPath = rootPath;
	}
	
	public boolean validate() {
		for (Page page : this) {
			if(!page.isCompleted() && page.isRequired())
				return false;
		}
		return true;
	}
}
