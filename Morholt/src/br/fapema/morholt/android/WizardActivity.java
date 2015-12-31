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

package br.fapema.morholt.android;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import br.fapema.morholt.android.collectpages.interfaces.Validetable;
import br.fapema.morholt.android.model.BaseActivity;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.basic.FixedFragmentStatePagerAdapter;
import br.fapema.morholt.android.wizardpager.wizard.basic.MyViewPager;
import br.fapema.morholt.android.wizardpager.wizard.basic.ReviewChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.basic.TreeChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.model.ModelCallbacks;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.model.PageList;
import br.fapema.morholt.android.wizardpager.wizard.ui.ReviewFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import br.fapema.morholt.android.R;
import com.squareup.otto.Subscribe;

import dagger.ObjectGraph;

public class WizardActivity extends BaseActivity implements
        ReviewFragment.Callbacks,
        ModelCallbacks {
 
	private MyViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    
    @Inject
    protected PageList pageList; 
    
    
    
    private boolean mConsumePageSelectedEvent;
    private List<Page> mCurrentPageSequence;
    
    boolean shouldValidateBeforeAdvancing = true;
	boolean showValidationErrorsAfterFirstEdition = false;
	
	private ObjectGraph activityGraph;

	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        saveValuesAsTemp();
		
        Log.d(this + "onCreate", "activityGraph null:" + String.valueOf(activityGraph == null));
		
        if(activityGraph == null) {
        	MyApplication application = getMyApplication();
        	activityGraph = application.getApplicationGraph(); 
        	activityGraph.inject(this);
        }
		
        BusProvider.getInstance().register(this);

    	Log.d(getClass().toString() + ".onCreate savedInstanceState ", String.valueOf(savedInstanceState));
        
        setContentView(R.layout.activity_wizard);
        
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (MyViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(getMyApplication().getCurrentCollectPage());
        BusProvider.getInstance().post(new TreeChangeEvent());
        
        logComparationValuesWithTemp();
        
        String title = ProjectInfo.getProjectName(); 
        setTitle(title);
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void changeToPageWith(String key) {
    	saveValuesAsTemp();
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
              //  mPager.getAdapter().notifyDataSetChanged();
             //   mPager.refreshDrawableState();
                View childAt = mPager.getChildAt(i);
                mPager.setCurrentItem(i);
               
                
                getMyApplication().setCurrentCollectPage(i);
             //   refresh();
                break;
            }
        }
        logComparationValuesWithTemp();
    }

    
    
    public void refresh() {
    	
    }
    
    public Page getPage(String pageId) {
    	
    	for (int i = getCurrentPageSequence().size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(pageId)) {
                return mCurrentPageSequence.get(i);
            }
    	}
    	throw new RuntimeException("Page not found for id: " + pageId);
    }

    private void logCurrentPageSequence(String callingMethod) {
    	Log.d(getClass().toString() + ".logCurrentPageSequence from " + callingMethod +":  ", "start");
    	for (Page page : mCurrentPageSequence) {
    		Log.d(getClass().toString() + ".onPageDataChanged:  page ", page.toString());
		}
    	Log.d(getClass().toString() + ".logCurrentPageSequence :  ", "end");
    }
    
    public void enablePager(Boolean enabled) {
    	mPager.enableSwipe(enabled);
    }
    
    
	@Override
    @Subscribe
    public void onPageDataChanged(PageChangeEvent pageEvent) {
		
		saveValuesAsTemp();
		
    	Page page = pageEvent.page;
    	Log.d(getClass().toString() + ".onPageDataChanged: ", page.toString());
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
            	Log.d(getClass().toString() + ".onPageDataChanged:  ", "mPagerAdapter.notifyDataSetChanged()");
                mPagerAdapter.notifyDataSetChanged();
            }
        }
        BusProvider.getInstance().post(new ReviewChangeEvent(mCurrentPageSequence));
        
        logComparationValuesWithTemp();
    }
    
    @Subscribe 
    public void onPageTreeChanged(TreeChangeEvent event) {
		saveValuesAsTemp();
		
    	if(pageList == null) return;
        mCurrentPageSequence =  getCurrentPageSequence();
        logCurrentPageSequence("onPageTreeChanged");
        recalculateCutOffPage();
    	Log.d(getClass().toString() + ".onPageTreeChanged  ", "");
        BusProvider.getInstance().post(new ReviewChangeEvent(mCurrentPageSequence));
        mPagerAdapter.notifyDataSetChanged();
        
        logComparationValuesWithTemp();
    }
    
    private boolean recalculateCutOffPage() {
    	
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i + 1 ; // this +1 is to be able to validate
                if(cutOffPage == mCurrentPageSequence.size()) cutOffPage--;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
        	Log.d(getClass().toString() + ".recalculateCutOffPage :  ", "true - cutOffPage: " + mPagerAdapter.getCutOffPage());
        	return true;
        }

    	Log.d(getClass().toString() + ".recalculateCutOffPage :  ", "false - cutOffPage:" + mPagerAdapter.getCutOffPage());
        return false; 
    }

    public boolean isCurrentPage(String pageKey) {
    	int currentPage = getCurrentPage();
    	return mCurrentPageSequence.get(currentPage).getKey().equals(pageKey);
    }
    
    private int getCurrentPage() {
    	return mPager.getCurrentItem();
    }
    
    public class MyPagerAdapter extends FixedFragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

		SparseArray<PageInterface> registeredFragments = new SparseArray<PageInterface>();
		
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public String getTag(int position) {
        	if(position >= mCurrentPageSequence.size()) {
        		return "review";
        	}
        	else {
        		return mCurrentPageSequence.get(position).getKey();
        	}
        }
        
        @Override
        public Fragment getItem(int i) {
        	Log.d(getClass().toString() + ".getItem: ", String.valueOf(i));
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }
         //   if(mCurrentPageSequence.get(i).getFragment() != null) {
          //  	return mCurrentPageSequence.get(i).getFragment();
           // }
            //else {
            	return (Fragment) mCurrentPageSequence.get(i).createFragment(); 
            //}
        }

        @Override
        public int getItemPosition(Object object) {
        	Log.d(getClass().toString() + ".getItemPosition: ", object.toString());
            if (object == mPrimaryItem) {
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        	Log.d(getClass().toString() + ".setCutOffPage: ", String.valueOf(mCutOffPage));
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
        
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
        	Log.d(getClass().toString() + ".instantiateItem position: ", String.valueOf(position));
			PageInterface fragment = (PageInterface) super.instantiateItem(container,
					position);
			registeredFragments.put(position, fragment);
			return fragment;
		}
		
		@Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
        	Log.d(getClass().toString() + ".destroyItem position: ", String.valueOf(position));
	        registeredFragments.remove(position);
	        super.destroyItem(container, position, object);
	    }

	    public PageInterface getRegisteredFragment(int position) {
	    	PageInterface pageInterface = registeredFragments.get(position);
	    	if(pageInterface == null) {
	    		pageInterface = (PageInterface) mPagerAdapter.getItem(position);
			}
	        return pageInterface;
	    }
	    
    }
    
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageSelected(int numPageSelected) {
        	Log.d(getClass().toString() + ".onPageSelected numPageSelected: ", String.valueOf(numPageSelected));
        	saveValuesAsTemp();
        	
            if (mConsumePageSelectedEvent) {
                mConsumePageSelectedEvent = false;
                return;
            }
            
			int currentCollectPage = getMyApplication().getCurrentCollectPage();
			PageInterface currentView = (PageInterface) mPagerAdapter
					.getRegisteredFragment(currentCollectPage);
			
			if (currentCollectPage == numPageSelected) {
				return;
			} else if (numPageSelected < currentCollectPage) {
				currentView.clearErrors();
				getMyApplication().setCurrentCollectPage(numPageSelected);
			} else {
				boolean valid = currentView.validate();
				if (valid || !shouldValidateBeforeAdvancing) {	
					getMyApplication().setCurrentCollectPage(numPageSelected);
					currentView.clearErrors();
				} else {
					mPager.setCurrentItem(currentCollectPage);
				}
				getMyApplication().addToViewsAlreadyValidating(mPagerAdapter
						.getRegisteredFragment(currentCollectPage).getUniqueIdentifier());
			}
			// if it´s not on the same page after failing validation
			if (showValidationErrorsAfterFirstEdition) {
				showValidationErrors();
			}
			if(MyApplication.valuesAlreadySentToServer()) {
				disableFragment();
			}
			configureKeyboard();
			System.out.println("onPageSelected + " + numPageSelected);
			logComparationValuesWithTemp();
		}
		
		private void disableFragment() {
			PageInterface fragment = (PageInterface) mPagerAdapter
					.getRegisteredFragment(getMyApplication().getCurrentCollectPage());
			fragment.disable();
		}

		private void configureKeyboard() {
			boolean hideKeyboard = getMyApplication().isViewAlreadyValidating(mPagerAdapter
					.getRegisteredFragment(getMyApplication().getCurrentCollectPage()).getUniqueIdentifier());
			PageInterface viewToBeShown = (PageInterface) mPagerAdapter
					.getRegisteredFragment(getMyApplication().getCurrentCollectPage());
			
			viewToBeShown.configureKeyboard(hideKeyboard);
		}

		private void showValidationErrors() {
			Validetable fragment = (Validetable) mPagerAdapter
					.getRegisteredFragment(getMyApplication().getCurrentCollectPage());
			if(getMyApplication().isViewAlreadyValidating(fragment.getUniqueIdentifier())) {
				fragment.validate();
			}
		}
	}

	
	public List<Page> getCurrentPageSequence() {
		if(mCurrentPageSequence == null) {
			MyApplication application = (MyApplication) getApplication();
			activityGraph = application.getApplicationGraph(); 
        	activityGraph.inject(this);
        	
        	 mCurrentPageSequence = new ArrayList<Page>();
        	 pageList.flattenCurrentPageSequence(mCurrentPageSequence);
		}
		return mCurrentPageSequence;
	}
	
	

	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	

}