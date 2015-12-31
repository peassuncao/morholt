package br.fapema.morholt.android.wizardpager.wizard.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import br.fapema.morholt.android.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.helper.StringUtils;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;
import android.widget.TextView;

public class DateFragment extends BugFragment implements PageInterface{
	
	private Page mPage; 

	protected DatePicker dateSpinner;
	
	public static DateFragment create(String pageId) {
		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);
		
		DateFragment textFragment = new DateFragment();
		textFragment.setArguments(args);     
		return textFragment;
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
		View rootView = inflater.inflate(R.layout.fragment_page_date,
				container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());
//		((TextView) rootView.findViewById(R.id.comment)).setText(mPage
//				.getComment());

		dateSpinner = (DatePicker) rootView.findViewById(R.id.dateSpinner);
		dateSpinner.setSaveEnabled(false);
		
		update();
		return rootView;
	}
	
	@Override
	public void disable() {
		dateSpinner.setEnabled(false);
	}
	
	@Override
	public void update() {
		if(StringUtils.isNotBlank(mPage.getValue())) { 
			String[] date = mPage.getValue().split("/");
			dateSpinner.init(Integer.valueOf(date[0]),Integer.valueOf( date[1])-1, Integer.valueOf(date[2]), new MyOnDateChangeListener());
		}
		else {
			Calendar c = Calendar.getInstance();
			int mYear = c.get(Calendar.YEAR);
			int mMonth = c.get(Calendar.MONTH);
			int mDay = c.get(Calendar.DAY_OF_MONTH);
			dateSpinner.init(mYear, mMonth, mDay, new MyOnDateChangeListener());
			String month = (mMonth+1)  >= 10 ? ""+ (mMonth+1) :   "0"+ (mMonth+1) ;
			String day = mDay  >= 10 ? ""+mDay : "0"+mDay;
			mPage.saveValue(mYear+"/"+month+"/"+day);
		}
	}

	private final class MyOnDateChangeListener implements OnDateChangedListener {
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			String month = (monthOfYear+1)  >= 10 ? ""+ (monthOfYear+1)  :   "0"+ (monthOfYear+1) ;
			String day = dayOfMonth  >= 10 ? ""+dayOfMonth : "0"+dayOfMonth;
			mPage.saveValue(year+"/"+month+"/"+day);

			BusProvider.getInstance().post(new PageChangeEvent(mPage));
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public boolean validate() {
		if(!mPage.isRequired() || mPage.isCompleted()) { // TODO put this on superclass
			clearErrors();
			return true;
		}
		else {
			showErrors();
			return false;
		}
	}

	@Override
	public void showErrors() {
	}

	@Override
	public void clearErrors() {
		// do nothing
	}

	@Override
	public void markAsNotFirstOnResumeAnyomore() {
		super.markAsNotFirstOnResumeAnyomore();
		
	}

	@Override
	public void configureKeyboard() {
		hideKeyboard(getActivity());
	}
	
	@Override
	public String toString() {
		return getClass().getCanonicalName() + "-> " + mPage.getValue();
	}

	@Override
	public String getUniqueIdentifier() {
		return mPage.getColumnOnDB();
	}
}