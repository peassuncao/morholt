package br.fapema.morholt.web.client.gui.basic;

import java.util.Date;

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class MyDateBox extends DateBox {

	public MyDateBox() {
		super();
	}

	public MyDateBox(DatePicker picker, Date date, Format format) {
		super(picker, date, format);
	}
	
	public void setReadOnly(boolean readOnly) {
		getTextBox().setReadOnly(readOnly);
	}
	
	@Override
	public void showDatePicker() {
		if (getTextBox().isReadOnly() == false) {
			super.showDatePicker();
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setReadOnly(!enabled);
	}

}
