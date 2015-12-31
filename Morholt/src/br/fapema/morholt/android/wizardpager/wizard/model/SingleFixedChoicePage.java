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
import java.util.Arrays;

import android.text.TextUtils;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.SingleChoiceFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

/**
 * A page offering the user a number of mutually exclusive choices.
 */
public class SingleFixedChoicePage extends Page {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6007732885407529794L;
	protected ArrayList<String> mChoices = new ArrayList<String>();

    public SingleFixedChoicePage(String title, String columnOnDB, String comment, ModelPath modelPath) {
        super(title, columnOnDB, comment, modelPath);
    }

    @Override
    public PageInterface createFragment() {
        return SingleChoiceFragment.create(title);
    }

    public String getOptionAt(int position) {
        return mChoices.get(position);
    }

    public int getOptionCount() {
        return mChoices.size();
    }

    @Override
    public void addReviewItemTo(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), getValue(), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(getValue());
    }

    public SingleFixedChoicePage setChoices(String... choices) {
    	if(choices == null) return this;
        mChoices.addAll(Arrays.asList(choices));
        return this;
    }

    public SingleFixedChoicePage setValue(String value) {
        setValue(value);
        return this;
    }
}
