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
import java.util.HashMap;

import br.fapema.morholt.android.db.DAO;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.MultipleChoiceFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
public class MultipleFixedChoicePage extends SingleFixedChoicePage {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 7756294187846220948L;

	public MultipleFixedChoicePage(String title, String columnOnDB, String comment, ModelPath modelPath) {
        super(title, columnOnDB, comment, modelPath);
    }

    @Override
    public PageInterface createFragment() {
        return MultipleChoiceFragment.create(title);
    }

    public HashMap<String, Boolean> getValues() {
    	return DAO.getValues(mChoices, modelPath);
    }
    
    public void saveValues(HashMap<String, Boolean> hashMap) {
    	DAO.save(hashMap, modelPath);
    }
    
    
    @Override
    public void addReviewItemTo(ArrayList<ReviewItem> dest) {
    	/* TODO
        StringBuilder sb = new StringBuilder();

        ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        if (selections != null && selections.size() > 0) {
            for (String selection : selections) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(selection);
            }
        }

        dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));
    	*/
    }

    @Override
    public boolean isCompleted() {
    	boolean anySelected = false;
    	HashMap<String, Boolean> values = getValues();
    	for (Boolean value : values.values()) {
			if(value) {
				anySelected = true;
				break;
			}
		}
    	return anySelected;
    }
}
