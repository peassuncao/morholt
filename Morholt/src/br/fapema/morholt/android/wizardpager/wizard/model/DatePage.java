package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.google.api.client.util.StringUtils;

import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.DateFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class DatePage extends Page {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9091188439868205820L;

	public DatePage(String title, String columnOnDB, String comment, ModelPath modelPath) {
		super(title, columnOnDB, comment, modelPath);
	}

	@Override
	public PageInterface createFragment() {
		return DateFragment.create(title);
	}

	@Override
	public void addReviewItemTo(ArrayList<ReviewItem> dest) {
		
		String value = obtainValue();
		dest.add(new ReviewItem(getTitle(), value,
				getKey()));
	}

	private String obtainValue() {
		if(br.fapema.morholt.android.helper.StringUtils.isBlank(getValue())) return "";
		//localizes date 'year/month/day'
		
		String[] date = getValue().split("/");
		Locale locale = MyApplication.getAppContext().getResources().getConfiguration().locale;
		GregorianCalendar gregorianCalendar = new GregorianCalendar(locale);
		gregorianCalendar.set(Integer.valueOf(date[0]),Integer.valueOf( date[1])-1, Integer.valueOf(date[2]));
		
		int style = java.text.DateFormat.MEDIUM;
		java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(style, locale);
		String value = dateFormat.format(gregorianCalendar.getTime());
		
		return value;
	}

	@Override
	public boolean isCompleted() {
		return true;   
	}
}
