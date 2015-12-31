package br.fapema.morholt.android.wizardpager.wizard.ui.repeat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.wizardpager.wizard.basic.ReviewChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.basic.TreeChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.RepeatPageList;
import br.fapema.morholt.android.wizardpager.wizard.model.ReviewItem;
import br.fapema.morholt.android.wizardpager.wizard.ui.MyListFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import br.fapema.morholt.android.R;


public class ReviewRepeatFragment extends MyListFragment implements PageInterface, OnClickListener {

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
		protected static final String ARG_KEY = "key_review";

	    private List<ReviewItem> mCurrentReviewItems;

	    private ReviewAdapter mReviewAdapter;
	    
	    private Button buttonPrevious;
	    private Button buttonNext;
	    private TextView textSending;
	    private Page mPage;
	    private TextView selectedPageNumberOverTotalText;
	    
		public static ReviewRepeatFragment create(String pageId) {  
			Bundle args = new Bundle();
			args.putSerializable(PAGE_ID, pageId);

			ReviewRepeatFragment reviewRepeat = new ReviewRepeatFragment();
			reviewRepeat.setArguments(args);

			return reviewRepeat;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			String pageId = (String) args.getSerializable(PAGE_ID);
			mPage = getPage(pageId);
			setRetainInstance(false);
	        mReviewAdapter = new ReviewAdapter();
		}

	    
	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	   //     BusProvider.getInstance().unregister(this);
	    }

	    

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View rootView = inflater.inflate(R.layout.fragment_repeat_review, container, false);

	        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
	        titleView.setText(R.string.review);
	        titleView.setTextColor(getResources().getColor(R.color.review_green));
	        
	        selectedPageNumberOverTotalText = (TextView) rootView.findViewById(R.id.selectedPageNumberOverTotalText);
	        
	        updateSelectedPageNumberOverTotalText();

	        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
	        setListAdapter(mReviewAdapter);
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
	        buttonPrevious = (Button) rootView.findViewById(R.id.buttonPrevious);
	        buttonPrevious.setOnClickListener(this);

	        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
	        buttonNext.setOnClickListener(this);
	        
	        onPageTreeChanged(null);
	        return rootView;
	    }
	    
	    private void updateSelectedPageNumberOverTotalText() {
			RepeatPageList repeatPageList = (RepeatPageList) mPage.getContainerPageList();
	        selectedPageNumberOverTotalText.setText(repeatPageList.getSelectedPageNumberOverTotalText());
	    }
	    
//	    @Subscribe
	    public void onPageTreeChanged(TreeChangeEvent event) {
	    	onReviewPageChanged(null);
	    }

	  //  @Subscribe
	    public void onReviewPageChanged(ReviewChangeEvent reviewChangeEvent) {
	        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
	        

			RepeatPageList repeatPageList = (RepeatPageList) mPage.getContainerPageList();
			
	        for (Page page : repeatPageList) {
	        		if(!page.equals(mPage)) {
	        			page.addReviewItemTo(reviewItems);
	        		}
	        }
	        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
	            @Override
	            public int compare(ReviewItem a, ReviewItem b) {
	                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
	            }
	        });
	        mCurrentReviewItems = reviewItems;

	        if (mReviewAdapter != null) {
	            mReviewAdapter.notifyDataSetInvalidated();
	        }
	    }

	    @Override
	    public void onListItemClick(ListView l, View v, int position, long id) {

			//RepeatPageList repeatPageList = (RepeatPageList) mPage.getContainerPageList();
			//repeatPageList.refreshPages();
	    	((WizardActivity)getActivity()).changeToPageWith(mCurrentReviewItems.get(position).getPageKey());
	    }

	    public interface Callbacks {
	        void changeToPageWith(String pageKey);
	        public List<Page> getCurrentPageSequence();
	    }

	    private class ReviewAdapter extends BaseAdapter {
	        @Override
	        public boolean hasStableIds() {
	            return true;
	        }

	        @Override
	        public int getItemViewType(int position) {
	            return 0;
	        }

	        @Override
	        public int getViewTypeCount() {
	            return 1;
	        }

	        @Override
	        public boolean areAllItemsEnabled() {
	            return true;
	        }

	        @Override
	        public Object getItem(int position) {
	            return mCurrentReviewItems.get(position);
	        }

	        @Override
	        public long getItemId(int position) {
	            return mCurrentReviewItems.get(position).hashCode();
	        }

	        @Override
	        public View getView(int position, View view, ViewGroup container) {
	            LayoutInflater inflater = LayoutInflater.from(getActivity());
	            View rootView = inflater.inflate(R.layout.list_item_review, container, false);

	            ReviewItem reviewItem = mCurrentReviewItems.get(position);
	            String value = reviewItem.getDisplayValue();
	            if (TextUtils.isEmpty(value)) {
	                value = "(vazio)";
	            }
	            ((TextView) rootView.findViewById(android.R.id.text1)).setText(reviewItem.getTitle());
	            ((TextView) rootView.findViewById(android.R.id.text2)).setText(value);
	            return rootView;
	        }

	        @Override
	        public int getCount() {
	            return mCurrentReviewItems == null ? 0 : mCurrentReviewItems.size();
	        }
	    }

		@Override
		public void configureKeyboard(boolean hideKeyboard) {
			hideKeyboard(getActivity());
			
		}

		@Override
		public boolean validate() {
			return true;
		}

		@Override
		public void showErrors() {
			// do nothing
		}

		@Override
		public void clearErrors() {
			// do nothing
		}

		@Override
		public void markAsNotFirstOnResumeAnyomore() {
			// do nothing			
		}
		


		@Override
		public String getUniqueIdentifier() {
			return ARG_KEY;
		}

		
		
		@Override
		public void onClick(View v) {
			RepeatPageList repeatPageList = (RepeatPageList) mPage.getContainerPageList();
			
			if(v.getId() == R.id.buttonPrevious) {
				repeatPageList.previousRepeat();
			}
			else {
				repeatPageList.nextRepeat();
			}
			
			updateSelectedPageNumberOverTotalText();
		
			//	((WizardActivity)getActivity()).
			
			onReviewPageChanged(null);
		}

		@Override
		public void update() {
			// do nothing
		}

		@Override
		public void disable() {
		}
	}