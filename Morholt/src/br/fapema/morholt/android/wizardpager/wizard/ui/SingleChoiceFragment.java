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

package br.fapema.morholt.android.wizardpager.wizard.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.model.SingleFixedChoicePage;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import br.fapema.morholt.android.R;

public class SingleChoiceFragment extends MyListFragment implements PageInterface{

	
    private List<String> mChoices;
    private Page mPage;
	private View rootView;

    public static SingleChoiceFragment create(String pageId) {
		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);
        SingleChoiceFragment fragment = new SingleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    public SingleChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Bundle args = getArguments();
		String pageId = (String) args.getSerializable(PAGE_ID);
		mPage = getPage(pageId);
		setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        SingleFixedChoicePage fixedChoicePage = (SingleFixedChoicePage) mPage;
        mChoices = new ArrayList<String>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
    	
        rootView = inflater.inflate(R.layout.fragment_page, container, false); 
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                mChoices));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSaveEnabled(false);

       update();

        return rootView;
    }
    
    @Override
	public void disable() {
    	rootView.findViewById(android.R.id.list).setEnabled(false);
	}
    
    @Override
	public void update() {
    	final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
    	 // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = mPage.getValue();
                for (int i = 0; i < mChoices.size(); i++) {
                    if (mChoices.get(i).equals(selection)) {
                        listView.setItemChecked(i, true);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onViewStateRestored(savedInstanceState);
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPage.saveValue(
                getListAdapter().getItem(position).toString());
        BusProvider.getInstance().post(new PageChangeEvent(mPage));
    }

	@Override
	public boolean validate() {
		if(!mPage.isRequired() || mPage.isCompleted()) {
			return true;
		}
		else {
			showErrors();
			return false;
		}
	}

	@Override
	public void configureKeyboard(boolean hideKeyboard) {
		hideKeyboard(getActivity());
		
	}


	@Override
	public void showErrors() {
		Toast t = Toast.makeText(getActivity(), "Escolha alguma opção.", Toast.LENGTH_SHORT);
		t.show();
	}

	@Override
	public void clearErrors() {
		
	}

	@Override
	public void markAsNotFirstOnResumeAnyomore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUniqueIdentifier() {
		return mPage.getColumnOnDB();
	}
}
