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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.fapema.morholt.android.R;
import com.squareup.otto.Subscribe;

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
import android.widget.Toast;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.WizardActivity;
import br.fapema.morholt.android.db.DAO;
import br.fapema.morholt.android.initial.load.LoadListValues;
import br.fapema.morholt.android.model.BaseActivity;
import br.fapema.morholt.android.parser.XSLReader;
import br.fapema.morholt.android.task.EndpointCallback;
import br.fapema.morholt.android.task.PhotoSendEndpointTask;
import br.fapema.morholt.android.task.SendEndpointTask;
import br.fapema.morholt.android.task.TaskResult;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.basic.ReviewChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.basic.TreeChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.ReviewItem;
import br.fapema.morholt.android.wizardpager.wizard.model.repeat.ReviewRepeatPage;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class ReviewFragment extends MyListFragment implements PageInterface, OnClickListener, EndpointCallback {
	protected static final String ARG_KEY = "key_review";
    private List<ReviewItem> mCurrentReviewItems;
    private ReviewAdapter mReviewAdapter;
    private Button button;
    private Button buttonPhoto;
    private TextView textSending;
	private Button buttonNew;
	
    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewAdapter = new ReviewAdapter();
        BusProvider.getInstance().register(this);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_review, container, false);

        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText(R.string.review);
        titleView.setTextColor(getResources().getColor(R.color.review_green));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(mReviewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        button = (Button) rootView.findViewById(R.id.buttonSendMe);
        button.setOnClickListener(this);

        buttonPhoto = (Button) rootView.findViewById(R.id.buttonSendPhoto);
        buttonPhoto.setOnClickListener(this);
        
        buttonNew = (Button) rootView.findViewById(R.id.buttonNew);
        buttonNew.setOnClickListener(this);
        
        textSending = (TextView) rootView.findViewById(R.id.sendingText);
        textSending.setVisibility(TextView.INVISIBLE);
        onPageTreeChanged(null);
        
        disablePhotoSending();
        return rootView;
    }
    
    public void disable() {
    	disableSimpleSending();
    	disablePhotoSending();
    }
    
    public void disableSimpleSending() {
    	if(MyApplication.valuesAlreadySentToServer()) {
    		button.setEnabled(false); 
    	}
    }
    
   void disablePhotoSending() {
   		if(MyApplication.photosAlreadySentToServer() || !MyApplication.valuesAlreadySentToServer()) {
   			buttonPhoto.setEnabled(false);
   		}
    }
    
//    @Subscribe
    public void onPageTreeChanged(TreeChangeEvent event) {
    	onReviewPageChanged(null);
    }

    @Subscribe
    public void onReviewPageChanged(ReviewChangeEvent reviewChangeEvent) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        
        for (Page page : ((WizardActivity)getActivity()).getCurrentPageSequence()) {
        	if(page.getContainerPageList() == null) {
        		page.addReviewItemTo(reviewItems);
        	}
        	else {
        		if(page instanceof ReviewRepeatPage) {
        			page.addReviewItemTo(reviewItems);
        		}
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
		return false;
	}

	@Override
	public void showErrors() {
	}

	@Override
	public void clearErrors() {
	}

	@Override
	public void markAsNotFirstOnResumeAnyomore() {
	}

	@Override
	public String getUniqueIdentifier() {
		return ARG_KEY;
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.buttonSendPhoto) {
			isReadyPhoto(false);
			new PhotoSendEndpointTask(getActivity(), new PhotoEndpointCallback(), null, MyApplication.getRootContentValues()).execute(getActivity());
		}
		else if(v.getId() == R.id.buttonNew) {
			((BaseActivity)getActivity()).openInitialActivity();
		}
		else {
			isReady(false);
			new SendEndpointTask(getActivity(), this, getMyApplication()).execute(getActivity());
		}
	}
	
	private class PhotoEndpointCallback implements EndpointCallback {

		@Override
		public void endpointCallback(TaskResult result) {
			isReadyPhoto(true);
			if (result.result == SendEndpointTask.RESULT_OK) {
				Toast.makeText(getActivity(), "sucesso no envio de foto", Toast.LENGTH_SHORT).show();
				DAO.save(XSLReader.PHOTO_SENT_TO_SERVER, "1", MyApplication.getRootPath());
				disablePhotoSending();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_AUTHORIZATION) {
				Toast.makeText(getActivity(), "não foi possível enviar, falha de autorização do usuário", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ALLOCATED) {
				Toast.makeText(getActivity(), "não foi possível enviar, este usuário não está alocado neste projeto", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_AUTHENTICATION) {
				Toast.makeText(getActivity(), "não foi possível enviar, falha na autenticação do usuário", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ONLINE) {
				Toast.makeText(getActivity(), "não foi possível enviar, sem acesso à internet", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_SERVER_UNAVAILABLE) {
				Toast.makeText(getActivity(), "não foi possível enviar, servidor indisponível", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(getActivity(), "falha no envio", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void isReady(boolean isReady) {
		button.setEnabled(isReady);
		if(getActivity() != null) {
			((WizardActivity) getActivity()).enablePager(isReady);
		}
		if (isReady) {
			textSending.setVisibility(TextView.INVISIBLE);
		} else {
			textSending.setVisibility(TextView.VISIBLE);
		}
	}
	
	private void isReadyPhoto(boolean isReady) {
		buttonPhoto.setEnabled(isReady);
		((WizardActivity) getActivity()).enablePager(isReady);
		if (isReady) {
			textSending.setVisibility(TextView.INVISIBLE);
		} else {
			textSending.setVisibility(TextView.VISIBLE);
		}
	}

	@Override
	public void update() {
	}

	@Override
	public void endpointCallback(TaskResult result) {
		
		isReady(true);
		if(result.result == SendEndpointTask.RESULT_OK) {
			Toast.makeText(getActivity(), "sucesso", Toast.LENGTH_SHORT).show();
			DAO.save(XSLReader.SENT_TO_SERVER, "1", MyApplication.getRootPath());
			disableSimpleSending();
   			buttonPhoto.setEnabled(true);
		}
		else if (result.result == SendEndpointTask.RESULT_ERROR_AUTHORIZATION) {
			Toast.makeText(getActivity(), "não foi possível enviar, falha de autorização do usuário", Toast.LENGTH_SHORT).show();
		}
		else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ALLOCATED) {
			Toast.makeText(getActivity(), "não foi possível enviar, este usuário não está alocado neste projeto", Toast.LENGTH_SHORT).show();
		}
		else if (result.result == SendEndpointTask.RESULT_ERROR_AUTHENTICATION) {
			Toast.makeText(getActivity(), "não foi possível enviar, falha na autenticação do usuário", Toast.LENGTH_SHORT).show();
		}
		else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ONLINE) {
			Toast.makeText(getActivity(), "não foi possível enviar, sem acesso à internet", Toast.LENGTH_SHORT).show();
		}
		else if (result.result == SendEndpointTask.RESULT_ERROR_SERVER_UNAVAILABLE) {
			Toast.makeText(getActivity(), "não foi possível enviar, acesso ao servidor indisponível", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(getActivity(), "falha no envio", Toast.LENGTH_SHORT).show();
	}
}